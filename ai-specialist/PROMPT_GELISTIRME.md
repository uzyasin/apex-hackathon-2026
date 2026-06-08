# Prompt Geliştirme Defteri
# AI Model Sorumlusu — bu dosya senin çalışma alanın

Bu dosyayı şu döngüyle kullan:
1. Prompt taslağı yaz
2. `AiService.java`'daki `SYSTEM_PROMPT` constant'ına yapıştır (text block `"""..."""`)
3. Backend'i restart et (`Ctrl+C` → `mvn spring-boot:run`)
4. `curl` ile test et
5. Sonucu buraya not al
6. Düzelt, tekrar test et

---

## Ürün Tanımı (İlk 5 dakikada doldur)

**Ürün ne yapıyor?**
> Kullanıcı sprint/backlog verilerini giriyor, sistem AI ile sprint sağlığı, story point
> tahmini, görev kırılımı, blokaj çözümü ve sprint review raporu döndürüyor.
> (AI Destekli Scrum/Kanban Asistanı ve Yönetim Paneli.)

**AI'dan beklenen temel görev:**
> Analiz + Tahmin + Üretim: sprint analizi, predictive sizing, task decomposition, sprint review.

**Girdi tipi:**
> [x] Serbest metin (sprint/backlog) &nbsp;&nbsp; [x] Form verisi (issue alanları) &nbsp;&nbsp; [ ] Görsel

**Çıktı tipi:**
> [x] Skor + açıklama &nbsp;&nbsp; [x] Öneri listesi &nbsp;&nbsp; [x] Yapısal JSON (DTO başına)

---

## Hızlı Test Komutu

Prompt'u `AiService.SYSTEM_PROMPT`'a yapıştırdıktan sonra backend'i başlat ve test et:

```bash
# Backend'i başlat (terminal 1)
cd backend
mvn spring-boot:run

# Test et (terminal 2)
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"BURAYA TEST GİRDİSİ YAZ\"}"
```

> Spring Boot DevTools eklendiyse kod değişince otomatik restart olur.
> Eklenmediyse her prompt değişiminde `Ctrl+C` → `mvn spring-boot:run` tekrar.

---

## Prompt İterasyon Defteri

### v1 — İlk Deneme

**Yazılış saati:** ___:___

**System Prompt:**
```
[BURAYA YAZ]
```

**Test girdisi:**
```
[BURAYA YAZ]
```

**Gelen yanıt:**
```json
[BURAYA YAZ]
```

**Sorun:**
> [Hangi alan eksik? Format bozuk mu? Halüsinasyon var mı? Türkçe/İngilizce sorunu var mı?]

**Yapılan değişiklik:**
> [Ne değiştirdin ve neden?]

---

### v2 — Düzeltilmiş

**Yazılış saati:** ___:___

**System Prompt:**
```
[BURAYA YAZ]
```

**Test girdisi:**
```
[BURAYA YAZ]
```

**Gelen yanıt:**
```json
[BURAYA YAZ]
```

**Sorun / Not:**
> [Daha iyi mi? Hâlâ sorun var mı?]

---

### v3 — (Gerekirse)

**Yazılış saati:** ___:___

**System Prompt:**
```
[BURAYA YAZ]
```

**Test girdisi:**
```
[BURAYA YAZ]
```

**Gelen yanıt:**
```json
[BURAYA YAZ]
```

---

## ✅ AKTİF PROMPT — AiService.SYSTEM_PROMPT'a bu yapıştırıldı

> Son güncelleme: rebase sonrası — main üzerine kuruldu, 5 prompt aktif.

AiService artık TEK bir SYSTEM_PROMPT yerine özellik başına ayrı text-block sabitleri kullanır:

