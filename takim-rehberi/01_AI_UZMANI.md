# 🧠 AI Model Uzmanı — Detaylı Rehber

> Bu rehber **AI Model Uzmanı** içindir.
> Diğer roller: [Backend](02_BACKEND.md) · [Frontend](03_FRONTEND.md) · [Git Yöneticisi](04_GIT_YONETICISI.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **yapay zeka beyni**sin. Görevin:
- Claude'a "ne yapması gerektiğini" söyleyen **system prompt**'u yazmak (`AiService.SYSTEM_PROMPT`)
- AI çıktısının her seferinde **doğru JSON formatında** gelmesini garanti etmek
- Backend ve Frontend'in beklediği **veri sözleşmesini** tanımlamak
- AI hata verirse **fallback yanıt** (`AiResult.fallback()`) ile uygulamayı çalışır tutmak

Kısaca: Backend "endpoint'i çalıştır" diyecek, sen onun çağıracağı metodun (`AiService.analyze`) **mükemmel cevap döndürdüğünden** emin olacaksın.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + Java Spring Boot backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Java 21 + Spring Boot 3.2.5 + Maven
- **Frontend:** React 18 + Vite + Tailwind CSS
- **AI:** Anthropic Claude — `claude-sonnet-4-6` (senin yöneteceğin)
- **DB:** H2 in-memory + JdbcTemplate
- **Git:** Her rol kendi branch'inde, FS yöneticisi merge'leri yapar

---

## 2. ORTAK: 4 Saatlik Zaman Planı

```
[0:00–0:30]  Fikir + kapsam + görev dağılımı
[0:30–3:00]  Tam odak kodlama (MVP)
[3:00–3:45]  Entegrasyon + uçtan uca test
[3:45–4:00]  Git push + README + teslim
```

**Senin için kritik anlar:**
- **0:10:** AI çıktı sözleşmesi hazır olmalı (`ai-specialist/AI_CIKTI_SOZLESMESI.md`)
- **0:45:** v1 prompt test edilmiş ve çalışıyor olmalı
- **2:30:** Final prompt hazır, edge case'ler kapanmış
- **3:00:** Yeni özellik yok, sadece destek

---

## 3. ORTAK: Tüm Roller

| Rol | Sorumluluk | Dokunduğu |
|-----|-----------|-----------|
| **AI Uzmanı (sen)** | Prompt, AiService, çıktı sözleşmesi | `ai-specialist/`, `AiService.java`, `AiResult.java`, `3_ai_prompts.md` |
| **Backend** | Endpoint'ler, DB, file upload | `ApiController.java`, `DbService.java`, `DocumentService.java` |
| **Frontend** | UI | `frontend/src/` |
| **FS Yöneticisi** | Git, merge, koordinasyon | Her yer (dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar

- [ ] Boilerplate'i clone'la
- [ ] `ANTHROPIC_API_KEY` env variable'ı set et (FS Yöneticisi anahtarı paylaşacak)
  - Windows: `$env:ANTHROPIC_API_KEY="sk-ant-..."`
  - Linux/Mac: `export ANTHROPIC_API_KEY=sk-ant-...`
- [ ] `cd backend && mvn dependency:resolve` — Maven bağımlılıklarını önceden indir (~3-5 dk)
- [ ] `mvn spring-boot:run` — başlatabildiğini doğrula, port 3001'de çalışıyor olmalı
- [ ] `curl -X POST localhost:3001/api/analyze -d "{\"input\":\"test\"}" -H "Content-Type: application/json"` — gerçek API'ye dokunduğunu gör
- [ ] Java 21 kurulu mu? `java -version` ile kontrol
- [ ] Anthropic dokümantasyonunu hızlıca tara: https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering
- [ ] `ai-specialist/PROMPT_GELISTIRME.md` ve `AI_CIKTI_SOZLESMESI.md` dosyalarını gözden geçir
- [ ] IDE setup: IntelliJ Community veya VS Code + Extension Pack for Java

**API anahtarı kim açacak?** Takımda netleştir. Tek ortak anahtar = hız. Limit/güvenlik kaygısı varsa her kişi kendi anahtarını açar.

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika

```bash
# Konu açıklandıktan sonra:
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout feat/ai

# API key set et
$env:ANTHROPIC_API_KEY="sk-ant-..."   # PowerShell
# veya: export ANTHROPIC_API_KEY=sk-ant-...

# Backend'i ayağa kaldır
cd backend
mvn spring-boot:run
# → port 3001'de çalışıyor olmalı, "Started HackathonApplication" mesajı görmelisin
```

Backend ayakta, sen prompt yazmaya hazırsın.

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **AI Çıktı Sözleşmesi:** AI'ın döneceği JSON yapısını ilk 20 dakikada netleştir, ekiple paylaş
2. **System Prompt:** `AiService.SYSTEM_PROMPT` constant'ını yaz, iteratif geliştir
3. **AiResult DTO:** Eğer şema değişirse `AiResult.java` alanlarını güncelle
4. **Test:** Her prompt versiyonunu `curl` ile test et
5. **Fallback:** `AiResult.fallback()` static method'unu güncel tut
6. **RAG:** Konu belge analizi gerektiriyorsa `RAG_KURULUM.md` rehberini uygula

### Yan Görevler
- Backend'in `AiService`'i doğru çağırıp çağırmadığını kontrol et
- Frontend'in AI çıktısının her alanını doğru render ettiğini gör
- Jüri sunumunda AI tarafını anlat

---

## 7. SENIN: Dokunduğun Dosyalar

```
backend/src/main/java/com/hackathon/service/AiService.java   ← Ana çalışma alanın
backend/src/main/java/com/hackathon/dto/AiResult.java        ← AI çıktı DTO (şema değişirse güncelle)
ai-specialist/PROMPT_GELISTIRME.md                            ← Prompt iterasyon defterin
ai-specialist/AI_CIKTI_SOZLESMESI.md                          ← Veri sözleşmesi (DOLDUR, EKİBE PAYLAŞ)
ai-specialist/RAG_KURULUM.md                                  ← Gerekirse uygula
AI_CONTEXT/3_ai_prompts.md                                    ← Prompt arşivi
```

**Dokunmayacağın yerler:**
- `backend/src/main/java/com/hackathon/controller/` (Backend'in)
- `backend/src/main/java/com/hackathon/service/DbService.java` (Backend'in)
- `frontend/` (Frontend'in)

İstisna: Backend tıkanırsa endpoint yazımına destek olabilirsin (kendi branch'inde değil, ona söyle).

---

## 8. SENIN: Adım Adım Timeline

### 🟢 [0:00 – 0:20] Konu Analizi ve Sözleşme

**Hedef:** AI'ın ne yapacağını netleştir, Backend/Frontend için JSON sözleşmesini çıkar.

```bash
# Adım 1: Branch'ine geç
git checkout feat/ai
git pull origin feat/ai

# Adım 2: Sözleşme dosyasını aç
code ai-specialist/AI_CIKTI_SOZLESMESI.md
```

**Doldurulacak alanlar (10 dk):**

```markdown
## Seçilen Sözleşme

ENDPOINT: POST /api/analyze
REQUEST:
{
  "input": "kullanıcı metni",
  "context": null veya string
}

RESPONSE.data:
{
  "summary": "1 cümle özet",
  "insights": ["string", "string"],
  "score": integer 0-100,
  "recommendation": "string"
}
```

**Eğer şema değiştiyse:** `backend/src/main/java/com/hackathon/dto/AiResult.java` dosyasını da güncelle. Örnek alan ekleme:

```java
@Data @NoArgsConstructor @JsonIgnoreProperties(ignoreUnknown = true)
public class AiResult {
    private String summary;
    private List<String> insights;
    private int score;
    private String recommendation;
    private String verdict;  // ← YENİ ALAN
    ...
}
```

**Bildirim (kritik!):** Ekibe yüksek sesle veya WhatsApp'a yaz:
> "Arkadaşlar, AI sözleşmesi hazır: `summary, insights, score, recommendation`. AiResult DTO güncel. Backend ve Frontend buna göre kodlayın. `ai-specialist/AI_CIKTI_SOZLESMESI.md`'de detayı var."

**Commit:**
```bash
git add ai-specialist/AI_CIKTI_SOZLESMESI.md backend/src/main/java/com/hackathon/dto/AiResult.java
git commit -m "feat(ai): output contract tanımlandı"
git push
```

---

### 🟢 [0:20 – 1:00] İlk Prompt (v1) ve Test

**Hedef:** Çalışan ilk prompt, gerçek AI yanıtı al.

```bash
code ai-specialist/PROMPT_GELISTIRME.md
```

**v1 Şablonu:**
```
You are an expert [ROL — örn: senior HR specialist].

You will receive [GİRDİ TÜRÜ — örn: a CV text].
Your task: [GÖREV — örn: evaluate suitability for the [position] role].

Respond ONLY with this exact JSON structure:
{
  "summary": "<one sentence>",
  "insights": ["<insight 1>", "<insight 2>"],
  "score": <integer 0-100>,
  "recommendation": "<clear next action>"
}

CRITICAL RULES:
- Output ONLY the JSON. No markdown. No code fences. No explanation.
- If input is unclear, set score to 0 and explain in summary.
- Respond in Turkish.
```

**AiService.java'ya yapıştır:**
```bash
code backend/src/main/java/com/hackathon/service/AiService.java
# SYSTEM_PROMPT text block'unu (""" ... """) yukarıdaki ile değiştir
```

**Backend'i restart et:**
```bash
# Terminal 1'de Ctrl+C, sonra:
cd backend
mvn spring-boot:run
# Spring Boot DevTools eklendiyse otomatik restart olur
```

**Test (curl):**
```bash
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"Yasin, 5 yıl Java geliştirici, Spring Boot ve PostgreSQL biliyor\"}"
```

**Beklenen:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "summary": "...",
    "insights": [...],
    "score": 75,
    "recommendation": "..."
  }
}
```

**Sorun varsa:**
- JSON parse hatası → System prompt'a "No markdown. No code fences." ekle
- Türkçe yerine İngilizce dönüyor → "Respond in Turkish" ekle
- Saçma cevap → Role tanımını güçlendir, örnek ekle
- `set-via-env-var` yanıt geliyor → `ANTHROPIC_API_KEY` env variable yok, set et ve backend'i restart

**Commit:**
```bash
git add ai-specialist/ backend/src/main/java/com/hackathon/service/AiService.java
git commit -m "feat(ai): v1 prompt - temel analiz"
git push
```

---

### 🟢 [1:00 – 2:30] Prompt İyileştirme Döngüsü

**Hedef:** Her edge case'i kapsayan, tutarlı çıktı veren prompt.

**Test Listesi (her birini dene):**

| Senaryo | Girdi | Beklenen |
|---------|-------|----------|
| Normal | "Yasin, 5 yıl Java geliştirici" | score 60-80 arası, tutarlı |
| Çok kısa | "Yasin" | score düşük, summary açıklayıcı |
| Boş | "" | Backend 400 dönmeli, AI'ya gitmemeli |
| Alakasız | "Pizza tarifi nasıl yapılır" | score 0, summary "alakasız" |
| Çok uzun | 2000 kelimelik metin | Çalışmalı, token limiti aşmamalı |
| Karışık dil | Türkçe + İngilizce | Tutarlı yanıt |

Her testi `PROMPT_GELISTIRME.md` defterine kaydet (v2, v3, v4...).

**Yaygın iyileştirmeler:**

1. **Daha sıkı JSON zorlama:**
   ```
   Your entire response must be a valid JSON object starting with { and ending with }.
   Do not include ```json or ``` markers.
   ```

2. **Few-shot örnek ekle (token israfı ama tutarlılık artar):**
   ```
   Example output:
   {"summary": "Güçlü teknik beceri, liderlik eksik", "insights": ["..."], "score": 85, "recommendation": "..."}
   ```

3. **Edge case'leri açıkça yönet:**
   ```
   If the input is empty, irrelevant, or unclear:
   {"summary": "Geçerli bir girdi sağlanmadı", "insights": [], "score": 0, "recommendation": "Lütfen geçerli bir girdi sağlayın"}
   ```

**Her büyük iyileştirme commit edilir:**
```bash
git add ai-specialist/ backend/src/main/java/com/hackathon/service/AiService.java
git commit -m "feat(ai): v2 prompt - JSON zorlama güçlendirildi"
git push
```

---

### 🟢 [2:30 – 3:00] Final Prompt + Entegrasyon Desteği

**Hedef:** Final prompt yerinde, uçtan uca akış çalışıyor.

```bash
# Final prompt'u PROMPT_GELISTIRME.md'de "AKTİF PROMPT" bölümüne yaz
code ai-specialist/PROMPT_GELISTIRME.md

# AiService.java'nın bu versiyona uyumlu olduğunu doğrula
code backend/src/main/java/com/hackathon/service/AiService.java
```

**Uçtan uca test (Frontend → Backend → AI):**
1. Frontend'i aç (http://localhost:5173)
2. Gerçek bir girdi yaz, gönder
3. Sonuç ekranda görünüyor mu?
4. Tüm alanlar (summary, insights, score, recommendation) doğru gösteriliyor mu?

Sorun varsa: Frontend'in `HomePage.jsx`'ine bak, AI'dan gelen alanı doğru kullanıyor mu?

**Final commit:**
```bash
git add .
git commit -m "feat(ai): final prompt - tüm edge case'ler kapandı"
git push
```

**FS yöneticisine bildir:** "AI branch'i merge için hazır."

---

### 🟢 [3:00 – 4:00] Sunum Hazırlığı ve Cila

**Hedef:** Sunumda anlatacaklarına hazırlan, son düzeltmeleri yap.

#### Sunum Notların (1.5 dakika)

> "Projemizin akıllı kısmını Anthropic'in Claude Sonnet 4.6 modeli üzerine kurduk.
> Bu modeli seçmemizin nedeni: [domain]'de karmaşık metinleri analiz edebilmesi.
>
> AI mimarisinde 3 önemli karar aldık:
> 1. **Yapısal JSON çıktı** — AI'ya cevabını kesin bir formatta vermesi için sıkı kurallar koyduk
> 2. **Fallback mekanizması** — `AiResult.fallback()` ile API yavaşlarsa veya hata verirse uygulamamız çökmüyor
> 3. **Prompt mühendisliği** — [domain]'e özel rol tanımıyla model performansını optimize ettik"

#### Son Dakika Cilalama (DİKKATLİ!)
- Sadece tutarsız davranışı düzelt
- Çalışan bir şeyi BOZMAK için yeni özellik ekleme
- Şüphedeysen: dokunma

---

## 9. SENIN: Sık Karşılaştığın Hatalar

### Hata 1: AI markdown ile JSON dönüyor

**Belirti:**
```json
{"data": "```json\n{\"score\": 85,...}\n```"}
```

**Çözüm:** System prompt'a ekle:
```
Your response MUST start with { and end with }.
DO NOT use markdown formatting.
DO NOT use code fences like ```json or ```.
```

`AiService.callClaude()` zaten markdown'ı temizliyor:
```java
text = text.replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)^```\\s*", "").replaceAll("\\s*```$", "");
```

---

### Hata 2: AI alakasız alanlar ekliyor

**Belirti:**
```json
{
  "summary": "...",
  "score": 85,
  "explanation": "Ekstra alan",  ← bunu istemiyorsun
  "insights": [...]
}
```

**Çözüm:** `AiResult` DTO'sunda `@JsonIgnoreProperties(ignoreUnknown = true)` zaten var — alakasız alanlar yutuluyor.
Ama temizliği için prompt'a:
```
Your response must contain EXACTLY these 4 keys: summary, insights, score, recommendation.
Do not add any other keys.
```

---

### Hata 3: Token limiti aşımı (uzun çıktı)

**Belirti:** Yanıt yarıda kesiliyor, JSON parse hatası, fallback dönüyor.

**Çözüm:** `application.yml`'de `anthropic.max-tokens`'ı artır:
```yaml
anthropic:
  max-tokens: 2048   # 1024'ten 2048'e
```

Veya prompt'a ekle:
```
Keep "summary" under 200 characters.
Keep each "insights" item under 100 characters.
Maximum 5 items in "insights".
```

---

### Hata 4: API rate limit (429)

**Belirti:**
```
[AiService] error: 429 Too Many Requests
```

**Çözüm:**
- Anthropic dashboard'da limit'i kontrol et
- 1 dk bekle ve tekrar dene
- Yedek API key'e geç (FS yöneticisinden iste)
- `AiService.callClaude()`'a retry mekanizması:
  ```java
  for (int i = 0; i < 2; i++) {
      try {
          ResponseEntity<Map> response = restTemplate.postForEntity(...);
          // ... process and return
      } catch (HttpClientErrorException.TooManyRequests e) {
          Thread.sleep(2000);
          continue;
      }
  }
  return AiResult.fallback();
  ```

---

### Hata 5: AI Türkçe değil İngilizce yanıt veriyor

**Çözüm:** System prompt'un en başına ve sonuna:
```
You MUST respond entirely in Turkish (Türkçe).
All field values (summary, insights, recommendation) must be in Turkish.
```

---

### Hata 6: "set-via-env-var" yanıtı geliyor

**Sebep:** `ANTHROPIC_API_KEY` env variable set edilmedi, `application.yml` default'u kullanılıyor.

**Çözüm:**
1. Terminal'i kapat
2. Env variable set et:
   ```bash
   $env:ANTHROPIC_API_KEY="sk-ant-..."   # PowerShell
   ```
3. **Aynı terminal'de** `mvn spring-boot:run` çalıştır

---

### Hata 7: AiService değişikliği reload olmuyor

**Çözüm:** Spring Boot DevTools eklenmediyse her değişiklik için backend'i durdurup tekrar başlatman gerek.

Pom.xml'e ekle (varsa atla):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Sonra IDE'de "Auto Build" / "Auto Compile" aç → değişikliklerde otomatik restart.

---

## 10. SENIN: Demo Hazırlığı

### Sunumda göstereceğin 3 şey:

1. **AI'nın "akıllı" davranışı**
   - Birkaç farklı girdi ile arayüzde dene
   - Skor değişiyor, summary anlamlı

2. **Edge case kontrolü**
   - Boş veya alakasız girdi ver → uygulama çökmüyor, anlamlı yanıt veriyor
   - "Bakın, `AiResult.fallback()` mekanizmamız çalışıyor"

3. **Prompt mühendisliği**
   - `AiService.java`'yı aç, `SYSTEM_PROMPT` text block'unu göster
   - "Buradaki rol tanımı ve sıkı JSON kuralları sayesinde tutarlı çıktı alıyoruz"

### Soru-cevap için hazırlık:
- **"Hangi model?"** → Anthropic Claude Sonnet 4.6
- **"Neden bu model?"** → [Domain]'e uygun, hızlı, JSON çıktısı tutarlı
- **"Maliyet?"** → Token başına çok düşük, hackathon için yeterli
- **"Fine-tune yaptınız mı?"** → Hayır, sıfırdan eğitmek 4 saatte mümkün değil. Bunun yerine prompt mühendisliği ile model davranışını yönettik.
- **"RAG kullandınız mı?"** → [Evet/Hayır, kullandıysan PDFBox + in-memory chunking yaklaşımı]

---

## 11. ORTAK: Git İş Akışı

### Branch Stratejisi
```
main                           ← FS yöneticisi merge eder
├── feat/ai                    ← SENİN BRANCH'İN
├── feat/backend
└── feat/frontend
```

### Senin Günlük Akış
```bash
# Sabah
git checkout feat/ai
git pull origin feat/ai

# Çalışırken her ~30 dk
git add ai-specialist/ backend/src/main/java/com/hackathon/service/AiService.java backend/src/main/java/com/hackathon/dto/AiResult.java
git commit -m "feat(ai): [ne değişti]"
git push

# main'den güncellemeleri al (FS merge yaptıktan sonra)
git checkout feat/ai
git merge main      # Veya: git pull origin main --rebase
```

### Commit Mesaj Formatı
```
feat(ai): v2 prompt — JSON format güçlendirildi
fix(ai): Türkçe yanıt zorlaması eklendi
docs(ai): output contract güncellendi
```

### Yasak Komutlar
- `git push --force` — asla
- `git reset --hard` — asla (kendi dosyanı kaybetmek istemiyorsan)
- `git checkout main` ve direkt commit — sadece FS yapar

---

## 12. ORTAK: Takım İletişim Protokolü

### Senin Bildirmen Gerekenler

| Olay | Kime | Mesaj Örneği |
|------|------|--------------|
| AI sözleşmesi hazır | Backend, Frontend | "Sözleşme: summary, insights, score, recommendation — `AI_CIKTI_SOZLESMESI.md`. AiResult DTO da güncel." |
| Sözleşme değişti | Backend, Frontend, FS | "Sözleşmeye `confidence` alanı ekledim, AiResult.java'da da var" |
| v1 prompt çalıştı | Tüm ekip | "v1 hazır, curl ile test ettim, /api/analyze çalışıyor" |
| Final prompt | FS | "AI branch merge için hazır" |
| Tıkandım | Tüm ekip | "Token limiti aşımı var, prompt'u kısaltıyorum, 10 dk sürer" |

### Sana Bildirilenler
- Backend: "AiService.analyze metodunu çağırıyorum, doğru mu?"
- Frontend: "summary alanı bazen boş geliyor, bakar mısın"
- FS: "Merge yapıyorum, 2 dk commit atma"

### Saatlik Sync (FS yönetir)
Her saat 2 dakika dur:
- Ne yaptın
- 30 dk sonra ne yapacaksın
- Tıkanmış mısın

---

## 13. ORTAK: Acil Durum

### Senaryo 1: Anthropic API tamamen kapalı
**Çözüm:**
- Status sayfasını kontrol et: status.anthropic.com
- Yedek olarak OpenAI veya Gemini'ye hızlıca geç (varsa anahtarı)
- `AiService.java`'yı yeniden HTTP endpoint'e göre yaz (~15 dk):
  - OpenAI: `https://api.openai.com/v1/chat/completions`
  - Authorization header: `Bearer sk-...`
  - Body formatı farklı, dikkat et
- Son çare: Mock yanıt döndür (`AiService.analyze()` içinde hard-coded `AiResult`), demo'da "AI offline" notu ile aç

### Senaryo 2: WiFi koptu
**Çözüm:**
- Telefon hotspot
- Backend ve frontend offline çalışıyor (sadece AI çağrısı dış servis)
- H2 in-memory DB her şeyi tutmaya devam eder

### Senaryo 3: Laptop çöktü
**Çözüm:**
- En son push'unu hatırla → başka laptop'tan clone'la → devam et
- Bu yüzden 30 dk'da bir push yapıyorsun

### Senaryo 4: Prompt asla tutarlı çalışmıyor (saatler geçti)
**Çözüm:**
- MVP'yi daralt — daha basit bir çıktı şemasına dön
- "summary" + "score" yeter, "insights" gerekmez
- Çalışmayan kısımları AiResult'tan kaldır, çalışanı parlat

### Senaryo 5: Backend hiç çalışmıyor (Backend takıldı)
**Çözüm:**
- Backend'ciye yardıma git, kendi branch'ini bekleyebilir
- Çoğunlukla Maven dependency, port çakışması veya CORS sorunudur

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce şunlar tamam olmalı:

- [ ] `feat/ai` branch'in `main`'e merge edilmiş
- [ ] `main` üzerinde son `AiService.java` çalışıyor
- [ ] `ai-specialist/AI_CIKTI_SOZLESMESI.md` final hâliyle commit edilmiş
- [ ] `ai-specialist/PROMPT_GELISTIRME.md` final prompt yazılmış
- [ ] Test girdileri ile uçtan uca akış başarılı
- [ ] `application.yml` git'e gitmemiş eğer API key direkt yazıldıysa — ASLA
- [ ] README'de "Kullanılan AI: Anthropic Claude" yazısı var (FS yazacak ama hatırlat)

### Senin son commit'in
```bash
git checkout feat/ai
git status                  # uncommitted bir şey kalmamış mı?
git log --oneline -5        # son commit'lerin tamam mı?
git push origin feat/ai     # son push
```

---

## 15. Hızlı Referans — Kodlama Sırasında Sürekli Açık Tut

### Anthropic HTTP çağrısı (Java, referans)
```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.set("x-api-key", apiKey);
headers.set("anthropic-version", "2023-06-01");

Map<String, Object> body = Map.of(
    "model", "claude-sonnet-4-6",
    "max_tokens", 1024,
    "system", SYSTEM_PROMPT,
    "messages", List.of(Map.of("role", "user", "content", userInput))
);

HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
ResponseEntity<Map> response = restTemplate.postForEntity(
    "https://api.anthropic.com/v1/messages", request, Map.class
);

List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.getBody().get("content");
String text = ((String) contentList.get(0).get("text")).trim();
```

### Java text block (system prompt için)
```java
private static final String SYSTEM_PROMPT = """
        You are an expert HR specialist.
        Respond ONLY with JSON:
        {
          "summary": "...",
          "score": 0-100
        }
        """;
```

### Test komutları
```bash
# Hızlı test
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"test\"}"

# Context ile (RAG)
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"test\", \"context\": \"ek belge metni\"}"
```

### Backend restart
```bash
# Terminal'de Ctrl+C ile durdur
cd backend
mvn spring-boot:run
```

### `/ship` komutu (Claude Code kullanıyorsan)
```
/ship AiService.java'ya analyzeWithImage metodu ekle, görsel kabul eden Claude vision çağrısı yapacak
```
4 agent (planner→coder→tester→reviewer) otomatik çalışır.

---

**Başarılar! AI tarafını sen kontrol ediyorsun. 🧠**
