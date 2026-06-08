package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * POST /api/analyze AI çıktı şeması (genel sprint analizi).
 * AI uzmanı sahibi — şema değişirse burası ve AiService.ANALYZE_PROMPT birlikte güncellenir.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResult {

    /** Sprint sağlık skoru: 0-100 arası puan */
    @JsonProperty("sprint_health_score")
    private int sprintHealthScore;

    /** Sprint / görev durumunun kısa yazılı özeti */
    private String summary;

    /** Görevin mantıksal alt görevlere kırılımı */
    @JsonProperty("task_breakdown")
    private List<TaskBreakdown> taskBreakdown;

    /** Tespit edilen riskler */
    private List<String> risks;

    /** AI'nın önerdiği aksiyonlar */
    private List<String> recommendations;

    /**
     * Nihai karar:
     * "Planlanabilir" | "Revize Gerekli" | "Reddedilmeli"
     */
    private String verdict;

    // ── DB uyumu: DbService.saveAnalysis() bu getter'ı kullanır ──────────────
    public int getScore() {
        return sprintHealthScore;
    }

    // ─── Fallback ─────────────────────────────────────────────────────────────
    public static AiResult fallback() {
        AiResult r = new AiResult();
        r.setSprintHealthScore(0);
        r.setSummary("Analiz şu an kullanılamıyor. Lütfen tekrar deneyin.");
        r.setTaskBreakdown(List.of());
        r.setRisks(List.of("AI servisi geçici olarak yanıt vermiyor."));
        r.setRecommendations(List.of("Birkaç saniye sonra tekrar dene."));
        r.setVerdict("Revize Gerekli");
        return r;
    }

    // ─── İç sınıf: Görev kırılım kalemi ─────────────────────────────────────
    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TaskBreakdown {
        /** Alt görev başlığı */
        private String title;

        /** Katman türü: Frontend | Backend | DB | Test | DevOps */
        private String type;

        /** Tahmin edilen story point (Fibonacci önerilir) */
        @JsonProperty("story_points")
        private int storyPoints;

        /** Atama önerisi: takım üyesi adı veya rol */
        @JsonProperty("suggested_assignee")
        private String suggestedAssignee;
    }
}

