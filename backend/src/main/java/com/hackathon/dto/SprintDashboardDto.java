package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/** GET /api/sprint/{id}/dashboard — planlanan vs gerçekleşen + burndown. Mock: contract/mocks/sprint-dashboard.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintDashboardDto {
    private int sprintId;
    private String name;
    private String startDate;
    private String endDate;
    private int plannedPoints;
    private int completedPoints;
    private int plannedCount;
    private int completedCount;
    /** Negatif = altında, pozitif = üstünde */
    private double deviationPercent;
    /** "TODO" | "IN_PROGRESS" | "DONE" → sayı */
    private Map<String, Integer> statusBreakdown;
    private List<BurndownPoint> burndown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BurndownPoint {
        /** ISO date: yyyy-MM-dd */
        private String date;
        private int remaining;
    }
}
