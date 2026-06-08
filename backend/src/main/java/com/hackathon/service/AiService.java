package com.hackathon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * AI beyni — Anthropic Claude entegrasyonu.
 *
 * Her ürün özelliği için ayrı bir SYSTEM_PROMPT (text-block) ve public metot vardır.
 * Ortak {@link #callClaude} metodu HTTP çağrısı + markdown temizleme + JSON parse +
 * hata durumunda fallback mantığını tek noktada toplar.
 *
 * Sözleşme: contract/API_CONTRACT.md — AI üreten endpoint'ler:
 *   - POST /api/analyze                (genel sprint analizi — demo/özet)
 *   - POST /api/planning/predict-size  (story point tahmini)
 *   - POST /api/planning/blockers      (blokaj çözümü — BONUS)
 *   - POST /api/tasks/decompose        (görev kırılımı)
 *   - GET  /api/sprint/{id}/review     (sprint demo raporu)
 */
@Service
public class AiService {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    // ─── PROMPT 1: GENEL SPRINT ANALİZİ (POST /api/analyze) ─────────────────
    private static final String ANALYZE_PROMPT = """
            Sen bir AI destekli Agile Yönetim Asistanısın (Scrum/Kanban).
            Görevin: sprint verilerini, backlog task'larını ve takım kapasitesini analiz ederek
            somut, uygulanabilir bir JSON çıktısı üretmek.

            YALNIZCA aşağıdaki JSON nesnesini döndür:
            {
              "sprint_health_score": <0-100 arası tam sayı>,
              "summary": "<sprint/görev durumunun 2-3 cümlelik Türkçe özeti>",
              "task_breakdown": [
                {
                  "title": "<alt görev başlığı>",
                  "type": "<Frontend | Backend | DB | Test | DevOps>",
                  "story_points": <Fibonacci: 1, 2, 3, 5, 8, 13>,
                  "suggested_assignee": "<rol veya isim>"
                }
              ],
              "risks": ["<risk açıklaması>"],
              "recommendations": ["<somut öneri>"],
              "verdict": "<Planlanabilir | Revize Gerekli | Reddedilmeli>"
            }

            Kurallar:
            - YALNIZCA JSON döndür. Markdown, kod bloğu (```), ekstra metin YOK.
            - sprint_health_score: 70+ yeşil, 40-69 sarı, <40 kırmızı.
            - story_points yalnızca Fibonacci (1,2,3,5,8,13) olabilir.
            - Girdi Türkçe ise Türkçe yanıt ver.
            - Girdi belirsiz/alakasızsa sprint_health_score=0 yap ve summary'de açıkla.
            """;

    // ─── PROMPT 2: STORY POINT TAHMİNİ (POST /api/planning/predict-size) ────
    private static final String PREDICT_SIZE_PROMPT = """
            Sen deneyimli bir Agile tahmin uzmanısın (estimation specialist).
            Görevin: verilen backlog task'larına, takımın geçmiş velocity verisine dayanarak
            OBJEKTİF story point tahmini yapmak (Predictive Sizing).

            YALNIZCA aşağıdaki JSON nesnesini döndür:
            {
              "predictions": [
                {
                  "key": "<issue key>",
                  "summary": "<issue başlığı>",
                  "currentStoryPoints": <mevcut puan veya null>,
                  "predictedStoryPoints": <Fibonacci: 1,2,3,5,8,13>,
                  "confidence": "<HIGH | MEDIUM | LOW>",
                  "rationale": "<tahminin Türkçe gerekçesi — velocity ve benzer task'lara atıf yap>"
                }
              ]
            }

            Kurallar:
            - YALNIZCA JSON döndür. Markdown veya kod bloğu YOK.
            - Her girdideki issue için TAM OLARAK bir prediction üret. key'leri AYNEN koru.
            - predictedStoryPoints yalnızca Fibonacci (1,2,3,5,8,13) olabilir.
            - Belirsizlik yüksekse confidence=LOW ve puanı yukarı yuvarla.
            - Velocity bağlamını ve benzer geçmiş task'ları rationale'da açıkça gerekçe göster.
            - rationale Türkçe olmalı.
            """;

    // ─── PROMPT 3: BLOKAJ ÇÖZÜMÜ (POST /api/planning/blockers) ──────────────
    private static final String BLOCKER_PROMPT = """
            Sen kıdemli bir teknik lider ve problem çözücüsün.
            Görevin: bloklanmış bir task için kök neden analizi yapıp uygulanabilir çözümler önermek.

            YALNIZCA aşağıdaki JSON nesnesini döndür:
            {
              "key": "<issue key>",
              "rootCause": "<blokajın kök neden analizi, Türkçe>",
              "suggestions": ["<adım adım çözüm önerisi>", "..."],
              "recommendedAction": "<en öncelikli, somut önerilen eylem>"
            }

            Kurallar:
            - YALNIZCA JSON döndür. Markdown veya kod bloğu YOK.
            - key'i girdideki ile AYNEN koru.
            - suggestions 2-4 madde, somut ve uygulanabilir olmalı.
            - Tüm metinler Türkçe olmalı.
            """;

    // ─── PROMPT 4: GÖREV KIRILIMI (POST /api/tasks/decompose) ───────────────
    private static final String DECOMPOSE_PROMPT = """
            Sen bir teknik proje liderisin.
            Görevin: verilen task'ı mantıklı teknik alt görevlere bölmek
            (Frontend, Backend, DB, Test, DevOps katmanları).

            YALNIZCA aşağıdaki JSON nesnesini döndür:
            {
              "parentKey": "<kaynak issue key>",
              "parentSummary": "<kaynak issue başlığı>",
              "subtasks": [
                {
                  "tempId": "st1",
                  "title": "<alt görev başlığı>",
                  "discipline": "<FRONTEND | BACKEND | DB | TEST | DEVOPS>",
                  "estimateHours": <tahmini saat, tam sayı>,
                  "description": "<alt görevin kısa teknik açıklaması>"
                }
              ]
            }

            Kurallar:
            - YALNIZCA JSON döndür. Markdown veya kod bloğu YOK.
            - parentKey ve parentSummary'i girdideki ile AYNEN koru.
            - tempId'ler st1, st2, st3... şeklinde sıralı olmalı.
            - discipline BÜYÜK HARF ve şu sabitlerden biri: FRONTEND, BACKEND, DB, TEST, DEVOPS.
            - Her task için en az bir TEST alt görevi öner.
            - Tüm metinler Türkçe olmalı.
            """;

    // ─── PROMPT 5: SPRINT REVIEW RAPORU (GET /api/sprint/{id}/review) ───────
    private static final String REVIEW_PROMPT = """
            Sen bir Scrum Master ve teknik sunum uzmanısın.
            Görevin: "Bu sprint ne başardık?" odaklı bir demo raporu ve jüriye/paydaşa
            sunulacak özet üretmek.

            YALNIZCA aşağıdaki JSON nesnesini döndür:
            {
              "sprintId": <sprint id, tam sayı>,
              "headline": "<tek cümle çarpıcı sprint özeti — sunum slaytı için>",
              "summary": "<2-4 cümle ayrıntılı Türkçe özet>",
              "achievements": ["<tamamlanan somut başarı>", "..."],
              "demoScript": ["1. <adım>", "2. <adım>", "..."]
            }

            Kurallar:
            - YALNIZCA JSON döndür. Markdown veya kod bloğu YOK.
            - sprintId'i girdideki ile AYNEN koru.
            - achievements yalnızca GERÇEKTEN tamamlanan (DONE) işlerden üretilmeli, uydurma YOK.
            - demoScript numaralı, izlenebilir adımlar içermeli.
            - Tüm metinler Türkçe olmalı.
            """;
    // ───────────────────────────────────────────────────────────────────────

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens}")
    private int maxTokens;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ═══════════════════════════════════════════════════════════════════════
    //  PUBLIC API — Backend controller'ları bu metodları çağırır
    // ═══════════════════════════════════════════════════════════════════════

    /** POST /api/analyze — serbest metinden genel sprint analizi. */
    public AiResult analyze(String userInput) {
        return callClaude(ANALYZE_PROMPT, userInput, AiResult.class, AiResult::fallback);
    }

    /** POST /api/analyze — RAG bağlamı ile sprint analizi. */
    public AiResult analyzeWithContext(String userInput, String context) {
        String combined = "CONTEXT:\n---\n" + context + "\n---\n\nINPUT: " + userInput;
        return callClaude(ANALYZE_PROMPT, combined, AiResult.class, AiResult::fallback);
    }

    /** POST /api/planning/predict-size — backlog task'larına story point tahmini. */
    public SizePredictionResponse predictSizes(PredictSizeInput input) {
        if (input == null || input.getIssues() == null || input.getIssues().isEmpty()) {
            return SizePredictionResponse.fallback();
        }
        String user = buildPredictSizeUserMessage(input);
        return callClaude(PREDICT_SIZE_PROMPT, user, SizePredictionResponse.class,
                SizePredictionResponse::fallback);
    }

    /**
     * POST /api/planning/blockers — bloklanmış task için çözüm önerisi (BONUS).
     * Backend, controller istek alanlarını doğrudan bu parametrelere geçirir.
     */
    public BlockerSuggestion suggestBlockers(String issueKey, String summary,
                                             String description, String blockerReason) {
        String user = """
                ISSUE KEY: %s
                BAŞLIK: %s
                AÇIKLAMA: %s
                BLOKAJ NEDENİ: %s
                """.formatted(issueKey, summary, nullSafe(description), blockerReason);
        return callClaude(BLOCKER_PROMPT, user, BlockerSuggestion.class,
                () -> BlockerSuggestion.fallback(issueKey));
    }

    /**
     * POST /api/tasks/decompose — task'ı teknik alt görevlere böl.
     * Backend, controller istek alanlarını doğrudan bu parametrelere geçirir.
     */
    public DecomposeResponse decompose(String issueKey, String summary,
                                       String description, Integer storyPoints) {
        String user = """
                ISSUE KEY: %s
                BAŞLIK: %s
                AÇIKLAMA: %s
                MEVCUT STORY POINT: %s
                """.formatted(issueKey, summary, nullSafe(description),
                storyPoints == null ? "belirtilmedi" : storyPoints.toString());
        return callClaude(DECOMPOSE_PROMPT, user, DecomposeResponse.class,
                () -> DecomposeResponse.fallback(issueKey, summary));
    }

    /**
     * GET /api/sprint/{id}/review — sprint demo raporu.
     *
     * @param sprintId      sprint kimliği
     * @param sprintContext backend'in (SprintService) hazırladığı sprint metrik özeti:
     *                      sprint adı, hedef, planlanan/tamamlanan puan, tamamlanan issue
     *                      başlıkları vb. düz metin.
     */
    public SprintReview generateReview(int sprintId, String sprintContext) {
        String user = """
                SPRINT ID: %d
                SPRINT VERİLERİ:
                ---
                %s
                ---
                Yukarıdaki gerçek sprint verilerine dayanarak demo raporunu üret.
                """.formatted(sprintId, nullSafe(sprintContext));
        return callClaude(REVIEW_PROMPT, user, SprintReview.class,
                () -> SprintReview.fallback(sprintId));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ORTAK ÇEKİRDEK — HTTP çağrısı + temizleme + parse + fallback
    // ═══════════════════════════════════════════════════════════════════════

    private <T> T callClaude(String systemPrompt, String userContent, Class<T> type, Supplier<T> fallback) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", ANTHROPIC_VERSION);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", maxTokens,
                    "system", systemPrompt,
                    "messages", List.of(Map.of("role", "user", "content", userContent))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ANTHROPIC_API_URL, request, Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.getBody().get("content");
            String text = ((String) contentList.get(0).get("text")).trim();

            // Claude bazen markdown ile sarar — temizle
            text = stripMarkdownFences(text);

            return objectMapper.readValue(text, type);
        } catch (Exception e) {
            System.err.println("[AiService] " + type.getSimpleName() + " error: " + e.getMessage());
            return fallback.get();
        }
    }

    private static String stripMarkdownFences(String text) {
        return text
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)^```\\s*", "")
                .replaceAll("\\s*```$", "")
                .trim();
    }

    private String buildPredictSizeUserMessage(PredictSizeInput input) {
        String issues = input.getIssues().stream()
                .map(it -> """
                        - KEY: %s
                          BAŞLIK: %s
                          AÇIKLAMA: %s
                          MEVCUT PUAN: %s""".formatted(
                        it.getKey(),
                        it.getSummary(),
                        nullSafe(it.getDescription()),
                        it.getCurrentStoryPoints() == null ? "yok" : it.getCurrentStoryPoints().toString()))
                .collect(Collectors.joining("\n"));

        return """
                VELOCITY BAĞLAMI:
                %s

                TAHMİN EDİLECEK TASK'LAR:
                %s
                """.formatted(nullSafe(input.getVelocityContext()), issues);
    }

    private static String nullSafe(String s) {
        return (s == null || s.isBlank()) ? "yok" : s;
    }
}


