# RAG Kurulum Rehberi (Java)
# AI Model Sorumlusu — Belge/Veri Bağlama

Bu dosyayı kullan eğer: Yarışmada sana PDF, TXT, CSV gibi bir belge verilirse
ve "Bu belgeden soru yanıtla" veya "Bu veriye göre analiz yap" tarzı bir görev çıkarsa.

RAG = Retrieval-Augmented Generation
Kısaca: Büyük belgeyi Claude'a tümden gönderme → önce ilgili parçayı bul → sadece o parçayı gönder.

---

## Karar: RAG Gerekli Mi?

Önce bu soruyu sor kendine:

| Durum | Karar |
|-------|-------|
| Belge 10 sayfanın altında | RAG'e gerek yok — tüm belgeyi context'e ekle |
| Belge 10-50 sayfa arası | Basit in-memory RAG yeterli (bu dosyadaki yaklaşım) |
| Belge 50 sayfa üstü | Yine in-memory ama chunk sayısını artır |
| Birden fazla belge | Yine in-memory, birden fazla kaynağı birleştir |

**Dış servis (Pinecone, Chroma) kullanma.** 4 saatte kurulum riski yüksek.
Aşağıdaki in-memory yaklaşım hackathon için yeterli.

---

## Adım 1: PDFBox Zaten Hazır

`backend/pom.xml`'de PDFBox 3.0.1 bağımlılığı zaten var.
`DocumentService.java` zaten PDF/TXT/MD text extraction yapıyor:

```java
// backend/src/main/java/com/hackathon/service/DocumentService.java
public String extractText(MultipartFile file) throws IOException { ... }
```

Yani dosyayı text'e çevirmek için yeni kod yazmana gerek yok.
Eğer disk üzerinden okumak istersen (kullanıcı upload etmediyse):

```java
// Yeni helper method - DocumentService.java'ya ekle
public String extractTextFromFile(String path) throws IOException {
    Path filePath = Path.of(path);
    if (path.endsWith(".pdf")) {
        try (PDDocument doc = Loader.loadPDF(filePath.toFile())) {
            return new PDFTextStripper().getText(doc);
        }
    }
    return Files.readString(filePath, StandardCharsets.UTF_8);
}
```

---

## Adım 2: Metni Parçalara Böl (Chunking)

Yeni servis: `backend/src/main/java/com/hackathon/service/RagService.java`

```java
package com.hackathon.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RagService {

    private final DocumentService documentService;

    @Value("${rag.document-path:#{null}}")
    private String documentPath;

    private static final int CHUNK_SIZE_WORDS = 500;
    private static final int CHUNK_OVERLAP_WORDS = 50;

    public record Chunk(int id, String text) {}

    private final List<Chunk> chunks = new ArrayList<>();

    public RagService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostConstruct
    public void init() {
        if (documentPath == null || documentPath.isBlank()) {
            System.out.println("[RagService] document path not configured, skipping load");
            return;
        }
        try {
            String text = documentService.extractTextFromFile(documentPath);
            loadFromText(text);
            System.out.println("[RagService] " + chunks.size() + " chunk yüklendi: " + documentPath);
        } catch (IOException e) {
            System.err.println("[RagService] load error: " + e.getMessage());
        }
    }

    public void loadFromText(String text) {
        chunks.clear();
        String[] words = text.split("\\s+");
        int step = CHUNK_SIZE_WORDS - CHUNK_OVERLAP_WORDS;
        for (int i = 0; i < words.length; i += step) {
            int end = Math.min(i + CHUNK_SIZE_WORDS, words.length);
            String chunk = String.join(" ", List.of(words).subList(i, end));
            if (!chunk.isBlank()) {
                chunks.add(new Chunk(chunks.size(), chunk));
            }
            if (end == words.length) break;
        }
    }

    public List<String> findRelevantChunks(String query, int topK) {
        String[] queryWords = query.toLowerCase().split("\\s+");
        return chunks.stream()
                .map(c -> new Object() {
                    final Chunk chunk = c;
                    final int score = scoreChunk(c.text().toLowerCase(), queryWords);
                })
                .filter(item -> item.score > 0)
                .sorted(Comparator.<Object>comparingInt(item -> {
                    try { return (int) item.getClass().getField("score").get(item); }
                    catch (Exception e) { return 0; }
                }).reversed())
                .limit(topK)
                .map(item -> {
                    try { return ((Chunk) item.getClass().getField("chunk").get(item)).text(); }
                    catch (Exception e) { return ""; }
                })
                .toList();
    }

    private int scoreChunk(String chunkLower, String[] queryWords) {
        int score = 0;
        for (String w : queryWords) {
            if (w.length() < 3) continue;
            int idx = 0;
            while ((idx = chunkLower.indexOf(w, idx)) != -1) {
                score++;
                idx += w.length();
            }
        }
        return score;
    }

    public String buildContext(String query) {
        List<String> relevant = findRelevantChunks(query, 3);
        return relevant.isEmpty() ? null : String.join("\n\n---\n\n", relevant);
    }
}
```

> Yukarıdaki anonymous class yerine daha temizi: ayrı bir `ScoredChunk` record'u kullan.
> Hackathon hızı için kısaltılmış versiyon yeterli.

**chunkSize ne olmalı?**
- 300-500 kelime genellikle iyi çalışır
- Çok küçükse bağlam kaybolur, çok büyükse token israfı olur

---

## Adım 3: AiService'e RAG Method'u Ekle

`backend/src/main/java/com/hackathon/service/AiService.java` içine yeni method ekle:

