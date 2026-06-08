package com.hackathon.service;

import com.hackathon.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Epik 1 — Akıllı Planlama.
 * Velocity hesabı + predict-size orkestrasyon (Jira → AI).
 */
@Service
public class PlanningService {

    private final JiraGateway jiraService;
    private final AiService aiService;

    public PlanningService(JiraGateway jiraService, AiService aiService) {
        this.jiraService = jiraService;
        this.aiService = aiService;
    }

    /** GET /api/velocity — son 5 kapalı sprintten ortalama hız hesabı. */
    public VelocityDto getVelocity() {
        List<SprintDto> allSprints = jiraService.getSprints();
        List<SprintDto> allClosed = allSprints.stream()
                .filter(s -> "closed".equals(s.getState()))
                .toList();
        // Sadece son 5 sprint — N+1 yükünü sınırlamak için
        int fromIdx = Math.max(0, allClosed.size() - 5);
        List<SprintDto> closed = allClosed.subList(fromIdx, allClosed.size());

        List<VelocityDto.SprintVelocity> velocities = new ArrayList<>();
        int totalCompleted = 0;

        for (SprintDto sprint : closed) {
            List<IssueDto> issues = jiraService.getSprintIssues(sprint.getId());
            int committed = issues.stream()
                    .mapToInt(i -> i.getStoryPoints() == null ? 0 : i.getStoryPoints())
                    .sum();
            int completed = issues.stream()
                    .filter(IssueDto::isResolved)
                    .mapToInt(i -> i.getStoryPoints() == null ? 0 : i.getStoryPoints())
                    .sum();
            totalCompleted += completed;
            velocities.add(new VelocityDto.SprintVelocity(
                    sprint.getId(), sprint.getName(), committed, completed));
        }

        double avg = velocities.isEmpty() ? 0 :
                (double) totalCompleted / velocities.size();
        String trend = calcTrend(velocities);

        return new VelocityDto(Math.round(avg * 10.0) / 10.0, trend, velocities);
    }

    /**
     * POST /api/planning/predict-size — issueKey listesini Jira'dan çözümle,
     * velocity bağlamı ile AI'a gönder.
     */
    public SizePredictionResponse predictSize(List<String> issueKeys) {
        List<PredictSizeInput.IssueItem> issueItems = new ArrayList<>();
        for (String key : issueKeys) {
            IssueDto issue = jiraService.getIssue(key);
            if (issue == null) {
                PredictSizeInput.IssueItem item = new PredictSizeInput.IssueItem();
                item.setKey(key);
                item.setSummary(key + " (Jira'dan çekilemedi)");
                issueItems.add(item);
                continue;
            }
            PredictSizeInput.IssueItem item = new PredictSizeInput.IssueItem();
            item.setKey(issue.getKey());
            item.setSummary(issue.getSummary());
            item.setDescription(issue.getDescription());
            item.setCurrentStoryPoints(issue.getStoryPoints());
            issueItems.add(item);
        }

        VelocityDto velocity = getVelocity();
        String velocityContext = buildVelocityContext(velocity);

        PredictSizeInput input = new PredictSizeInput();
        input.setIssues(issueItems);
        input.setVelocityContext(velocityContext);

        return aiService.predictSizes(input);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private String calcTrend(List<VelocityDto.SprintVelocity> data) {
        if (data.size() < 2) return "stable";
        int last = data.get(data.size() - 1).getCompletedPoints();
        int prev = data.get(data.size() - 2).getCompletedPoints();
        double delta = (double) (last - prev) / Math.max(prev, 1) * 100;
        if (delta > 10) return "improving";
        if (delta < -10) return "declining";
        return "stable";
    }

    private String buildVelocityContext(VelocityDto v) {
        StringBuilder sb = new StringBuilder();
        sb.append("Son %d sprint ortalama velocity: %.1f puan (trend: %s).\n"
                .formatted(v.getSprints().size(), v.getAverageVelocity(), v.getTrend()));
        v.getSprints().forEach(s ->
                sb.append("  - %s: taahhüt=%d, tamamlanan=%d\n"
                        .formatted(s.getName(), s.getCommittedPoints(), s.getCompletedPoints())));
        return sb.toString();
    }
}
