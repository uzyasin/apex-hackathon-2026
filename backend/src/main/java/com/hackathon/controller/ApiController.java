package com.hackathon.controller;

import com.hackathon.dto.AiResult;
import com.hackathon.dto.AnalyzeRequest;
import com.hackathon.dto.ApiResponse;
import com.hackathon.service.AiService;
import com.hackathon.service.DbService;
import com.hackathon.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AiService aiService;
    private final DbService dbService;
    private final DocumentService documentService;

    public ApiController(AiService aiService, DbService dbService, DocumentService documentService) {
        this.aiService = aiService;
        this.dbService = dbService;
        this.documentService = documentService;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of("status", "ok")));
    }

    @PostMapping("/analyze")
    public ResponseEntity<ApiResponse<Map<String, Object>>> analyze(@Valid @RequestBody AnalyzeRequest request) {
        AiResult result = (request.getContext() != null && !request.getContext().isBlank())
                ? aiService.analyzeWithContext(request.getInput(), request.getContext())
                : aiService.analyze(request.getInput());

        Long id = dbService.saveAnalysis(request.getInput(), request.getContext(), result);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", id);
        data.put("summary", result.getSummary());
        data.put("insights", result.getInsights());
        data.put("score", result.getScore());
        data.put("recommendation", result.getRecommendation());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/results")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(dbService.getAllAnalyses()));
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable Long id) {
        Map<String, Object> row = dbService.getAnalysisById(id);
        if (row == null) {
            return ResponseEntity.status(404).body(ApiResponse.fail("Kayıt bulunamadı"));
        }
        return ResponseEntity.ok(ApiResponse.ok(row));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Dosya yüklenmedi"));
        }
        try {
            String text = documentService.extractText(file);
            String preview = text.length() > 5000 ? text.substring(0, 5000) : text;
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("filename", file.getOriginalFilename());
            data.put("size", file.getSize());
            data.put("text", preview);
            return ResponseEntity.ok(ApiResponse.ok(data));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Dosya işlenemedi: " + e.getMessage()));
        }
    }

    // ─── YENİ ENDPOINT'LER BURAYA ──────────────────────────────────────────
    // Şablon:
    // @PostMapping("/your-feature")
    // public ResponseEntity<ApiResponse<YourDto>> yourFeature(@Valid @RequestBody YourRequest req) {
    //   ...
    //   return ResponseEntity.ok(ApiResponse.ok(result));
    // }
    // ──────────────────────────────────────────────────────────────────────
}