```java
// AiService.java içine ekle

@Autowired(required = false)  // RagService opsiyonel — yoksa hata vermesin
private RagService ragService;

private static final String RAG_SYSTEM_PROMPT = """
        You are a knowledgeable assistant. Answer using ONLY the provided context.
        If the answer is not in the context, say "Bu bilgi belgede bulunmuyor."

        Respond ONLY with valid JSON:
        {
          "answer": "string",
          "source_quote": "exact quote from context",
          "confidence": "high" | "medium" | "low"
        }
        """;

public Map<String, Object> answerFromDocument(String question) {
    if (ragService == null) {
        return Map.of("answer", "RAG aktif değil", "confidence", "low");
    }
    String context = ragService.buildContext(question);
    if (context == null) {
        return Map.of(
            "answer", "Bu bilgi belgede bulunmuyor.",
            "source_quote", "",
            "confidence", "low"
        );
    }

    String userMessage = "CONTEXT:\n---\n" + context + "\n---\n\nQUESTION: " + question;

    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = Map.of(
            "model", model,
            "max_tokens", 1024,
            "system", RAG_SYSTEM_PROMPT,
            "messages", List.of(Map.of("role", "user", "content", userMessage))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(ANTHROPIC_API_URL, request, Map.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.getBody().get("content");
        String text = ((String) contentList.get(0).get("text")).trim();
        text = text.replaceAll("(?s)^```json\\s*", "").replaceAll("\\s*```$", "");

        return objectMapper.readValue(text, Map.class);
    } catch (Exception e) {
        System.err.println("[AiService] RAG error: " + e.getMessage());
        return Map.of("answer", "RAG hatası: " + e.getMessage(), "confidence", "low");
    }
}
```

> Not: `RagService`'i constructor injection ile alacaksan AiService constructor'ını güncelle.
> Yukarıdaki `@Autowired` field injection sadece basitlik için.

---

## Adım 4: application.yml'ye Belge Yolu Ekle

```yaml
# backend/src/main/resources/application.yml
rag:
  document-path: ./data/document.pdf   # veya .txt
```

`backend/data/` klasörü oluştur, yarışmada verilen belgeyi oraya koy.

---

## Adım 5: Endpoint Ekle

`backend/src/main/java/com/hackathon/controller/ApiController.java`:

```java
@PostMapping("/ask")
public ResponseEntity<ApiResponse<Map<String, Object>>> ask(@RequestBody Map<String, String> body) {
    String question = body.get("question");
    if (question == null || question.isBlank()) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("question gerekli"));
    }
    Map<String, Object> result = aiService.answerFromDocument(question);
    return ResponseEntity.ok(ApiResponse.ok(result));
}
```

---

## Adım 6: Test Et

```bash
# Belgeyi data/ klasörüne koy
cp /path/to/belge.pdf backend/data/document.pdf

# Backend'i yeniden başlat
cd backend
mvn spring-boot:run
# → "[RagService] N chunk yüklendi" mesajı görmelisin

# Test et (başka terminal)
curl -X POST http://localhost:3001/api/ask \
  -H "Content-Type: application/json" \
  -d "{\"question\": \"belgede yazılan konu nedir\"}"
```

---

## Dosya Upload ile RAG (Kullanıcı Belge Yüklüyorsa)

Eğer kullanıcı frontend'den belge yüklüyorsa, `/api/upload` endpoint'i zaten hazır.
RAG'i kullanıcı upload'ından doldurmak için:

```java
// ApiController.java
@Autowired private RagService ragService;

@PostMapping("/upload")
public ResponseEntity<ApiResponse<Map<String, Object>>> upload(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "loadToRag", defaultValue = "false") boolean loadToRag
) {
    try {
        String text = documentService.extractText(file);
        if (loadToRag) {
            ragService.loadFromText(text);
        }
        // ... rest
    } catch (Exception e) { ... }
}
```

Frontend tarafında:
```javascript
const formData = new FormData();
formData.append('file', file);
formData.append('loadToRag', 'true');
await axios.post('/api/upload', formData);
// Sonra /api/ask çağrılabilir
```

---

## Sorun Giderme

**"Chunk bulunamadı" — AI "bilgi yok" diyor ama belgede var**
→ Kelime eşleştirme kısmi eşleşmeyi bulamıyor. `scoreChunk`'da kelime uzunluğu filtresini gevşet:
```java
if (w.length() < 2) continue;  // 3 yerine 2
```
veya `findRelevantChunks(query, 5)` → topK'yı artır.

**Yanıt yavaş**
→ `max_tokens`'ı düşür (1024 → 512, `application.yml`).
→ `topK`'yı küçült (3 → 2).
→ Chunk size'ı düşür (500 → 300).

**JSON parse hatası**
→ `RAG_SYSTEM_PROMPT`'a şunu ekle: `"Never use markdown. Never use code fences."`
→ `text.replaceAll(...)` ile `\`\`\`json` temizliği zaten var.

**PDFBox NullPointerException**
→ PDF korumalı veya bozuk olabilir. `pdftotext` (Linux/Mac) veya başka tool ile düz metin TXT'ye çevirip onu yükle.

**Belge yüklenmiyor (app başlangıcında)**
→ `application.yml`'de `rag.document-path` set edilmiş mi?
→ Dosya `backend/data/` altında mı?
→ Backend log'unda hata mesajı var mı?

---

## Hızlı Alternatif: Embedding Olmadan, Tüm Belgeyi Gönder

Belge gerçekten kısaysa (< 20 sayfa, < 30k kelime), kelime arama yerine direkt tüm belgeyi context'e ekle:

```java
public AiResult analyzeWithFullDocument(String userQuestion, String fullDocument) {
    return analyzeWithContext(userQuestion, fullDocument);
}
```

Claude'un context window'u büyük (200k token), kısa belgeler için RAG gereksiz.
