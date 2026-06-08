package com.hackathon.controller;

import com.hackathon.dto.*;
import com.hackathon.service.AiService;
import com.hackathon.service.PlanningService;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Epik 1 — Akıllı Planlama endpoint'leri.
 */
@RestController
@RequestMapping("/api")
@Validated
public class PlanningController {

    private final PlanningService planningService;
    private final AiService aiService;

    public PlanningController(PlanningService planningService, AiService aiService) {
        this.planningService = planningService;
        this.aiService = aiService;
    }

    /** GET /api/velocity — geçmiş sprint hız özeti */
    @GetMapping("/velocity")
    public ResponseEntity<ApiResponse<VelocityDto>> getVelocity() {
        return ResponseEntity.ok(ApiResponse.ok(planningService.getVelocity()));
    }

    /**
     * POST /api/planning/predict-size
     * İstek: { "issueKeys": ["PROJ-101", "PROJ-102"] }
     */
    @PostMapping("/planning/predict-size")
    public ResponseEntity<ApiResponse<SizePredictionResponse>> predictSize(
            @RequestBody PredictSizeRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(planningService.predictSize(req.issueKeys())));
    }

    /**
     * POST /api/planning/blockers (BONUS)
     * İstek: { "issueKey", "summary", "description", "blockerReason" }
     */
    @PostMapping("/planning/blockers")
    public ResponseEntity<ApiResponse<BlockerSuggestion>> blockers(
            @RequestBody BlockerRequest req) {
        BlockerSuggestion result = aiService.suggestBlockers(
                req.issueKey(), req.summary(), req.description(), req.blockerReason());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ─── İstek kayıtları ────────────────────────────────────────────────────

    public record PredictSizeRequest(
            @NotEmpty(message = "issueKeys boş olamaz") List<String> issueKeys) {
    }

    public record BlockerRequest(
            String issueKey,
            String summary,
            String description,
            String blockerReason) {
    }
}
