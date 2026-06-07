# 🧠 AI Model Uzmanı — Detaylı Rehber

> Bu rehber **AI Model Uzmanı** içindir.
> Diğer roller: [Backend](02_BACKEND.md) · [Frontend](03_FRONTEND.md) · [Git Yöneticisi](04_GIT_YONETICISI.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **yapay zeka beyni**sin. Görevin:
- Claude'a "ne yapması gerektiğini" söyleyen **system prompt**'u yazmak
- AI çıktısının her seferinde **doğru JSON formatında** gelmesini garanti etmek
- Backend ve Frontend'in beklediği **veri sözleşmesini** tanımlamak
- AI hata verirse **fallback yanıt** ile uygulamayı çalışır tutmak

Kısaca: Backend "endpoint'i çalıştır" diyecek, sen onun çağıracağı fonksiyonun **mükemmel cevap döndürdüğünden** emin olacaksın.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + (Node.js veya Java) backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Node.js 20 + Express (önerilen) veya Java 21 + Spring Boot
- **Frontend:** React 18 + Vite + Tailwind CSS
- **AI:** Anthropic Claude — `claude-sonnet-4-6` (senin yöneteceğin)
- **DB:** SQLite (Node) veya H2 (Java) — sıfır config
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
| **AI Uzmanı (sen)** | Prompt, aiService, çıktı sözleşmesi | `ai-specialist/`, `aiService.js`, `3_ai_prompts.md` |
| **Backend** | Endpoint'ler, DB | `backend-node/src/routes/`, `services/` (AI dışı) |
| **Frontend** | UI | `frontend/src/` |
| **FS Yöneticisi** | Git, merge, koordinasyon | Her yer (dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar

- [ ] Boilerplate'i clone'la, `cp .env.example .env` yap
- [ ] `.env` içine `ANTHROPIC_API_KEY=sk-ant-...` yaz (FS Yöneticisi paylaşacak)
- [ ] `cd backend-node && npm install && npm run dev` — başlatabildiğini doğrula
- [ ] `curl -X POST localhost:3001/api/analyze -d '{"input":"test"}' -H "Content-Type: application/json"` — gerçek API'ye dokunduğunu gör
- [ ] Anthropic dokümantasyonunu hızlıca tara: https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering
- [ ] `ai-specialist/PROMPT_GELISTIRME.md` ve `AI_CIKTI_SOZLESMESI.md` dosyalarını gözden geçir
- [ ] Cursor veya VS Code kurulu olsun — Claude'a prompt yazmak için

**API anahtarı kim açacak?** Takımda netleştir. Tek ortak anahtar = hız. Limit/güvenlik kaygısı varsa her kişi kendi anahtarını açar.

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika

```bash
# Konu açıklandıktan sonra:
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout feat/ai

# .env'yi oluştur
cp .env.example .env
# .env dosyasını aç, ANTHROPIC_API_KEY'i yaz

# Backend'i ayağa kaldır
cd backend-node
npm install   # eğer dün npm install yapmadıysan
npm run dev   # → port 3001'de çalışıyor olmalı

# Başka terminal — health check
curl http://localhost:3001/health
```

Backend ayakta, sen prompt yazmaya hazırsın.

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **AI Çıktı Sözleşmesi:** AI'ın döneceği JSON yapısını ilk 20 dakikada netleştir, ekiple paylaş
2. **System Prompt:** `aiService.js`'deki `SYSTEM_PROMPT`'u yaz, iteratif geliştir
3. **Test:** Her prompt versiyonunu `curl` ile test et
4. **Fallback:** API hata verirse ne döneceğini netleştir
5. **RAG:** Konu belge analizi gerektiriyorsa `RAG_KURULUM.md` rehberini uygula

### Yan Görevler
- Backend'in `aiService.js`'i doğru çağırıp çağırmadığını kontrol et
- Frontend'in AI çıktısının her alanını doğru render ettiğini gör
- Jüri sunumunda AI tarafını anlat

---

## 7. SENIN: Dokunduğun Dosyalar

```
backend-node/src/services/aiService.js    ← Ana çalışma alanın
ai-specialist/PROMPT_GELISTIRME.md        ← Prompt iterasyon defterin
ai-specialist/AI_CIKTI_SOZLESMESI.md      ← Veri sözleşmesi (DOLDUR, EKİBE PAYLAŞ)
ai-specialist/RAG_KURULUM.md              ← Gerekirse uygula
AI_CONTEXT/3_ai_prompts.md                ← Prompt arşivi
```

**Dokunmayacağın yerler:**
- `backend-node/src/routes/api.js` (Backend'in)
- `backend-node/src/services/dbService.js` (Backend'in)
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
  "score": integer 0-100,
  "summary": "1 cümle özet",
  "verdict": "Onayla" veya "Reddet",
  "details": ["string", "string"]
}
```

**Bildirim (kritik!):** Ekibe yüksek sesle veya WhatsApp'a yaz:
> "Arkadaşlar, AI sözleşmesi hazır: `score, summary, verdict, details`. Backend ve Frontend buna göre kodlayın. `ai-specialist/AI_CIKTI_SOZLESMESI.md`'de detayı var."

**Commit:**
```bash
git add ai-specialist/AI_CIKTI_SOZLESMESI.md
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
  "score": <integer 0-100>,
  "summary": "<one sentence>",
  "verdict": "<Onayla | Reddet>",
  "details": ["<string>", "<string>"]
}

CRITICAL RULES:
- Output ONLY the JSON. No markdown. No code fences. No explanation.
- If input is unclear, set score to 0 and explain in summary.
- Respond in Turkish.
```

**aiService.js'e yapıştır:**
```bash
code backend-node/src/services/aiService.js
# SYSTEM_PROMPT değişkenini yukarıdaki ile değiştir
```

**Backend'i restart et** (npm run dev zaten watch mode'da olabilir, otomatik reload).

**Test (curl):**
```bash
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "Yasin, 5 yıl Java geliştirici, Spring Boot ve PostgreSQL biliyor"}'
```

**Beklenen:**
```json
{
  "success": true,
  "data": {
    "score": 75,
    "summary": "...",
    "verdict": "Onayla",
    "details": [...]
  }
}
```

**Sorun varsa:**
- JSON parse hatası → System prompt'a "No markdown. No code fences." ekle
- Türkçe yerine İngilizce dönüyor → "Respond in Turkish" ekle
- Saçma cevap → Role tanımını güçlendir, örnek ekle

**Commit:**
```bash
git add ai-specialist/ backend-node/src/services/aiService.js
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

2. **Az-shot örnek ekle (token israfı ama tutarlılık artar):**
   ```
   Example output:
   {"score": 85, "summary": "Güçlü teknik beceri, liderlik eksik", "verdict": "Onayla", "details": ["..."]}
   ```

3. **Edge case'leri açıkça yönet:**
   ```
   If the input is empty, irrelevant, or unclear:
   {"score": 0, "summary": "Geçerli bir girdi sağlanmadı", "verdict": "Reddet", "details": []}
   ```

**Her büyük iyileştirme commit edilir:**
```bash
git add ai-specialist/ backend-node/src/services/aiService.js
git commit -m "feat(ai): v2 prompt - JSON zorlama güçlendirildi"
git push
```

---

### 🟢 [2:30 – 3:00] Final Prompt + Entegrasyon Desteği

**Hedef:** Final prompt yerinde, uçtan uca akış çalışıyor.

```bash
# Final prompt'u PROMPT_GELISTIRME.md'de "AKTİF PROMPT" bölümüne yaz
code ai-specialist/PROMPT_GELISTIRME.md

# aiService.js'in bu versiyona uyumlu olduğunu doğrula
code backend-node/src/services/aiService.js
```

**Uçtan uca test (Frontend → Backend → AI):**
1. Frontend'i aç (http://localhost:5173)
2. Gerçek bir girdi yaz, gönder
3. Sonuç ekranda görünüyor mu?
4. Tüm alanlar (score, summary, verdict, details) doğru gösteriliyor mu?

Sorun varsa: Frontend'in `HomePage.jsx`'e bak, AI'dan gelen alanı doğru kullanıyor mu?

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
> 2. **Fallback mekanizması** — API yavaşlarsa veya hata verirse uygulamamız çökmüyor
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

Veya `aiService.js`'de markdown'ı temizle:
```javascript
let text = message.content[0].text.trim();
text = text.replace(/^```json\s*/i, '').replace(/\s*```$/, '');
return JSON.parse(text);
```

---

### Hata 2: AI alakasız alanlar ekliyor

**Belirti:**
```json
{
  "score": 85,
  "summary": "...",
  "explanation": "Ekstra alan",  ← bunu istemiyorsun
  "details": [...]
}
```

**Çözüm:** Frontend'i bozmaz çünkü sadece tanımlı alanları okur. Ama temizliği için:
```
Your response must contain EXACTLY these 4 keys: score, summary, verdict, details.
Do not add any other keys.
```

---

### Hata 3: Token limiti aşımı (uzun çıktı)

**Belirti:** Yanıt yarıda kesiliyor, JSON parse hatası.

**Çözüm:** `aiService.js`'de `max_tokens` değerini artır:
```javascript
max_tokens: 2048,  // 1024'ten 2048'e
```

Veya prompt'a ekle:
```
Keep "summary" under 200 characters.
Keep each "details" item under 100 characters.
Maximum 5 items in "details".
```

---

### Hata 4: API rate limit (429)

**Belirti:**
```
Error: 429 Too Many Requests
```

**Çözüm:**
- Anthropic dashboard'da limit'i kontrol et
- 1 dk bekle ve tekrar dene
- Yedek API key'e geç (FS yöneticisinden iste)
- `aiService.js`'de retry mekanizması:
  ```javascript
  async function callWithRetry(fn, retries = 2) {
    try { return await fn(); }
    catch (err) {
      if (retries > 0 && err.status === 429) {
        await new Promise(r => setTimeout(r, 2000));
        return callWithRetry(fn, retries - 1);
      }
      throw err;
    }
  }
  ```

---

### Hata 5: AI Türkçe değil İngilizce yanıt veriyor

**Çözüm:** System prompt'un en başına ve sonuna:
```
You MUST respond entirely in Turkish (Türkçe).
All field values (summary, verdict, details) must be in Turkish.
```

---

### Hata 6: aiService.js'de "ANTHROPIC_API_KEY undefined"

**Çözüm:**
1. `backend-node/.env` dosyası var mı?
2. İçinde `ANTHROPIC_API_KEY=sk-ant-...` doğru yazılmış mı? (tırnak yok, boşluk yok)
3. `cp .env.example .env` yapıldı mı?
4. Backend'i restart et (Ctrl+C, sonra `npm run dev`)

---

## 10. SENIN: Demo Hazırlığı

### Sunumda göstereceğin 3 şey:

1. **AI'nın "akıllı" davranışı**
   - Birkaç farklı girdi ile arayüzde dene
   - Skor değişiyor, summary anlamlı

2. **Edge case kontrolü**
   - Boş veya alakasız girdi ver → uygulama çökmüyor, anlamlı yanıt veriyor
   - "Bakın, fallback mekanizmamız çalışıyor"

3. **Prompt mühendisliği**
   - `aiService.js`'i aç, system prompt'u kısaca göster
   - "Buradaki rol tanımı ve sıkı JSON kuralları sayesinde tutarlı çıktı alıyoruz"

### Soru-cevap için hazırlık:
- **"Hangi model?"** → Anthropic Claude Sonnet 4.6
- **"Neden bu model?"** → [Domain]'e uygun, hızlı, JSON çıktısı tutarlı
- **"Maliyet?"** → Token başına çok düşük, hackathon için yeterli
- **"Fine-tune yaptınız mı?"** → Hayır, sıfırdan eğitmek 4 saatte mümkün değil. Bunun yerine prompt mühendisliği ile model davranışını yönettik.
- **"RAG kullandınız mı?"** → [Evet/Hayır, kullandıysan in-memory yaklaşım]

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
git add ai-specialist/ backend-node/src/services/aiService.js
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
| AI sözleşmesi hazır | Backend, Frontend | "Sözleşme: score, summary, verdict, details — `AI_CIKTI_SOZLESMESI.md`" |
| Sözleşme değişti | Backend, Frontend, FS | "Sözleşmeye `confidence` alanı ekledim — kontrol edin" |
| v1 prompt çalıştı | Tüm ekip | "v1 hazır, curl ile test ettim, /api/analyze çalışıyor" |
| Final prompt | FS | "AI branch merge için hazır" |
| Tıkandım | Tüm ekip | "Token limiti aşımı var, prompt'u kısaltıyorum, 10 dk sürer" |

### Sana Bildirilenler
- Backend: "POST /api/analyze çalışıyor mu test edebilir misin"
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
- `aiService.js`'i OpenAI SDK'ya çevir (10 dk):
  ```bash
  cd backend-node
  npm install openai
  ```
  ```javascript
  const OpenAI = require('openai');
  const client = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });
  // chat.completions.create({...})
  ```
- Son çare: Mock yanıt döndür (`aiService.js`'de hard-coded data), demo'da "AI offline" notu ile aç

### Senaryo 2: WiFi koptu
**Çözüm:**
- Telefon hotspot
- Backend ve frontend offline çalışıyor (sadece AI çağrısı dış servis)
- Mevcut açık AI yanıtları cache'lenmiş olabilir

### Senaryo 3: Laptop çöktü
**Çözüm:**
- En son push'unu hatırla → başka laptop'tan clone'la → devam et
- Bu yüzden 30 dk'da bir push yapıyorsun

### Senaryo 4: Prompt asla tutarlı çalışmıyor (saatler geçti)
**Çözüm:**
- MVP'yi daralt — daha basit bir çıktı şemasına dön
- "summary" + "score" yeter, "details" gerekmez
- Çalışmayan kısımları kaldır, çalışanı parlat

### Senaryo 5: Backend hiç çalışmıyor (Backend takıldı)
**Çözüm:**
- Backend'ciye yardıma git, kendi branch'ini bekleyebilir
- Veya mock backend kur: aiService.js'i frontend'den direkt çağıracak şekilde refaktör et (son çare)

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce şunlar tamam olmalı:

- [ ] `feat/ai` branch'in `main`'e merge edilmiş
- [ ] `main` üzerinde son `aiService.js` çalışıyor
- [ ] `ai-specialist/AI_CIKTI_SOZLESMESI.md` final hâliyle commit edilmiş
- [ ] `ai-specialist/PROMPT_GELISTIRME.md` final prompt yazılmış
- [ ] Test girdileri ile uçtan uca akış başarılı
- [ ] `.env` git'e gitmemiş (`.gitignore` zaten engelliyor ama kontrol et)
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

### Anthropic SDK çağrısı (referans)
```javascript
const Anthropic = require('@anthropic-ai/sdk');
const client = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });

const message = await client.messages.create({
  model: 'claude-sonnet-4-6',
  max_tokens: 1024,
  system: SYSTEM_PROMPT,
  messages: [{ role: 'user', content: userInput }],
});
const text = message.content[0].text;
```

### Test komutları
```bash
# Hızlı test
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "test"}'

# Context ile (RAG)
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "test", "context": "ek belge metni"}'
```

### `/ship` komutu (Claude Code kullanıyorsan)
```
/ship aiService.js'e analyzeWithImage fonksiyonu ekle, görsel kabul eden Claude vision çağrısı yapacak
```
4 agent (planner→coder→tester→reviewer) otomatik çalışır.

---

**Başarılar! AI tarafını sen kontrol ediyorsun. 🧠**
