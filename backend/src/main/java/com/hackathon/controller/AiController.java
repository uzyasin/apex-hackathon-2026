package com.hackathon.controller;

import com.hackathon.dto.BlockerSuggestion;
import com.hackathon.dto.DecomposeResponse;
import com.hackathon.dto.PredictSizeInput;
import com.hackathon.dto.SizePredictionResponse;
import com.hackathon.dto.SprintReview;
import com.hackathon.dto.ApiResponse;
import com.hackathon.service.AiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AI üreten endpoint'ler — sözleşmedeki (contract/API_CONTRACT.md) dört AI özelliğini
 * {@link AiService}'e bağlar. Tüm yanıtlar {@code ApiResponse<T>} ile sarılır.
 *
 * <p><b>Backend devir notu:</b> {@code predict-size} ve {@code review} endpoint'leri,
 * üretimde Jira/Sprint verisinden TÜRETİLEN bağlama ihtiyaç duyar. Bu controller, o bağlamı
 * istek gövdesinden/parametresinden alarak JİRASIZ ÇALIŞABİLİR bir referans sunar. Backend,
 * {@code JiraService}/{@code SprintService} hazır olunca:
 * <ul>
 *   <li>{@code predict-size}: istek gövdesini sözleşmedeki {@code {issueKeys:[...]}}'e indirip
 *       issue detaylarını + velocity bağlamını sunucu tarafında doldurabilir.</li>
 *   <li>{@code review}: {@code context} parametresi yerine sprint metriklerini sunucu tarafında
 *       üretip {@code generateReview}'a verebilir.</li>
 * </ul>
 * Bu değişiklikler {@link AiService} imzalarını DEĞİŞTİRMEDEN yapılabilir.
 */
@RestController
@RequestMapping("/api")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    // ─── Epik 1: Akıllı Planlama ────────────────────────────────────────────

    /**
     * POST /api/planning/predict-size — seçili task'lara story point tahmini.
     * Gövde: issue detayları + velocity bağlamı (bkz. {@link PredictSizeInput}).
     */
    @PostMapping("/planning/predict-size")
    public ResponseEntity<ApiResponse<SizePredictionResponse>> predictSize(
            @RequestBody PredictSizeInput input) {
        return ResponseEntity.ok(ApiResponse.ok(aiService.predictSizes(input)));
    }

    /**
     * POST /api/planning/blockers — bloklanmış task için AI çözüm önerisi (BONUS).
     */
    @PostMapping("/planning/blockers")
    public ResponseEntity<ApiResponse<BlockerSuggestion>> blockers(
            @Valid @RequestBody BlockerReq req) {
        BlockerSuggestion data = aiService.suggestBlockers(
                req.issueKey(), req.summary(), req.description(), req.blockerReason());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    // ─── Epik 2: Görev Kırılımı ─────────────────────────────────────────────

    /**
     * POST /api/tasks/decompose — task'ı teknik alt görevlere böler.
     */
    @PostMapping("/tasks/decompose")
    public ResponseEntity<ApiResponse<DecomposeResponse>> decompose(
            @Valid @RequestBody DecomposeReq req) {
        DecomposeResponse data = aiService.decompose(
                req.issueKey(), req.summary(), req.description(), req.storyPoints());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    // ─── Epik 3: AI Sprint Review ───────────────────────────────────────────

    /**
     * GET /api/sprint/{sprintId}/review — sprint demo raporu.
     * {@code context}: backend'in sprint metriklerinden ürettiği düz metin özet.
     * Üretimde bu, sunucu tarafında {@code SprintService}'ten gelir.
     */
    @GetMapping("/sprint/{sprintId}/review")
    public ResponseEntity<ApiResponse<SprintReview>> review(
            @PathVariable int sprintId,
            @RequestParam(name = "context", required = false) String context) {
        return ResponseEntity.ok(ApiResponse.ok(aiService.generateReview(sprintId, context)));
    }

    // ─── İstek gövdeleri (iç içe record — DTO ad çakışmasını önler) ─────────

    /** POST /api/planning/blockers istek gövdesi. */
    public record BlockerReq(
            @NotBlank(message = "issueKey gereklidir") String issueKey,
            @NotBlank(message = "summary gereklidir") String summary,
            String description,
            @NotBlank(message = "blockerReason gereklidir") String blockerReason) {
    }

    /** POST /api/tasks/decompose istek gövdesi. */
    public record DecomposeReq(
            @NotBlank(message = "issueKey gereklidir") String issueKey,
            @NotBlank(message = "summary gereklidir") String summary,
            String description,
            Integer storyPoints) {
    }
}

