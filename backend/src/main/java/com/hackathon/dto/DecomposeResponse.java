package com.hackathon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /api/tasks/decompose yanıtı.
 * AI bir task'ı teknik alt görevlere böler.
 * Mock: contract/mocks/tasks-decompose.json
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DecomposeResponse {

    /** Kaynak issue key */
    private String parentKey;

    /** Kaynak issue başlığı */
    private String parentSummary;

    /** Üretilen alt görevler */
    private List<Subtask> subtasks;

    public static DecomposeResponse fallback(String parentKey, String parentSummary) {
        DecomposeResponse r = new DecomposeResponse();
        r.setParentKey(parentKey);
        r.setParentSummary(parentSummary);
        r.setSubtasks(List.of());
        return r;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Subtask {
        /** Geçici ID (st1, st2...) — Jira'ya yazılmaz */
        private String tempId;

        /** Alt görev başlığı */
        private String title;

        /** Katman: FRONTEND | BACKEND | DB | TEST | DEVOPS */
        private String discipline;

        /** Tahmini süre (saat) */
        private int estimateHours;

        /** Alt görev açıklaması */
        private String description;
    }
}

