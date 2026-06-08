package com.hackathon.controller;

import com.hackathon.dto.*;
import com.hackathon.service.SprintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Epik 3 — Sprint Review ve Dashboard endpoint'leri.
 */
@RestController
@RequestMapping("/api/sprint")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    /** GET /api/sprint/{sprintId}/dashboard — planlanan vs gerçekleşen + burndown */
    @GetMapping("/{sprintId}/dashboard")
    public ResponseEntity<ApiResponse<SprintDashboardDto>> getDashboard(
            @PathVariable int sprintId) {
        return ResponseEntity.ok(ApiResponse.ok(sprintService.getDashboard(sprintId)));
    }

    /** GET /api/sprint/{sprintId}/review — AI demo raporu */
    @GetMapping("/{sprintId}/review")
    public ResponseEntity<ApiResponse<SprintReview>> getReview(
            @PathVariable int sprintId) {
        return ResponseEntity.ok(ApiResponse.ok(sprintService.getReview(sprintId)));
    }

    /** GET /api/sprint/{sprintId}/carryover — taşan görevler (BONUS) */
    @GetMapping("/{sprintId}/carryover")
    public ResponseEntity<ApiResponse<CarryoverDto>> getCarryover(
            @PathVariable int sprintId) {
        return ResponseEntity.ok(ApiResponse.ok(sprintService.getCarryover(sprintId)));
    }

    /** GET /api/sprint/{sprintId}/health — 1-100 sağlık skoru (BONUS) */
    @GetMapping("/{sprintId}/health")
    public ResponseEntity<ApiResponse<SprintHealthDto>> getHealth(
            @PathVariable int sprintId) {
        return ResponseEntity.ok(ApiResponse.ok(sprintService.getHealth(sprintId)));
    }
}
