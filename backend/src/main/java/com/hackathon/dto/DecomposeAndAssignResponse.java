package com.hackathon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * POST /api/tasks/decompose-and-assign yanıtı.
 * decompose + assign tek seferde döner.
 * Mock: contract/mocks/tasks-decompose-and-assign.json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DecomposeAndAssignResponse {
    private String parentKey;
    private String parentSummary;
    private List<DecomposeResponse.Subtask> subtasks;
    private List<AssignResponse.Assignment> assignments;
}
