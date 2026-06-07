# Hackathon Kullanım Rehberi

Bu rehber, hazırlanan boilerplate yapısını yarışma gününde nasıl kullanacağını adım adım anlatır.

---

## Yarışmadan Önce (Bu Gece)

### 1. API Anahtarını Hazırla

[console.anthropic.com](https://console.anthropic.com) adresine gir, API key oluştur ve not al.
Bakiyeni kontrol et — yarışmada kredi bitmemeli.

### 2. .env Dosyasını Oluştur

```bash
# backend-node klasöründe:
cp .env.example .env
```

`.env` dosyasını aç ve `ANTHROPIC_API_KEY=sk-ant-...` kısmını kendi anahtarınla doldur.

### 3. Bağımlılıkları Yükle (Şimdi Yükle, Yarışmada Zaman Kaybetme)

```bash
# Terminal 1 — Node.js backend
cd backend-node
npm install

# Terminal 2 — Frontend
cd frontend
npm install
```

Java kullanacaksan:
```bash
cd backend-java
mvn dependency:resolve   # bağımlılıkları şimdiden indir
```

### 4. Çalıştığını Test Et

```bash
# Terminal 1
cd backend-node && npm run dev
# → "Backend running on http://localhost:3001" görmelisin

# Terminal 2
cd frontend && npm run dev
# → "http://localhost:5173" görmelisin
```

Tarayıcıda `http://localhost:5173` aç. Arayüz geliyorsa her şey hazır.

---

## Yarışma Günü — İlk 30 Dakika

### Adım 1: Konuyu Al, Ürünü Tanımla (5 dk)

Konu açıklandığında ekiple hızlıca karar verin:
- Ürün ne yapacak? (1 cümle)
- Kullanıcı ne girecek, ne görecek?
- AI neyi analiz edecek / üretecek?

**Kapsam daralt.** "Şunu da ekleriz" dediğin her şeyi elersin.

---

### Adım 2: architecture.md Dosyasını Doldur (10 dk)

`AI_CONTEXT/2_architecture.md` dosyasını aç. `⚠️ FILL IN` yazan kısımları doldur:

```markdown
## Product Summary
Kullanıcı CV yükler, sistem adayın pozisyona uygunluğunu puanlar.

## Core User Flow
Kullanıcı CV metnini girer → Analyze butonuna basar → AI skoru ve açıklamayı döner

## Active Backend
Node.js

## Database Schema
### Table: analyses
- id: INTEGER PRIMARY KEY AUTOINCREMENT
- cv_text: TEXT NOT NULL
- position: TEXT NOT NULL
- score: INTEGER
- result_json: TEXT
- created_at: DATETIME DEFAULT CURRENT_TIMESTAMP
```

**Bu dosya çok önemli.** Ekipten herkes AI'ya kod yazdırırken bu dosyaya referans verecek.
Aynı tablo isimlerini, aynı alan adlarını herkes kullanacak — çakışma olmayacak.

---

### Adım 3: AI Prompt'unu Yaz (10 dk)

`AI_CONTEXT/3_ai_prompts.md` dosyasını aç. "General Analyzer" şablonunu ürününe göre düzenle.

Örnek (CV analiz için):
```
You are an expert HR specialist. Analyze the given CV text for the specified position.

Respond ONLY with valid JSON:
{
  "score": number 0-100,
  "verdict": "Mülakata Çağır" or "Reddet",
  "strengths": ["string", "string"],
  "gaps": ["string"],
  "summary": "string"
}

No markdown, no extra text. Only the JSON object.
```

Bu prompt'u `backend-node/src/services/aiService.js` dosyasında `SYSTEM_PROMPT` değişkenine yapıştır.

---

### Adım 4: Rol Dağılımı (5 dk)

| Kişi | Görev |
|------|-------|
| **AI Uzmanı** | `aiService.js` içindeki system prompt'u mükemmelleştir, test et, geliştir |
| **Backend** | Yeni endpoint'ler ekle (`src/routes/api.js`), gerekirse DB bağlantısı kur |
| **Frontend** | `HomePage.jsx`'i ürüne göre düzenle, yeni sayfalar/component'ler ekle |
| **Full-Stack Destek** | Git yönetimi, birleştirme, deploy, eksik yerlere destek |

---

## Kodlama Aşaması — 3 Farklı Yöntem

### Yöntem A: /ship Komutu (En Güçlü — Claude Code Kullanıyorsan)

Bu komut **Planner → Coder → Tester → Reviewer** zincirini otomatik çalıştırır.

Claude Code terminaline yaz:
```
/ship CV metni alan POST endpoint'i ekle, better-sqlite3 ile analyses tablosuna kaydet
```

Ne olur:
1. **Planner** (Opus): Kodu yazmadan önce spec.md oluşturur — hangi dosya, hangi fonksiyon
2. **Coder** (Sonnet): Spec'i okur, kodu yazar
3. **Tester** (Sonnet): Test yazar ve çalıştırır
4. **Reviewer** (Opus): SHIP / NEEDS WORK / BLOCK kararı verir

Pipeline OPEN QUESTION veya test hatası görürse sana sorar, devam etmez.
Her feature için 5-10 dakika alır ama kaliteli kod çıkar.

---

### Yöntem B: Cursor / VS Code Copilot ile Çalışma

Chat panelinde `@` ile dosyaları referans ver:

```
@AI_CONTEXT/1_system_rules.md ve @AI_CONTEXT/2_architecture.md dosyalarına uygun olarak
backend-node/src/routes/api.js içine POST /api/cv-analyze endpoint'i ekle.
Request body: { cvText: string, position: string }
Response: analyses tablosuna kaydet ve AI sonucunu döndür.
```

Planlama modu için önce şunu yaz:
```
Henüz kod yazma. @AI_CONTEXT/2_architecture.md şemasına bakarak bu feature'ı
hangi adımlarda yapacağını listele.
```

Planı onayladıktan sonra:
```
Harika. Şimdi sadece 1. adımı yap.
```

---

### Yöntem C: Herhangi Bir AI Chat (ChatGPT, Claude Web, vb.)

1. `SKILLS.md` dosyasını aç, tüm içeriği kopyala
2. Chat'e yeni konuşma aç, içeriği yapıştır
3. "Hazırım de" yaz, AI onaylasın
4. Ardından `AI_CONTEXT/2_architecture.md` içeriğini de yapıştır
5. Artık kod isteyebilirsin — AI projenin tüm bağlamını biliyor

```
Şimdi backend-node/src/routes/api.js dosyasına POST /api/cv-analyze endpoint'i ekle.
architecture.md'deki analyses tablosuna kaydet.
```

---

## Kodlama Sırasında Dikkat Edilecekler

### Herkes Git'e Düzenli Push Yapsın

```bash
git add .
git commit -m "feat: cv analyze endpoint"
git push
```

Merge çakışması yaşamamak için:
- Backend kişisi sadece `backend-node/` dokunur
- Frontend kişisi sadece `frontend/` dokunur
- AI uzmanı sadece `aiService.js`, `3_ai_prompts.md` ve `ai-specialist/` klasörüne dokunur

### AI Prompt'unu Iteratif Geliştir

Test ederken AI saçma cevap veriyorsa:
1. `3_ai_prompts.md` dosyasını aç, prompt'u düzelt
2. `aiService.js`'deki `SYSTEM_PROMPT`'u güncelle
3. Endpoint'i tekrar test et

Bunu 3-4 kez döngü yaparak prompt'u mükemmelleştir.

### Hata Yönetimi Zaten Hazır

Backend'de her endpoint try/catch ile sarılı, `aiService.js`'de fallback yanıt var.
AI API yavaş kalırsa veya hata dönerse uygulama çökmez.

---

## Son 45 Dakika — Entegrasyon ve Test

### Backend ↔ Frontend Bağlantısını Doğrula

```bash
# Backend çalışıyor mu?
curl http://localhost:3001/health

# AI endpoint çalışıyor mu?
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "test girdisi"}'
```

Frontend'de tarayıcı konsolunu aç (F12), Network sekmesine bak.
API çağrısı yapılıyor mu, yanıt geliyor mu?

### Uçtan Uca Senaryo Testi

Jürinin yapacağı tam senaryoyu simüle et:
1. Tarayıcıyı aç
2. Gerçek bir girdi yaz
3. Submit et
4. Sonuç ekranda görünüyor mu?
5. Hata durumu: boş girdi, çok uzun girdi, geçersiz format

---

## Son 15 Dakika — Git Push ve README

### README'yi Güncelle

`README.md` dosyasının en üstüne ekle:

```markdown
## Proje: [ÜRÜN ADI]

**Ne Yapar:** [1 cümle açıklama]

**Kullanılan AI:** Anthropic Claude (claude-sonnet-4-6)

**Çalıştırmak için:**
1. `cd backend-node && npm install && npm run dev`
2. `cd frontend && npm install && npm run dev`
3. Tarayıcıda http://localhost:5173 aç
4. .env dosyasında ANTHROPIC_API_KEY gereklidir
```

### Final Push

```bash
git add .
git commit -m "final: hackathon submission"
git push origin main
```

Repo URL'sini kopyala, organizasyona teslim et.

---

## Dosya Referans Tablosu

| Dosya | Sahibi | Açıklama | Ne Zaman Kullanılır |
|-------|--------|----------|---------------------|
| `SKILLS.md` | Herkes | AI için evrensel bağlam dosyası | Her AI chat başlangıcında yapıştır |
| `AI_CONTEXT/1_system_rules.md` | Herkes | Kodlama standartları | Cursor/Copilot'a @ile referans ver |
| `AI_CONTEXT/2_architecture.md` | Herkes | DB şeması, endpoint'ler | İlk 15 dakikada doldur, sürekli güncelle |
| `AI_CONTEXT/3_ai_prompts.md` | AI Uzmanı | Claude system prompt şablonları | AI özelliği geliştirirken düzenle |
| `ai-specialist/PROMPT_GELISTIRME.md` | AI Uzmanı | Prompt iterasyon defteri | Prompt yazarken, her versiyonu buraya not al |
| `ai-specialist/AI_CIKTI_SOZLESMESI.md` | AI Uzmanı | Backend↔Frontend veri anlaşması | İlk 20 dk içinde doldur, ekiple paylaş |
| `ai-specialist/RAG_KURULUM.md` | AI Uzmanı | Belge bağlama (PDF/TXT) rehberi | Yarışmada belge verilirse aç ve uygula |
| `backend-node/src/services/aiService.js` | AI Uzmanı | Anthropic API entegrasyonu | System prompt buraya kopyalanır |
| `backend-node/src/routes/api.js` | Backend | HTTP endpoint'leri | Yeni route'lar buraya eklenir |
| `frontend/src/api/client.js` | Frontend | Backend API çağrıları | Yeni fetch fonksiyonları buraya |
| `frontend/src/pages/HomePage.jsx` | Frontend | Ana sayfa | Ürüne göre tamamen değiştirilebilir |
| `.claude/commands/ship.md` | Herkes | /ship pipeline komutu | Claude Code'da /ship ile tetiklenir |

---

## Sık Karşılaşılan Sorunlar

**"ANTHROPIC_API_KEY is not set" hatası**
→ `backend-node/.env` dosyasını oluşturdun mu? `cp .env.example .env` çalıştır.

**Frontend API'ye ulaşamıyor (CORS hatası)**
→ Backend'in çalıştığından emin ol (port 3001). `vite.config.js`'deki proxy 3001'e yönlendiriyor.

**AI JSON dışında bir şey dönüyor**
→ `aiService.js`'deki system prompt'una "Respond ONLY with valid JSON, no markdown" ekle.

**Java backend başlamıyor**
→ Java 21 yüklü mü? `java -version` kontrol et. `ANTHROPIC_API_KEY` env variable olarak set edildi mi?

**Git push reddedildi**
→ `git pull --rebase origin main` çalıştır, çakışmaları çöz, tekrar push et.
