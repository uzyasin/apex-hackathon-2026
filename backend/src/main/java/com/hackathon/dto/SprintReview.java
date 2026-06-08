package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * GET /api/sprint/{sprintId}/review yanıtı.
 * AI tarafından üretilen sprint demo raporu ve özeti.
 * Mock: contract/mocks/sprint-review.json
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SprintReview {

    private int sprintId;

    /** Tek cümle sprint özeti (jüri slaytı için) */
    private String headline;

    /** 2-4 cümle ayrıntılı özet */
    private String summary;

    /** Tamamlanan başarılar listesi */
    private List<String> achievements;

    /** Adım adım demo senaryosu */
    private List<String> demoScript;

    public static SprintReview fallback(int sprintId) {
        SprintReview r = new SprintReview();
        r.setSprintId(sprintId);
        r.setHeadline("Sprint raporu şu an üretilemiyor.");
        r.setSummary("AI servisi geçici olarak yanıt vermiyor. Lütfen birkaç saniye sonra tekrar deneyin.");
        r.setAchievements(List.of());
        r.setDemoScript(List.of());
        return r;
    }
}

