package com.hackathon.service;

import com.hackathon.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Epik 3 — Sprint Review ve Dashboard.
 * Jira verisinden kural tabanlı metrikler hesaplar.
 * Sprint review için AiService.generateReview()'ı besler.
 */
@Service
public class SprintService {

    private final JiraGateway jiraService;
    private final AiService aiService;

    public SprintService(JiraGateway jiraService, AiService aiService) {
        this.jiraService = jiraService;
        this.aiService = aiService;
    }

    /** GET /api/sprint/{sprintId}/dashboard */
    public SprintDashboardDto getDashboard(int sprintId) {
        SprintDto sprint = findSprint(sprintId);
        List<IssueDto> issues = jiraService.getSprintIssues(sprintId);

        int planned = sumPoints(issues);
        int completed = issues.stream().filter(IssueDto::isResolved)
                .mapToInt(i -> safe(i.getStoryPoints())).sum();

        double deviation = planned == 0 ? 0 :
                Math.round((double) (completed - planned) / planned * 1000) / 10.0;

        Map<String, Integer> breakdown = Map.of(
                "TODO", (int) issues.stream().filter(i -> "TODO".equals(i.getStatusCategory())).count(),
                "IN_PROGRESS", (int) issues.stream().filter(i -> "IN_PROGRESS".equals(i.getStatusCategory())).count(),
                "DONE", (int) issues.stream().filter(IssueDto::isResolved).count()
        );

        List<SprintDashboardDto.BurndownPoint> burndown =
                buildBurndown(sprint, issues, planned);

        String name = sprint != null ? sprint.getName() : "Sprint " + sprintId;
        String start = sprint != null ? sprint.getStartDate() : null;
        String end = sprint != null ? sprint.getEndDate() : null;

        return new SprintDashboardDto(
                sprintId, name, start, end,
                planned, completed,
                issues.size(),
                (int) issues.stream().filter(IssueDto::isResolved).count(),
                deviation,
                breakdown,
                burndown
        );
    }

    /** GET /api/sprint/{sprintId}/review — AI demo raporu */
    public SprintReview getReview(int sprintId) {
        SprintDto sprint = findSprint(sprintId);
        List<IssueDto> issues = jiraService.getSprintIssues(sprintId);
        String context = buildReviewContext(sprintId, sprint, issues);
        return aiService.generateReview(sprintId, context);
    }

    /** GET /api/sprint/{sprintId}/carryover (BONUS) */
    public CarryoverDto getCarryover(int sprintId) {
        SprintDto sprint = findSprint(sprintId);
        List<IssueDto> issues = jiraService.getSprintIssues(sprintId);

        List<IssueDto> notDone = issues.stream()
                .filter(i -> !i.isResolved()).toList();

        String sprintName = sprint != null ? sprint.getName() : "Sprint " + sprintId;

        List<CarryoverDto.CarryoverItem> items = notDone.stream()
                .map(i -> new CarryoverDto.CarryoverItem(
                        i.getKey(),
                        i.getSummary(),
                        i.getStoryPoints(),
                        1,
                        sprintName,
                        "Sprint sonunda tamamlanamadı."
                )).toList();

        int totalPoints = notDone.stream().mapToInt(i -> safe(i.getStoryPoints())).sum();

        return new CarryoverDto(sprintId, notDone.size(), totalPoints, items);
    }

