package com.hackathon.dto;

import lombok.Data;

import java.util.List;

/**
 * AiService.predictSizes() girdisi.
 *
 * Not: Sözleşmedeki POST /api/planning/predict-size isteği yalnızca
 * { "issueKeys": [...] } içerir. Backend (PlanningService) bu key'leri
 * Jira'dan çözümleyip her issue'nun başlığını/açıklamasını ve takım
 * velocity bağlamını DOLDURUP bu DTO ile AiService'e verir.
 */
@Data
public class PredictSizeInput {

    /** Tahmin yapılacak issue'ların çözümlenmiş detayları */
    private List<IssueItem> issues;

    /**
     * Velocity bağlamı: AI'ın tahmini objektifleştirmesi için
     * geçmiş sprint hız özeti. Örn:
     * "Son 3 sprint ortalama velocity 23.5 (trend: stable).
     *  Benzer auth task'ları geçmişte 4-6 puan tamamlandı."
     */
    private String velocityContext;

    @Data
    public static class IssueItem {
        private String key;
        private String summary;
        private String description;
        /** Mevcut atanmış puan, yoksa null */
        private Integer currentStoryPoints;
    }
}

