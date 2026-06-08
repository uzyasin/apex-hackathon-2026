package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** GET /api/sprint/{id}/carryover — sprint'ten taşan görevler. Mock: contract/mocks/sprint-carryover.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarryoverDto {
    private int sprintId;
    private int carriedOverCount;
    private int carriedOverPoints;
    private List<CarryoverItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarryoverItem {
        private String key;
        private String summary;
        private Integer storyPoints;
        /** Kaç sprint boyunca taşındı */
        private int sprintsSpilled;
        private String lastSprintName;
        private String reason;
    }
}