    /** GET /api/sprint/{sprintId}/health (BONUS) — 1-100 sağlık skoru */
    public SprintHealthDto getHealth(int sprintId) {
        SprintDashboardDto dash = getDashboard(sprintId);
        CarryoverDto carryover = getCarryover(sprintId);
        VelocityDto velocity = buildBasicVelocity(sprintId, dash);

        // Faktör 1: Tamamlanma Oranı (%40)
        double completionRate = dash.getPlannedPoints() == 0 ? 100 :
                (double) dash.getCompletedPoints() / dash.getPlannedPoints() * 100;

        // Faktör 2: Carryover oranı (%25)
        double carryoverRate = dash.getPlannedPoints() == 0 ? 100 :
                (1 - (double) carryover.getCarriedOverPoints() / Math.max(dash.getPlannedPoints(), 1)) * 100;

        // Faktör 3: Velocity tutarlılığı (%20) — sabit baz, varsa gerçek
        double velocityConsistency = 70;

        // Faktör 4: Kapsam stabilitesi (%15) — sabit (mid-sprint ekleme bilgisi yok)
        double scopeStability = 75;

        int score = (int) (completionRate * 0.40 + carryoverRate * 0.25
                + velocityConsistency * 0.20 + scopeStability * 0.15);
        score = Math.min(100, Math.max(1, score));

        String grade = calcGrade(score);

        List<SprintHealthDto.Factor> factors = List.of(
                new SprintHealthDto.Factor("Tamamlanma Oranı",
                        Math.round(completionRate * 10) / 10.0, 0.40,
                        completionRate >= 80 ? "positive" : completionRate >= 60 ? "neutral" : "negative",
                        "%d/%d puan teslim edildi".formatted(dash.getCompletedPoints(), dash.getPlannedPoints())),
                new SprintHealthDto.Factor("Taşma (Carryover)",
                        Math.round(carryoverRate * 10) / 10.0, 0.25,
                        carryover.getCarriedOverCount() == 0 ? "positive" : "negative",
                        "%d görev taşındı (%d puan)".formatted(
                                carryover.getCarriedOverCount(), carryover.getCarriedOverPoints())),
                new SprintHealthDto.Factor("Velocity Tutarlılığı",
                        velocityConsistency, 0.20, "neutral",
                        "Geçmiş sprint verisine dayalı tahmin"),
                new SprintHealthDto.Factor("Kapsam Değişimi",
                        scopeStability, 0.15, "neutral",
                        "Sprint ortası değişim takibi mevcut değil")
        );

        String summary = buildHealthSummary(score, dash, carryover);
        return new SprintHealthDto(sprintId, score, grade, factors, summary);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private SprintDto findSprint(int sprintId) {
        // Sadece son 20 sprint içinde ara — tüm listeyi çekmekten daha az yük
        List<SprintDto> all = jiraService.getSprints();
        int fromIdx = Math.max(0, all.size() - 20);
        return all.subList(fromIdx, all.size()).stream()
                .filter(s -> s.getId() == sprintId)
                .findFirst()
                .orElse(all.stream().filter(s -> s.getId() == sprintId).findFirst().orElse(null));
    }

    private int sumPoints(List<IssueDto> issues) {
        return issues.stream().mapToInt(i -> safe(i.getStoryPoints())).sum();
    }

    private int safe(Integer v) {
        return v == null ? 0 : v;
    }

    private List<SprintDashboardDto.BurndownPoint> buildBurndown(
            SprintDto sprint, List<IssueDto> issues, int planned) {
        if (sprint == null || sprint.getStartDate() == null || sprint.getEndDate() == null)
            return List.of(new SprintDashboardDto.BurndownPoint("start", planned));

        // Çözüm tarihine göre günlük kalan puan
        Map<String, Integer> resolvedByDay = new TreeMap<>();
        for (IssueDto i : issues) {
            if (i.isResolved() && i.getResolvedDate() != null) {
                resolvedByDay.merge(i.getResolvedDate(), safe(i.getStoryPoints()), Integer::sum);
            }
        }

        List<SprintDashboardDto.BurndownPoint> points = new ArrayList<>();
        int remaining = planned;
        String date = sprint.getStartDate();
        points.add(new SprintDashboardDto.BurndownPoint(date, remaining));

        for (Map.Entry<String, Integer> e : resolvedByDay.entrySet()) {
            remaining -= e.getValue();
            points.add(new SprintDashboardDto.BurndownPoint(
                    e.getKey(), Math.max(0, remaining)));
        }

        if (points.stream().noneMatch(p -> p.getDate().equals(sprint.getEndDate()))) {
            points.add(new SprintDashboardDto.BurndownPoint(sprint.getEndDate(), Math.max(0, remaining)));
        }

        return points;
    }

    private String buildReviewContext(int sprintId, SprintDto sprint, List<IssueDto> issues) {
        String name = sprint != null ? sprint.getName() : "Sprint " + sprintId;
        String goal = sprint != null && sprint.getGoal() != null ? sprint.getGoal() : "belirtilmedi";
        int planned = sumPoints(issues);
        int completed = issues.stream().filter(IssueDto::isResolved)
                .mapToInt(i -> safe(i.getStoryPoints())).sum();

        String done = issues.stream().filter(IssueDto::isResolved)
                .map(IssueDto::getSummary).collect(Collectors.joining(", "));
        String notDone = issues.stream().filter(i -> !i.isResolved())
                .map(IssueDto::getSummary).collect(Collectors.joining(", "));

        return """
                Sprint Adı: %s
                Sprint Hedefi: %s
                Planlanan Puan: %d, Tamamlanan Puan: %d
                Tamamlanan İşler: %s
                Tamamlanamayan İşler: %s
                """.formatted(name, goal, planned, completed,
                done.isBlank() ? "yok" : done,
                notDone.isBlank() ? "yok" : notDone);
    }

    private VelocityDto buildBasicVelocity(int sprintId, SprintDashboardDto dash) {
        VelocityDto.SprintVelocity sv = new VelocityDto.SprintVelocity(
                sprintId, dash.getName(), dash.getPlannedPoints(), dash.getCompletedPoints());
        return new VelocityDto(dash.getCompletedPoints(), "stable", List.of(sv));
    }

    private String calcGrade(int score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        return "D";
    }

    private String buildHealthSummary(int score, SprintDashboardDto dash, CarryoverDto carryover) {
        String performance = score >= 80 ? "Güçlü sprint." : score >= 60 ? "Orta düzey sprint." : "Zayıf sprint.";
        return "%s %d/%d puan tamamlandı. %d görev sonraki sprint'e taşındı.".formatted(
                performance, dash.getCompletedPoints(), dash.getPlannedPoints(),
                carryover.getCarriedOverCount());
    }
}