| Sabit | Endpoint | Çıktı DTO |
|-------|----------|-----------|
| `ANALYZE_PROMPT` | `POST /api/analyze` | `AiResult` |
| `PREDICT_SIZE_PROMPT` | `POST /api/planning/predict-size` | `SizePredictionResponse` |
| `BLOCKER_PROMPT` | `POST /api/planning/blockers` | `BlockerSuggestion` |
| `DECOMPOSE_PROMPT` | `POST /api/tasks/decompose` | `DecomposeResponse` |
| `REVIEW_PROMPT` | `GET /api/sprint/{id}/review` | `SprintReview` |

Tüm prompt'lar ortak `callClaude(systemPrompt, userContent, type, fallback)` jenerik
metodundan geçer (markdown temizleme + JSON parse + DTO fallback tek noktada).

**ANALYZE_PROMPT (FINAL):**
```
Sen bir AI destekli Agile Yönetim Asistanısın (Scrum/Kanban).
Görevin: sprint verilerini, backlog task'larını ve takım kapasitesini analiz ederek
somut, uygulanabilir bir JSON çıktısı üretmek.

YALNIZCA şu JSON nesnesini döndür:
{
  "sprint_health_score": <0-100 tam sayı>,
  "summary": "<2-3 cümle Türkçe özet>",
  "task_breakdown": [
    { "title": "...", "type": "Frontend|Backend|DB|Test|DevOps",
      "story_points": <1,2,3,5,8,13>, "suggested_assignee": "<rol/isim>" }
  ],
  "risks": ["..."],
  "recommendations": ["..."],
  "verdict": "Planlanabilir | Revize Gerekli | Reddedilmeli"
}

Kurallar:
- YALNIZCA JSON döndür. Markdown veya kod bloğu (```) YOK.
- story_points yalnızca Fibonacci (1,2,3,5,8,13).
- Girdi Türkçe ise Türkçe yanıt ver.
- Girdi belirsiz/alakasızsa sprint_health_score=0 yap ve summary'de açıkla.
```

**Java text block örneği:**
```java
private static final String SYSTEM_PROMPT = """
        [FINAL PROMPT BURAYA]
        """;
```

**Onaylı test çıktısı:**
```json
{
  [BURAYA ÇALIŞAN BİR TEST ÇIKTISI YAZ]
}
```

---

## Edge Case Test Listesi

Jüri sunum öncesi bunları elle dene:

| Test Senaryosu | Beklenen Davranış | Sonuç |
|----------------|-------------------|-------|
| Boş girdi gönder | Hata mesajı, çökme yok | ☐ |
| Çok kısa girdi (1-2 kelime) | Anlamlı yanıt veya açıklama | ☐ |
| Çok uzun girdi (2000+ karakter) | Token sınırı aşılmadan çalışıyor | ☐ |
| Türkçe girdi | Tutarlı JSON çıktı | ☐ |
| İngilizce girdi | Tutarlı JSON çıktı | ☐ |
| Alakasız/saçma girdi | Score 0, açıklayıcı summary | ☐ |
| API key yanlışsa | Fallback döner, uygulama çökmez | ☐ |

---

## Prompt Yazarken Altın Kurallar

**1. Rolü net tanımla**
```
❌ "You are an AI assistant..."
✅ "You are a senior HR specialist with 10 years of recruiting experience..."
```

**2. JSON formatını örnekle göster**
```
❌ "Respond with JSON containing score and summary"
✅ "Respond ONLY with this exact JSON:
    { "score": 85, "summary": "..." }
    No markdown. No extra text. No code fences."
```

**3. Kırmızı çizgilerini tanımla**
```
✅ "If the input is unrelated to [konu], set score to 0 and explain in summary."
✅ "Never invent data that isn't in the input."
✅ "Always respond in Turkish." (eğer Türkçe istiyorsan)
```

**4. Token tasarrufu yap (hız için)**
- System prompt 500 kelimeyi geçmesin
- Kullanıcı mesajını gereksiz yere uzatma
- `max_tokens` değerini `application.yml` veya `AiService.java`'da çıktı boyutuna göre ayarla:
  - Kısa JSON → 512
  - Orta JSON → 1024 (default)
  - Uzun metin içeren → 2048
- `application.yml` içinde: `anthropic.max-tokens: 1024`
