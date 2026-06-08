package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** POST /api/tasks/assign yanıtı. Mock: contract/mocks/tasks-assign.json */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignResponse {

    private List<Assignment> assignments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Assignment {
        private String tempId;
        private String discipline;
        private int estimateHours;
        private AssigneeSummary suggestedAssignee;
        private int matchScore;
        private String reason;
        private int memberLoadAfterHours;
        private int memberCapacityHours;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssigneeSummary {
        private String memberId;
        private String name;
    }
}
