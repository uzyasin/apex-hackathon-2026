# AI Çıktı Sözleşmesi
# AI Uzmanı ↔ Backend ↔ Frontend Veri Anlaşması

Bu dosya ekipte herkesin aynı veri yapısını konuşmasını sağlar.
AI uzmanı bu dosyayı doldurur → Backend ve Frontend buna göre kod yazar.
Yarışmanın ilk 15-20 dakikasında bu dosyayı ekiple birlikte netleştir.

---

## Ana Sözleşme — AI'dan Gelen Veri

```
⚠️ YARIŞMA BAŞINDA DOLDUR
```

### Endpoint: POST /api/analyze

**Backend'e giden istek (Frontend → Backend):**
```json
{
  "input": "kullanıcının girdiği metin",
  "context": "opsiyonel ek bağlam (RAG varsa dolu gelir)"
}
```

**AI'dan gelen ve backend'in frontend'e ilettiği yanıt:**
```json
{
  "success": true,
  "data": {
    "[ALAN_1]": "[tip ve açıklama — ÖRN: score: integer 0-100]",
    "[ALAN_2]": "[tip ve açıklama — ÖRN: summary: string]",
    "[ALAN_3]": "[tip ve açıklama — ÖRN: recommendation: string]",
    "[ALAN_4]": "[tip ve açıklama — ÖRN: tags: string[]]"
  }
}
```

---

## Örnek Doldurmalar (Referans için)

Ürüne göre aşağıdakilerden birini sil ve kullan, geri kalanı sil.

### Örnek A — Skor + Analiz (CV, makale, proje değerlendirme)
```json
{
  "success": true,
  "data": {
    "score": 85,
    "summary": "Aday güçlü teknik beceriye sahip ancak liderlik deneyimi eksik.",
    "strengths": ["Java bilgisi", "5 yıl deneyim"],
    "gaps": ["Liderlik deneyimi yok", "Python bilinmiyor"],
    "verdict": "Mülakata Çağır"
  }
}
```

**Alan Tanımları:**

| Alan | Tip | Açıklama | Frontend'de Ne Gösterir |
|------|-----|----------|------------------------|
| `score` | integer, 0-100 | Genel uygunluk skoru | Büyük sayı, renk kodlu |
| `summary` | string | Bir paragraf genel değerlendirme | Açıklama metni |
| `strengths` | string[] | Güçlü yönler listesi | Yeşil madde işaretleri |
| `gaps` | string[] | Eksik yönler listesi | Kırmızı madde işaretleri |
| `verdict` | string | Nihai karar | Buton / etiket |

---

### Örnek B — Sınıflandırma (Kategori tespiti, duygu analizi)
```json
{
  "success": true,
  "data": {
    "category": "Şikayet",
    "confidence": 92,
    "reason": "Metin olumsuz deneyim içeriyor ve çözüm talep ediyor.",
    "suggested_action": "Müşteri hizmetleri ekibine yönlendir"
  }
}
```

| Alan | Tip | Açıklama |
|------|-----|----------|
| `category` | string | Tespit edilen kategori |
| `confidence` | integer, 0-100 | Modelin güven skoru |
| `reason` | string | Neden bu kategori seçildi |
| `suggested_action` | string | Önerilen adım |

---

### Örnek C — RAG / Soru-Cevap (Belgeye dayalı)
```json
{
  "success": true,
  "data": {
    "answer": "Sözleşme 31 Aralık 2025'te sona erer.",
    "source_quote": "...geçerlilik süresi 31.12.2025 tarihi itibarıyla...",
    "confidence": "high"
  }
}
```

| Alan | Tip | Açıklama |
|------|-----|----------|
| `answer` | string | Belgeden bulunan cevap |
| `source_quote` | string | Cevabı destekleyen alıntı |
| `confidence` | "high" / "medium" / "low" | Cevap güveni |

---

## Seçilen Sözleşme (Burası Doldurulacak)

```
ÜRÜNÜMÜZDEKİ AKTİF SÖZLEŞME:
```

**Alan adları ve tipleri:**

| Alan | Tip | Zorunlu | Açıklama |
|------|-----|---------|----------|
| `[ALAN_1]` | [tip] | [E/H] | [açıklama] |
| `[ALAN_2]` | [tip] | [E/H] | [açıklama] |
| `[ALAN_3]` | [tip] | [E/H] | [açıklama] |

**Frontend'de her alan nerede gösterilecek:**

| Alan | Bileşen | Görünüm |
|------|---------|---------|
| `[ALAN_1]` | [component adı] | [ÖRN: büyük skor sayısı] |
| `[ALAN_2]` | [component adı] | [ÖRN: açıklama kutusu] |

---

## Fallback (API Hata Verirse)

Bu değerler `aiService.js`'deki `FALLBACK_RESPONSE` sabitine girilir.
Uygulama hiçbir zaman boş ekran göstermez.

```json
{
  "[ALAN_1]": [varsayılan değer],
  "[ALAN_2]": "Analiz şu an kullanılamıyor. Lütfen tekrar deneyin.",
  "[ALAN_3]": []
}
```

---

## Değişiklik Geçmişi

| Saat | Değişiklik | Kim |
|------|-----------|-----|
| __:__ | İlk sözleşme oluşturuldu | AI Uzmanı |
| __:__ | [değişiklik] | [isim] |

> Her sözleşme değişikliğini buraya not al.
> Backend ve Frontend sana "alan neden değişti?" diye sormak zorunda kalmasın.

---

## Ekip İletişim Protokolü

**AI Uzmanı → Backend:**
"Yeni sürümü `aiService.js`'e yazdım. Çıktı artık `verdict` yerine `decision` kullanıyor.
`AI_CIKTI_SOZLESMESI.md` güncellendi. Senin `api.js`'de bir değişiklik gerekmez,
alan adını sadece frontend'de güncellemek yeterli."

**Backend → AI Uzmanı:**
"Endpoint şimdi `position` alanını da gönderecek. Prompt'una ekler misin?"

**Frontend → AI Uzmanı:**
"Jüri soruyor: Türkçe girdide Türkçe mi yanıt vermeli? Prompt'a ekle."
