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

## Seçilen Sözleşme — AI-Powered Agile Manager

```
ÜRÜNÜMÜZDEKİ AKTİF SÖZLEŞME:
Konu: AI Destekli Scrum/Kanban Asistanı ve Sprint Yönetim Paneli
```

**AI'dan gelen yanıt (POST /api/analyze → response.data):**
```json
{
  "sprint_health_score": 78,
  "summary": "Sprint kapasitesi %85 dolu. Belirlenen 3 task teknik bağımlılık içeriyor, önce Backend alt görevleri tamamlanmalı.",
  "task_breakdown": [
    {
      "title": "Kullanıcı kimlik doğrulama API'si",
      "type": "Backend",
      "story_points": 5,
      "suggested_assignee": "Backend Geliştirici"
    },
    {
      "title": "Login formu ve hata mesajları",
      "type": "Frontend",
      "story_points": 3,
      "suggested_assignee": "Frontend Geliştirici"
    },
    {
      "title": "users tablosu ve migrasyon scripti",
      "type": "DB",
      "story_points": 2,
      "suggested_assignee": "Backend Geliştirici"
    }
  ],
  "risks": [
    "Frontend görevi Backend API tamamlanmadan başlayamaz (bağımlılık).",
    "Takım velocity'si geçen sprint 34 puan — bu sprint 42 puan planlandı, kapasite aşımı riski var."
  ],
  "recommendations": [
    "Backend ve DB görevlerini sprint'in ilk 2 gününe al.",
    "42 puanlık yük 34'e düşürülmeli; en az öncelikli 2 task sonraki sprint'e taşı.",
    "Blokaj durumunda Daily Scrum'da 15 dk ek zaman planla."
  ],
  "verdict": "Revize Gerekli"
}
```

**Alan Tanımları:**

| Alan | Tip | Zorunlu | Açıklama |
|------|-----|---------|----------|
| `sprint_health_score` | integer, 0-100 | E | Sprint sağlığı. 70+ yeşil, 40-69 sarı, <40 kırmızı |
| `summary` | string | E | 2-3 cümlelik Türkçe sprint/görev özeti |
| `task_breakdown` | object[] | E | Görevin teknik alt görev listesi |
| `task_breakdown[].title` | string | E | Alt görev adı |
| `task_breakdown[].type` | string | E | Frontend / Backend / DB / Test / DevOps |
| `task_breakdown[].story_points` | integer (Fibonacci) | E | Tahmin edilen efor (1,2,3,5,8,13) |
| `task_breakdown[].suggested_assignee` | string | E | Önerilen atama (rol veya isim) |
| `risks` | string[] | E | Tespit edilen riskler ve blokajlar |
| `recommendations` | string[] | E | Öncelik sıralı somut aksiyonlar |
| `verdict` | string | E | "Planlanabilir" \| "Revize Gerekli" \| "Reddedilmeli" |

**Frontend'de her alan nerede gösterilecek:**

| Alan | Bileşen | Görünüm |
|------|---------|---------|
| `sprint_health_score` | `SprintHealthGauge` | Büyük yuvarlak skor, renk kodlu (yeşil/sarı/kırmızı) |
| `summary` | `SummaryCard` | Açıklama kutusu, sol üst |
| `task_breakdown` | `TaskBreakdownTable` | Tablo: görev adı, tip rozeti, puan, atanan kişi |
| `risks` | `RiskList` | Kırmızı uyarı ikonlu madde listesi |
| `recommendations` | `RecommendationList` | Mavi öneri ikonlu madde listesi |
| `verdict` | `VerdictBadge` | Büyük renkli rozet (yeşil/sarı/kırmızı) |

---

## Fallback (API Hata Verirse)

Bu değerler `AiResult.java` içindeki `fallback()` static method'una girilir.
Uygulama hiçbir zaman boş ekran göstermez.

```java
public static AiResult fallback() {
    AiResult r = new AiResult();
    r.setSummary("Analiz şu an kullanılamıyor. Lütfen tekrar deneyin.");
    r.setInsights(List.of());
    r.setScore(0);
    r.setRecommendation("Birkaç saniye sonra tekrar dene.");
    return r;
}
```

`AiService.callClaude()` exception yakaladığında otomatik bu fallback'i döner.

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
"Yeni sürümü `AiService.java`'ya yazdım. Çıktı artık `verdict` yerine `decision` kullanıyor.
`AiResult.java` DTO'sunda da alan eklendi. `AI_CIKTI_SOZLESMESI.md` güncellendi.
`ApiController.java`'da `analyze` endpoint'inde alan ismi geçtiyse oraya da bak."

**Backend → AI Uzmanı:**
"Endpoint şimdi `position` alanını da gönderecek. Prompt'una ekler misin?"

**Frontend → AI Uzmanı:**
"Jüri soruyor: Türkçe girdide Türkçe mi yanıt vermeli? Prompt'a ekle."
