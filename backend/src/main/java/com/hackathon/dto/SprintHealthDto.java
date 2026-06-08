package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** GET /api/sprint/{id}/health — 1-100 sağlık skoru + faktörler. Mock: contract/mocks/sprint-health.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintHealthDto {
    private int sprintId;
    /** 1-100 */
    private int score;
    /** A+ / A / B+ / B / C / D */
    private String grade;
    private List<Factor> factors;
    private String summary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Factor {
        private String name;
        /** Alt skor 0-100 */
        private double value;
        /** Ağırlık katsayısı, toplam = 1.0 */
        private double weight;
        /** positive | neutral | negative */
        private String impact;
        private String detail;
    }
}
