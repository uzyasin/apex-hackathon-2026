# Prompt Geliştirme Defteri
# AI Model Sorumlusu — bu dosya senin çalışma alanın

Bu dosyayı şu döngüyle kullan:
1. Prompt taslağı yaz
2. `curl` ile test et
3. Sonucu buraya not al
4. Düzelt, tekrar test et
5. Bitti mi? Final prompt'u `aiService.js`'e kopyala

---

## Ürün Tanımı (İlk 5 dakikada doldur)

**Ürün ne yapıyor?**
> [DOLDUR: Kullanıcı ___ giriyor, sistem ___ döndürüyor]

**AI'dan beklenen temel görev:**
> [DOLDUR: Sınıflandırma mı? Özetleme mi? Analiz mi? Üretim mi?]

**Girdi tipi:**
> [ ] Serbest metin &nbsp;&nbsp; [ ] Belge (PDF/TXT) &nbsp;&nbsp; [ ] Form verisi &nbsp;&nbsp; [ ] Görsel

**Çıktı tipi:**
> [ ] Skor + açıklama &nbsp;&nbsp; [ ] Kategori &nbsp;&nbsp; [ ] Özet &nbsp;&nbsp; [ ] Öneri listesi &nbsp;&nbsp; [ ] Ham metin

---

## Hızlı Test Komutu

Prompt'u `aiService.js`'e yapıştırdıktan sonra backend'i başlat ve test et:

```bash
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "BURAYA TEST GİRDİSİ YAZ"}'
```

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

## ✅ AKTİF PROMPT — aiService.js'e bu yapıştırıldı

> Son güncelleme: ___:___

**System Prompt (FINAL):**
```
[BURAYA FINAL PROMPT'U YAZ — bunu aiService.js'deki SYSTEM_PROMPT'a kopyala]
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
- `max_tokens` değerini `aiService.js`'de çıktı boyutuna göre ayarla:
  - Kısa JSON → 512
  - Orta JSON → 1024
  - Uzun metin içeren → 2048
