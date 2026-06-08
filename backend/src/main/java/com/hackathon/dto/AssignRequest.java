package com.hackathon.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** POST /api/tasks/assign istek gövdesi. */
@Data
@NoArgsConstructor
public class AssignRequest {

    @NotEmpty(message = "subtasks listesi boş olamaz")
    private List<SubtaskItem> subtasks;

    @Data
    @NoArgsConstructor
    public static class SubtaskItem {
        private String tempId;
        private String title;
        /** FRONTEND | BACKEND | DB | TEST | DEVOPS */
        private String discipline;
        private int estimateHours;
    }
}
