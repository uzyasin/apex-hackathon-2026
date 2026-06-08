package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** GET /api/velocity — geçmiş sprint hız özeti. Mock: contract/mocks/velocity.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VelocityDto {
    private double averageVelocity;
    /** improving | stable | declining */
    private String trend;
    private List<SprintVelocity> sprints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SprintVelocity {
        private int sprintId;
        private String name;
        private int committedPoints;
        private int completedPoints;
    }
}
