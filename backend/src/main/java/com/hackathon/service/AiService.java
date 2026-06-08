package com.hackathon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.dto.AiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    // ─── SYSTEM PROMPT ─────────────────────────────────────────────────────
    // AI Uzmanı bu prompt'u AI_CONTEXT/3_ai_prompts.md ve
    // ai-specialist/PROMPT_GELISTIRME.md'de iterasyona tabi tutar,
    // final hâlini buraya yapıştırır.
    private static final String SYSTEM_PROMPT = """
            Sen bir AI destekli Agile Yönetim Asistanısın (Scrum/Kanban).
            Görevin: sprint verilerini, backlog task'larını ve takım kapasitesini analiz ederek
            somut, uygulanabilir JSON çıktıları üretmek.

            Kullanıcının girdisine göre aşağıdaki JSON yapısını AYNEN döndür:
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
            - YALNIZCA yukarıdaki JSON nesnesini döndür. Markdown, kod bloğu veya ekstra metin YOK.
            - sprint_health_score: 0-100 arası tamsayı. 70+ yeşil, 40-69 sarı, 40 altı kırmızı.
            - task_breakdown: mümkün olduğunca ayrıntılı teknik alt görev listesi.
            - risks: varsa blokajlar, bağımlılıklar, kapasite sorunları.
            - recommendations: velocity verisine dayalı, öncelik sıralı aksiyonlar.
            - verdict: tek bir kelime veya iki kelimelik sabit string (yukarıdaki üçten biri).
            - Girdi Türkçe ise Türkçe, İngilizce ise İngilizce yanıt ver.
            - Girdi belirsizse sprint_health_score=0 yap ve summary'de açıkla.
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

    public AiResult analyze(String userInput) {
        return callClaude(userInput);
    }

    public AiResult analyzeWithContext(String userInput, String context) {
        String combined = "CONTEXT:\n---\n" + context + "\n---\n\nINPUT: " + userInput;
        return callClaude(combined);
    }

    private AiResult callClaude(String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", ANTHROPIC_VERSION);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", maxTokens,
                    "system", SYSTEM_PROMPT,
                    "messages", List.of(Map.of("role", "user", "content", content))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ANTHROPIC_API_URL, request, Map.class);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.getBody().get("content");
            String text = ((String) contentList.get(0).get("text")).trim();

            // Bazen Claude markdown sarar — temizle
            text = text.replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)^```\\s*", "").replaceAll("\\s*```$", "");

            return objectMapper.readValue(text, AiResult.class);
        } catch (Exception e) {
            System.err.println("[AiService] error: " + e.getMessage());
            return AiResult.fallback();
        }
    }
}
