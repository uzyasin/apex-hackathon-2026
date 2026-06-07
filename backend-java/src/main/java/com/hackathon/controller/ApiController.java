package com.hackathon.controller;

import com.hackathon.dto.AiResult;
import com.hackathon.dto.AnalyzeRequest;
import com.hackathon.dto.ApiResponse;
import com.hackathon.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AiService aiService;

    public ApiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "ok")));
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<AiResult>> analyze(@Valid @RequestBody AnalyzeRequest request) {
        AiResult result = request.getContext() != null && !request.getContext().isBlank()
                ? aiService.analyzeWithContext(request.getInput(), request.getContext())
                : aiService.analyze(request.getInput());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ─── ADD NEW ENDPOINTS BELOW ───────────────────────────────────────────
    // Pattern: @PostMapping("/your-feature") public ResponseEntity<...> yourFeature(...)
    // ──────────────────────────────────────────────────────────────────────
}
