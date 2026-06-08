package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /api/planning/predict-size yanıtı.
 * AI seçili backlog task'larına story point tahmini yapar.
 * Mock: contract/mocks/planning-predict-size.json
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SizePredictionResponse {

    private List<Prediction> predictions;

    public static SizePredictionResponse fallback() {
        SizePredictionResponse r = new SizePredictionResponse();
        r.setPredictions(List.of());
        return r;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prediction {
        /** Jira issue key (ör. PROJ-201) */
        private String key;

        /** Issue başlığı */
        private String summary;

        /** Mevcut atanmış puan, yoksa null */
        @JsonProperty("currentStoryPoints")
        private Integer currentStoryPoints;

        /** AI'nın önerdiği puan (Fibonacci: 1,2,3,5,8,13) */
        @JsonProperty("predictedStoryPoints")
        private int predictedStoryPoints;

        /** Güven seviyesi: HIGH | MEDIUM | LOW */
        private String confidence;

        /** Tahmin gerekçesi (Türkçe) */
        private String rationale;
    }
}

