# ⚙️ Backend Geliştirici — Detaylı Rehber

> Bu rehber **Backend Geliştirici** içindir.
> Diğer roller: [AI Uzmanı](01_AI_UZMANI.md) · [Frontend](03_FRONTEND.md) · [Git Yöneticisi](04_GIT_YONETICISI.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **motor odası**sın. Görevin:
- Express API endpoint'lerini yazmak
- Veritabanı (SQLite) tablolarını ve CRUD'u kurmak
- AI Uzmanı'nın `aiService.js`'ini çağırıp veriyi DB'ye yazmak
- API'nin **çökmediğinden** ve **doğru JSON döndürdüğünden** emin olmak
- Dosya upload, query parametreleri, validation gibi standart işleri halletmek

Kısaca: Frontend "şu endpoint'i çağıracağım" diyecek, sen onun beklediği yanıtın geldiğinden emin olacaksın. AI Uzmanı'nın çıktısını DB'ye yazıp frontend'e iletmek senin işin.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + (Node.js veya Java) backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Node.js 20 + Express (önerilen) veya Java 21 + Spring Boot
- **Frontend:** React 18 + Vite + Tailwind CSS
- **AI:** Anthropic Claude — `claude-sonnet-4-6` (AI Uzmanı yönetir)
- **DB:** SQLite (Node) — `better-sqlite3` paketi, sıfır config
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
- **0:20:** Architecture.md'ye DB şeması ve endpoint listesi yazılmış olmalı
- **0:45:** DB tablosu çalışıyor, ilk basit endpoint hazır
- **1:30:** Ana endpoint (POST /api/analyze) tam çalışıyor, frontend bağlanabilir
- **2:30:** Tüm endpoint'ler hazır, code freeze
- **3:00:** Yeni endpoint yok, sadece bug fix

---

## 3. ORTAK: Tüm Roller

| Rol | Sorumluluk | Dokunduğu |
|-----|-----------|-----------|
| **AI Uzmanı** | Prompt, aiService, çıktı sözleşmesi | `ai-specialist/`, `aiService.js` |
| **Backend (sen)** | Endpoint'ler, DB | `backend-node/src/routes/`, `services/` (AI dışı) |
| **Frontend** | UI | `frontend/src/` |
| **FS Yöneticisi** | Git, merge, koordinasyon | Her yer (dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar

- [ ] Boilerplate'i clone'la, `cp backend-node/.env.example backend-node/.env` yap
- [ ] `.env` içine `ANTHROPIC_API_KEY=sk-ant-...` yaz (FS Yöneticisi paylaşacak)
- [ ] `cd backend-node && npm install` — bağımlılıkları indir (~2 dk)
- [ ] `npm run dev` — başlatabildiğini doğrula, port 3001'de çalışıyor olmalı
- [ ] `curl http://localhost:3001/health` — `{"status":"ok"}` gelmeli
- [ ] `curl -X POST localhost:3001/api/analyze -d '{"input":"test"}' -H "Content-Type: application/json"` — AI gerçekten yanıt veriyor mu
- [ ] SQLite hakkında biraz bilgi tazele: `better-sqlite3` sync API kullanır, kolay
- [ ] **Java kullanılacaksa:** `cd backend-java && mvn dependency:resolve` — dependency'leri indir

**Sorun yaşarsan:** Yarışma günü değil, bu gece çöz.

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika

```bash
# Konu açıklandıktan sonra:
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout feat/backend

# .env'yi oluştur (eğer dün yapmadıysan)
cp backend-node/.env.example backend-node/.env
# ANTHROPIC_API_KEY'i yaz

# Backend'i ayağa kaldır
cd backend-node
npm install   # dün yaptıysan gerek yok
npm run dev   # → port 3001'de çalışıyor olmalı
```

Backend ayakta. Şimdi AI Uzmanı'nın sözleşmesini bekle (10 dk içinde gelecek).

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **Mimari Karar:** `AI_CONTEXT/2_architecture.md`'ye DB tabloları ve endpoint listesi yaz
2. **Veritabanı Kur:** `dbService.js` ile tablo oluştur, CRUD fonksiyonları yaz
3. **Ana Endpoint:** `POST /api/analyze` → AI servisini çağır, DB'ye kaydet, JSON döndür
4. **Yardımcı Endpoint'ler:** Geçmiş kayıtlar, tek kayıt, dosya upload (gerekirse)
5. **Hata Yönetimi:** Tüm endpoint'lerde try/catch, anlamlı hata mesajları
6. **Validation:** Boş input, eksik alan, çok büyük veri kontrolleri

### Yan Görevler
- AI Uzmanı'nın `aiService.js`'ini doğru çağırdığından emin ol
- Frontend'in beklediği response formatını sözleşmeye uygun tut
- Postman/curl ile her endpoint'i kendin test et

---

## 7. SENIN: Dokunduğun Dosyalar

```
backend-node/src/routes/api.js              ← Yeni endpoint'ler buraya
backend-node/src/services/dbService.js      ← DB CRUD (sen oluşturacaksın)
backend-node/src/services/                  ← Yeni servis dosyaları
backend-node/src/index.js                   ← Middleware eklemen gerekirse
AI_CONTEXT/2_architecture.md                ← DB şeması + endpoint listesi (DOLDUR)
```

**Dokunmayacağın yerler:**
- `backend-node/src/services/aiService.js` (AI Uzmanı'nın — sen sadece import edip çağırırsın)
- `frontend/` (Frontend'in)
- `ai-specialist/` (AI Uzmanı'nın)

---

## 8. SENIN: Adım Adım Timeline

### 🟢 [0:00 – 0:20] Mimari Kararlar

**Hedef:** DB şeması netleşti, endpoint listesi hazır.

```bash
git checkout feat/backend
git pull origin feat/backend
code AI_CONTEXT/2_architecture.md
```

**Doldur (AI Uzmanı'nın sözleşmesi geldikten sonra):**

```markdown
## Active Backend
Node.js

## Database Schema

### Table: analyses
- id: INTEGER PRIMARY KEY AUTOINCREMENT
- input: TEXT NOT NULL
- context: TEXT
- result_json: TEXT
- score: INTEGER
- created_at: DATETIME DEFAULT CURRENT_TIMESTAMP

## API Endpoints

| Method | Path | Body | Response |
|--------|------|------|----------|
| GET | /health | — | `{status:"ok"}` |
| POST | /api/analyze | `{input, context}` | `{success, data: <AI sonucu>}` |
| GET | /api/results | — | `{success, data: [Analysis...]}` |
| GET | /api/results/:id | — | `{success, data: Analysis}` |
| POST | /api/upload | multipart file | `{success, data: {extracted_text}}` |
```

**Bildirim:** Frontend ve FS'ye yaz:
> "Backend mimarisi hazır. Endpoint'ler `architecture.md`'de. /api/analyze 10 dk içinde çalışır halde olacak."

**Commit:**
```bash
git add AI_CONTEXT/2_architecture.md
git commit -m "feat(be): architecture - endpoint ve DB şeması"
git push
```

---

### 🟢 [0:20 – 0:45] Veritabanı Kurulumu

**Hedef:** SQLite tablosu oluştu, CRUD fonksiyonları hazır.

```bash
code backend-node/src/services/dbService.js
```

**dbService.js (komple kopyala):**
```javascript
const Database = require('better-sqlite3');
const path = require('path');

const db = new Database(path.join(__dirname, '..', '..', 'hackathon.db'));

// Tabloyu oluştur (zaten varsa atla)
db.exec(`
  CREATE TABLE IF NOT EXISTS analyses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    input TEXT NOT NULL,
    context TEXT,
    result_json TEXT,
    score INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
  )
`);

// Prepared statements (hızlı, güvenli)
const stmts = {
  insert: db.prepare(`
    INSERT INTO analyses (input, context, result_json, score)
    VALUES (?, ?, ?, ?)
  `),
  getAll: db.prepare(`SELECT * FROM analyses ORDER BY created_at DESC LIMIT 100`),
  getById: db.prepare(`SELECT * FROM analyses WHERE id = ?`),
};

// Wrapper fonksiyonlar
function saveAnalysis({ input, context, result }) {
  const score = result?.score ?? 0;
  const info = stmts.insert.run(input, context || null, JSON.stringify(result), score);
  return info.lastInsertRowid;
}

function getAllAnalyses() {
  return stmts.getAll.all().map((row) => ({
    ...row,
    result: JSON.parse(row.result_json || '{}'),
  }));
}

function getAnalysisById(id) {
  const row = stmts.getById.get(id);
  if (!row) return null;
  return { ...row, result: JSON.parse(row.result_json || '{}') };
}

module.exports = { saveAnalysis, getAllAnalyses, getAnalysisById };
```

**Test (Node REPL):**
```bash
cd backend-node
node -e "const db = require('./src/services/dbService'); console.log(db.saveAnalysis({input:'test', result:{score:50}})); console.log(db.getAllAnalyses());"
# → ID dönmeli, sonra kayıt dönmeli
```

**Commit:**
```bash
git add backend-node/src/services/dbService.js
git commit -m "feat(be): SQLite db setup - analyses tablosu"
git push
```

---

### 🟢 [0:45 – 1:30] Ana Endpoint: POST /api/analyze

**Hedef:** Frontend'in çağırabileceği, AI'yi tetikleyen, DB'ye yazan endpoint.

`backend-node/src/routes/api.js`'i aç. Mevcut `/analyze` endpoint'ini şununla değiştir:

```javascript
const express = require('express');
const { analyze, analyzeWithContext } = require('../services/aiService');
const { saveAnalysis, getAllAnalyses, getAnalysisById } = require('../services/dbService');

const router = express.Router();

// Validation helper
function validateInput(input) {
  if (!input || typeof input !== 'string') {
    return 'input (string) gereklidir';
  }
  if (input.trim().length === 0) {
    return 'input boş olamaz';
  }
  if (input.length > 10000) {
    return 'input en fazla 10000 karakter olabilir';
  }
  return null;
}

// POST /api/analyze
router.post('/analyze', async (req, res, next) => {
  try {
    const { input, context } = req.body;

    const validationError = validateInput(input);
    if (validationError) {
      return res.status(400).json({ success: false, error: validationError });
    }

    // AI'yi çağır
    const result = context
      ? await analyzeWithContext(input, context)
      : await analyze(input);

    // DB'ye kaydet
    const id = saveAnalysis({ input, context, result });

    res.json({ success: true, data: { id, ...result } });
  } catch (err) {
    next(err);
  }
});

// GET /api/results
router.get('/results', (req, res, next) => {
  try {
    const analyses = getAllAnalyses();
    res.json({ success: true, data: analyses });
  } catch (err) {
    next(err);
  }
});

// GET /api/results/:id
router.get('/results/:id', (req, res, next) => {
  try {
    const analysis = getAnalysisById(Number(req.params.id));
    if (!analysis) {
      return res.status(404).json({ success: false, error: 'Kayıt bulunamadı' });
    }
    res.json({ success: true, data: analysis });
  } catch (err) {
    next(err);
  }
});

router.get('/health', (req, res) => {
  res.json({ success: true, data: { status: 'ok' } });
});

module.exports = router;
```

**Test her endpoint için:**

```bash
# /api/analyze
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "Yasin, 5 yıl Java geliştirici"}'
# → { success: true, data: { id: 1, score: ..., ... } }

# /api/results
curl http://localhost:3001/api/results
# → { success: true, data: [{ id: 1, ... }] }

# /api/results/:id
curl http://localhost:3001/api/results/1
# → { success: true, data: { id: 1, ... } }

# Validation testi
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{}'
# → 400, { success: false, error: "input (string) gereklidir" }
```

**Bildirim:** Frontend'e söyle:
> "POST /api/analyze hazır. Response: `{success, data: {id, score, summary, verdict, details}}`. Bağlayabilirsin."

**Commit:**
```bash
git add backend-node/src/routes/api.js
git commit -m "feat(be): POST /api/analyze endpoint çalışıyor"
git push
```

---

### 🟢 [1:30 – 2:30] Ek Endpoint'ler ve Dosya Upload

**Hedef:** Frontend'in ihtiyaç duyabileceği tüm endpoint'ler hazır.

#### Dosya Upload (gerekirse)

PDF, TXT, görsel yükleme varsa:

```javascript
const multer = require('multer');
const fs = require('fs');
const path = require('path');

const UPLOAD_DIR = path.join(__dirname, '..', '..', 'uploads');
if (!fs.existsSync(UPLOAD_DIR)) fs.mkdirSync(UPLOAD_DIR);

const upload = multer({
  dest: UPLOAD_DIR,
  limits: { fileSize: 10 * 1024 * 1024 }, // 10MB
  fileFilter: (req, file, cb) => {
    const allowed = ['.pdf', '.txt', '.md'];
    const ext = path.extname(file.originalname).toLowerCase();
    if (allowed.includes(ext)) cb(null, true);
    else cb(new Error('Sadece PDF, TXT, MD dosyaları kabul edilir'));
  },
});

router.post('/upload', upload.single('file'), async (req, res, next) => {
  try {
    if (!req.file) {
      return res.status(400).json({ success: false, error: 'Dosya yüklenmedi' });
    }
    const content = fs.readFileSync(req.file.path, 'utf-8');
    res.json({
      success: true,
      data: {
        filename: req.file.originalname,
        size: req.file.size,
        text: content.slice(0, 5000), // İlk 5000 karakter
      },
    });
  } catch (err) {
    next(err);
  }
});
```

PDF için ek dependency:
```bash
npm install pdf-parse
```

#### İstatistikler Endpoint'i (opsiyonel ama jüriye iyi görünür)

```javascript
const stats = db.prepare(`
  SELECT
    COUNT(*) as total,
    AVG(score) as avg_score,
    MAX(score) as max_score,
    MIN(created_at) as first_analysis
  FROM analyses
`);

router.get('/stats', (req, res) => {
  const data = stats.get();
  res.json({ success: true, data });
});
```

#### Sıralama / Filtreleme (gerekirse)

```javascript
router.get('/results', (req, res, next) => {
  try {
    const { sort = 'created_at', order = 'desc', minScore = 0 } = req.query;
    const sql = `
      SELECT * FROM analyses
      WHERE score >= ?
      ORDER BY ${sort} ${order === 'asc' ? 'ASC' : 'DESC'}
      LIMIT 100
    `;
    const analyses = db.prepare(sql).all(Number(minScore));
    res.json({ success: true, data: analyses });
  } catch (err) {
    next(err);
  }
});
```

**Her büyük değişiklikten sonra commit:**
```bash
git add backend-node/
git commit -m "feat(be): dosya upload endpoint"
git push
```

---

### 🟢 [2:30 – 3:00] Test, Temizlik, Hata Yönetimi

**Hedef:** Her endpoint hatalı girdilerde de çökmüyor.

#### Test Checklist

```bash
# 1. Normal akış
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "normal metin"}'

# 2. Boş body
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{}'
# → 400 hatası

# 3. Yanlış tip (number)
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": 123}'
# → 400 hatası

# 4. Çok uzun metin
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "'"$(printf 'a%.0s' {1..20000})"'"}'
# → 400 hatası (10000 karakter limiti)

# 5. Olmayan endpoint
curl localhost:3001/api/foo
# → 404

# 6. Geçersiz JSON body
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d 'not json'
# → 400 (Express otomatik halleder)
```

#### Final Commit

```bash
git add backend-node/
git commit -m "feat(be): tüm endpointler hazır + validation tamamlandı"
git push
```

**FS yöneticisine:** "Backend branch merge için hazır."

---

### 🟢 [3:00 – 4:00] Entegrasyon + Sunum Desteği

**Hedef:** main üzerinde her şey çalışıyor.

```bash
# FS merge yaptıktan sonra
git checkout main
git pull origin main

# Backend'i restart et
cd backend-node && npm run dev
```

#### Uçtan uca test:
1. Frontend'i aç (Frontend'ci başlatmış olmalı)
2. Form gönder → backend log'da çağrı görmeli misin
3. DB dosyası (`hackathon.db`) oluşmuş mu kontrol et:
   ```bash
   ls -la backend-node/hackathon.db
   ```

#### Sunum Notları (1 dakika)

> "Backend tarafında Express.js kullandık.
> Anthropic API'sini direkt çağırmak yerine `aiService.js` adında bir soyutlama katmanı oluşturduk — böylece istediğimiz zaman OpenAI veya Gemini'ye geçebiliriz.
> Her endpoint'te try/catch ve validation var. Veriyi SQLite'a kalıcı olarak yazıyoruz, kullanıcı geçmiş analizleri görebilir.
> Hata durumunda fallback yanıt dönüyor — kullanıcı boş bir ekran görmüyor."

---

## 9. SENIN: Sık Karşılaştığın Hatalar

### Hata 1: `Cannot find module 'better-sqlite3'`

**Çözüm:**
```bash
cd backend-node
npm install
# Hâlâ olmuyorsa:
npm install better-sqlite3 --build-from-source
```
Windows'ta build araçları yoksa:
```bash
npm install --global windows-build-tools
```

---

### Hata 2: SQLite "database is locked"

**Nedenler:**
- Aynı anda başka bir Node process açık (önceki `npm run dev` tam kapanmadı)
- DB dosyası başka bir uygulamada açık (SQLite browser vb.)

**Çözüm:**
```bash
# Tüm node processlerini kapat
# Windows:
taskkill /F /IM node.exe
# Linux/Mac:
killall node

# Sonra
npm run dev
```

---

### Hata 3: CORS hatası (Frontend bağlanamıyor)

**Belirti:** Frontend console'da:
```
Access to XMLHttpRequest at 'http://localhost:3001/api/...' from origin 'http://localhost:5173' blocked by CORS policy
```

**Çözüm:** `backend-node/src/index.js`'i kontrol et:
```javascript
app.use(cors({ origin: 'http://localhost:5173' }));
```

Yoksa şu olabilir:
- Frontend farklı port'ta çalışıyor → cors origin'i güncelle
- Vite proxy bypass ediliyor → frontend'in `vite.config.js`'i kontrol et

Geçici hızlı çözüm (test için):
```javascript
app.use(cors()); // tüm origin'lere açar
```

---

### Hata 4: Body undefined

**Belirti:**
```javascript
const { input } = req.body; // input undefined
```

**Çözüm:** `index.js`'te şunlar var mı kontrol et:
```javascript
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
```

Postman/curl'de:
- `Content-Type: application/json` header'ı var mı?
- Body geçerli JSON mu?

---

### Hata 5: AI çağrısı yavaş (>10 saniye)

**Çözüm:**
- `aiService.js`'de `max_tokens` değerini düşür (AI Uzmanı'na söyle)
- Timeout ekle:
  ```javascript
  const message = await Promise.race([
    client.messages.create({...}),
    new Promise((_, rej) => setTimeout(() => rej(new Error('AI timeout')), 15000))
  ]);
  ```

---

### Hata 6: Port 3001 zaten kullanılıyor

**Çözüm:**
```bash
# Hangi process kullanıyor bul:
# Windows:
netstat -ano | findstr :3001
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :3001
kill -9 <PID>

# Veya .env'de farklı port:
PORT=3002
```

Bu durumda Frontend'in `vite.config.js`'ini de güncelle:
```javascript
proxy: { '/api': { target: 'http://localhost:3002' } }
```

---

### Hata 7: Java seçildi ama backend başlamıyor

**Çözüm:**
```bash
java -version  # 21 olmalı
mvn -version

cd backend-java
mvn clean install
mvn spring-boot:run
```

`application.yml`'de API key var mı?
```yaml
anthropic:
  api-key: ${ANTHROPIC_API_KEY:dev-key-placeholder}
```

Env variable set et:
```bash
# Windows
set ANTHROPIC_API_KEY=sk-ant-...

# Linux/Mac
export ANTHROPIC_API_KEY=sk-ant-...
```

---

## 10. SENIN: Demo Hazırlığı

### Sunumda göstereceğin 2 şey:

1. **Mimari Diyagram (sözlü)**
   - "Express.js → aiService (Claude API) → SQLite DB"
   - "Her endpoint'te try/catch, validation, fallback"

2. **Canlı Endpoint Testi (opsiyonel, etkileyici)**
   - Postman veya curl ile bir endpoint çağır
   - "Bakın, history endpoint'imiz geçmiş kayıtları getiriyor"

### Soru-cevap için hazırlık:
- **"Neden SQLite?"** → 4 saatte sıfır config, dosya bazlı, prod'a geçmek istersek PostgreSQL'e migration kolay
- **"Auth yok mu?"** → MVP scope dışında, JWT eklemek 30 dakika sürer, gerekirse ekleriz
- **"Ölçeklendirme?"** → Stateless, horizontal scale edilebilir. DB için PostgreSQL ve Redis cache eklenebilir.
- **"Neden Express, Spring Boot değil?"** → AI hackathon hızı için. Tek dilde (JS) full-stack, daha az boilerplate

---

## 11. ORTAK: Git İş Akışı

### Branch Stratejisi
```
main                           ← FS yöneticisi merge eder
├── feat/ai
├── feat/backend               ← SENİN BRANCH'İN
└── feat/frontend
```

### Senin Günlük Akış
```bash
# Sabah
git checkout feat/backend
git pull origin feat/backend

# Çalışırken her ~30 dk (veya her endpoint sonrası)
git add backend-node/
git commit -m "feat(be): [ne değişti]"
git push

# main'den güncelleme al (FS merge yaptıktan sonra)
git checkout feat/backend
git merge main      # Veya: git pull origin main --rebase
```

### Commit Mesaj Formatı
```
feat(be): POST /api/analyze endpoint
feat(be): SQLite kurulumu
fix(be): boş input 400 dönmüyordu
docs(be): architecture.md güncellendi
```

### Yasak Komutlar
- `git push --force` — asla
- `git reset --hard` — asla (kendi dosyanı kaybetmek istemiyorsan)
- `main` branch'e direkt commit — sadece FS

---

## 12. ORTAK: Takım İletişim Protokolü

### Senin Bildirmen Gerekenler

| Olay | Kime | Mesaj Örneği |
|------|------|--------------|
| Mimari hazır | Tüm ekip | "DB şeması ve endpoint listesi `architecture.md`'de" |
| /api/analyze çalışıyor | Frontend, FS | "POST /api/analyze hazır, body: `{input}`, response: `{success, data}`" |
| Yeni endpoint | Frontend | "GET /api/results eklendi, kayıtları listeliyor" |
| DB şeması değişti | AI Uzmanı, FS | "analyses tablosuna `position` alanı ekledim" |
| Tıkandım | Tüm ekip | "Multer upload'da hata var, 15 dk içinde çözeceğim" |

### Sana Bildirilenler
- AI Uzmanı: "aiService.analyze fonksiyonu Promise döndürüyor, await edin"
- Frontend: "POST /api/analyze 500 dönüyor, log'a bakar mısın"
- FS: "Merge yapıyorum, 2 dk commit atma"

### Saatlik Sync (FS yönetir)
Her saat 2 dakika dur:
- Ne yaptın
- 30 dk sonra ne yapacaksın
- Tıkanmış mısın

---

## 13. ORTAK: Acil Durum

### Senaryo 1: Tüm endpoint'ler 500 dönüyor
**Çözüm:**
- `backend-node/src/index.js`'de errorHandler middleware sırası doğru mu? (en sonda olmalı)
- Console log'larına bak (`npm run dev` çıktısı)
- Try/catch eksik bir yer var mı?

### Senaryo 2: SQLite tamamen bozuldu
**Çözüm:**
```bash
rm backend-node/hackathon.db
# Server'ı restart et, tablo yeniden oluşur
```
Veri kaybı sorun değil — demo verileri yeniden ekleyebilirsin.

### Senaryo 3: AI çağrısı timeout
**Çözüm:**
- AI Uzmanı'na söyle: prompt'u kısalt, max_tokens'ı düşür
- Fallback yanıt zaten aktif (aiService.js'de)
- Demo öncesi cache: birkaç başarılı yanıtı DB'de tut, demo için aynı input'u kullan

### Senaryo 4: Frontend bağlanamıyor (CORS)
**Çözüm:** Yukarıda "Hata 3" çözümüne bak. Hızlı çözüm: `app.use(cors())` (sınırsız)

### Senaryo 5: AI Uzmanı'nın aiService.js'i bozuldu
**Çözüm:**
- `aiService.js`'in son çalışan versiyonuna git'ten geri dön:
  ```bash
  git log --oneline backend-node/src/services/aiService.js
  git checkout <hash> -- backend-node/src/services/aiService.js
  ```
- AI Uzmanı ile birlikte fix yap

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce:

- [ ] `feat/backend` branch'in `main`'e merge edilmiş
- [ ] `main` üzerinde tüm endpoint'ler çalışıyor
- [ ] `hackathon.db` dosyası mevcut ve okuyor
- [ ] `.env` git'e gitmemiş
- [ ] `AI_CONTEXT/2_architecture.md` final hâliyle güncel
- [ ] Frontend ile uçtan uca akış başarılı

### Senin son commit'in
```bash
git checkout feat/backend
git status
git log --oneline -5
git push origin feat/backend
```

---

## 15. Hızlı Referans

### Endpoint şablonu (kopyala-yapıştır)
```javascript
router.METHOD('/path', async (req, res, next) => {
  try {
    // Validation
    if (!req.body.X) return res.status(400).json({ success: false, error: 'X gerekli' });

    // İş mantığı
    const result = await someService.do(req.body.X);

    // DB
    saveAnalysis({...});

    // Yanıt
    res.json({ success: true, data: result });
  } catch (err) {
    next(err); // errorHandler middleware'i halleder
  }
});
```

### SQLite hızlı sorgular
```javascript
// SELECT
db.prepare('SELECT * FROM t WHERE x = ?').all(value);   // .all() = array
db.prepare('SELECT * FROM t WHERE id = ?').get(id);     // .get() = tek satır

// INSERT
const info = db.prepare('INSERT INTO t (a,b) VALUES (?,?)').run('x', 'y');
console.log(info.lastInsertRowid);

// UPDATE / DELETE
db.prepare('UPDATE t SET x = ? WHERE id = ?').run(newVal, id);
db.prepare('DELETE FROM t WHERE id = ?').run(id);
```

### `/ship` komutu (Claude Code'da)
```
/ship backend-node/src/routes/api.js'e POST /api/upload endpoint'i ekle, multer ile pdf kabul etsin
```
4 agent (planner→coder→tester→reviewer) otomatik çalışır.

---

**Başarılar! Motor odası senin. ⚙️**
