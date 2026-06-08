package com.hackathon.service;

import com.hackathon.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Epik 2 — Görev kırılımı + akıllı atama.
 * decompose: AiService'e delege eder.
 * assign: kural tabanlı — yetkinlik × 0.6 + kapasite × 0.4 skoru.
 */
@Service
public class TaskService {

    private final AiService aiService;
    private final TeamService teamService;

    public TaskService(AiService aiService, TeamService teamService) {
        this.aiService = aiService;
        this.teamService = teamService;
    }

    /** POST /api/tasks/decompose */
    public DecomposeResponse decompose(String issueKey, String summary,
                                       String description, Integer storyPoints) {
        return aiService.decompose(issueKey, summary, description, storyPoints);
    }

    /** POST /api/tasks/assign */
    public AssignResponse assign(List<AssignRequest.SubtaskItem> subtasks) {
        List<TeamMemberDto> members = teamService.getAll();
        // Geçici yük tablosu: atama sırasında yükü biriktirir
        Map<String, Integer> tempLoads = members.stream()
                .collect(Collectors.toMap(TeamMemberDto::getMemberId,
                        TeamMemberDto::getCurrentLoadHours));

        List<AssignResponse.Assignment> assignments = subtasks.stream()
                .map(sub -> buildAssignment(sub, members, tempLoads))
                .toList();

        return new AssignResponse(assignments);
    }

    /** POST /api/tasks/decompose-and-assign */
    public DecomposeAndAssignResponse decomposeAndAssign(String issueKey, String summary,
                                                          String description, Integer storyPoints) {
        DecomposeResponse decomposed = decompose(issueKey, summary, description, storyPoints);

        List<AssignRequest.SubtaskItem> items = decomposed.getSubtasks().stream()
                .map(st -> {
                    AssignRequest.SubtaskItem item = new AssignRequest.SubtaskItem();
                    item.setTempId(st.getTempId());
                    item.setTitle(st.getTitle());
                    item.setDiscipline(st.getDiscipline());
                    item.setEstimateHours(st.getEstimateHours());
                    return item;
                }).toList();

        AssignResponse assigned = assign(items);

        return new DecomposeAndAssignResponse(
                decomposed.getParentKey(),
                decomposed.getParentSummary(),
                decomposed.getSubtasks(),
                assigned.getAssignments()
        );
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PRIVATE — Atama algoritması
    // ═══════════════════════════════════════════════════════════════════

    private AssignResponse.Assignment buildAssignment(
            AssignRequest.SubtaskItem subtask,
            List<TeamMemberDto> members,
            Map<String, Integer> tempLoads) {

        String discipline = subtask.getDiscipline() == null ? "BACKEND" : subtask.getDiscipline();

        TeamMemberDto best = null;
        int bestScore = -1;

        for (TeamMemberDto m : members) {
            int skillLevel = m.getSkills().getOrDefault(discipline, 0);
            int currentLoad = tempLoads.getOrDefault(m.getMemberId(), m.getCurrentLoadHours());
            int available = m.getCapacityHours() - currentLoad;

            // Kapasite tamamen dolmuşsa atama yapma
            if (available <= 0) continue;

            int skillScore = skillLevel * 20;  // 0-100
            int capScore = (int) ((double) available / m.getCapacityHours() * 100);
            int score = (int) (skillScore * 0.6 + capScore * 0.4);

            if (score > bestScore) {
                bestScore = score;
                best = m;
            }
        }

        // Tüm üyeler doluysa en yüksek yetkinliğe sahip kişiyi seç
        if (best == null) {
            best = members.stream()
                    .max(Comparator.comparingInt(m -> m.getSkills().getOrDefault(discipline, 0)))
                    .orElse(members.get(0));
            bestScore = 50;
        }

        int currentLoad = tempLoads.getOrDefault(best.getMemberId(), best.getCurrentLoadHours());
        int loadAfter = currentLoad + subtask.getEstimateHours();
        tempLoads.put(best.getMemberId(), loadAfter);

        return new AssignResponse.Assignment(
                subtask.getTempId(),
                discipline,
                subtask.getEstimateHours(),
                new AssignResponse.AssigneeSummary(best.getMemberId(), best.getName()),
                Math.min(bestScore, 100),
                buildReason(best, discipline, currentLoad, bestScore),
                loadAfter,
                best.getCapacityHours()
        );
    }

    private String buildReason(TeamMemberDto m, String discipline, int load, int score) {
        int skill = m.getSkills().getOrDefault(discipline, 0);
        String kapasiteStr = score >= 80 ? "uygun" : score >= 60 ? "orta" : "sınırda";
        return "%s yetkinliği %d/5, mevcut yük %d/%d saat — kapasite %s."
                .formatted(discipline, skill, load, m.getCapacityHours(), kapasiteStr);
    }
}
