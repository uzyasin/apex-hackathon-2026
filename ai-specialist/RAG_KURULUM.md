# RAG Kurulum Rehberi
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

## Adım 1: Belgeyi Hazırla

Eğer PDF varsa önce metne çevir. `pdf-parse` paketini kullan:

```bash
# backend-node klasöründe
npm install pdf-parse
```

```javascript
// backend-node/src/services/documentService.js
const fs = require('fs');
const pdfParse = require('pdf-parse');

async function extractText(filePath) {
  const ext = filePath.split('.').pop().toLowerCase();

  if (ext === 'pdf') {
    const buffer = fs.readFileSync(filePath);
    const data = await pdfParse(buffer);
    return data.text;
  }

  if (ext === 'txt' || ext === 'md') {
    return fs.readFileSync(filePath, 'utf-8');
  }

  throw new Error(`Desteklenmeyen dosya tipi: ${ext}`);
}

module.exports = { extractText };
```

---

## Adım 2: Metni Parçalara Böl (Chunking)

```javascript
// backend-node/src/services/documentService.js — devamı

function chunkText(text, chunkSize = 500, overlap = 50) {
  const words = text.split(/\s+/);
  const chunks = [];

  for (let i = 0; i < words.length; i += chunkSize - overlap) {
    const chunk = words.slice(i, i + chunkSize).join(' ');
    if (chunk.trim().length > 0) {
      chunks.push({ id: chunks.length, text: chunk });
    }
  }

  return chunks;
}

module.exports = { extractText, chunkText };
```

**chunkSize ne olmalı?**
- 300-500 kelime genellikle iyi çalışır
- Çok küçükse bağlam kaybolur, çok büyükse token israfı olur

---

## Adım 3: İlgili Chunk'ı Bul (Basit Kelime Eşleştirme)

Vektör embedding kullanmak yerine basit TF-IDF benzeri kelime eşleştirme yap.
4 saatte bu yeterince iyi çalışır:

```javascript
// backend-node/src/services/ragService.js
const { extractText, chunkText } = require('./documentService');

let chunks = [];

function loadDocument(filePath) {
  // Uygulama başlarken bir kez çağır
  const text = require('fs').existsSync(filePath)
    ? require('fs').readFileSync(filePath, 'utf-8')
    : '';
  chunks = chunkText(text);
  console.log(`[RAG] ${chunks.length} chunk yüklendi: ${filePath}`);
}

function findRelevantChunks(query, topK = 3) {
  const queryWords = query.toLowerCase().split(/\s+/);

  const scored = chunks.map((chunk) => {
    const chunkWords = chunk.text.toLowerCase();
    const score = queryWords.reduce((acc, word) => {
      const count = (chunkWords.match(new RegExp(word, 'g')) || []).length;
      return acc + count;
    }, 0);
    return { ...chunk, score };
  });

  return scored
    .filter((c) => c.score > 0)
    .sort((a, b) => b.score - a.score)
    .slice(0, topK)
    .map((c) => c.text);
}

function buildContext(query) {
  const relevant = findRelevantChunks(query);
  if (relevant.length === 0) return null;
  return relevant.join('\n\n---\n\n');
}

module.exports = { loadDocument, buildContext };
```

---

## Adım 4: aiService.js ile Birleştir

`aiService.js` dosyasına şu fonksiyonu ekle:

```javascript
const { buildContext } = require('./ragService');

async function analyzeWithRAG(userQuestion) {
  const context = buildContext(userQuestion);

  const systemPrompt = context
    ? `You are a knowledgeable assistant. Answer using ONLY the provided context.
If the answer is not in the context, say "Bu bilgi belgede bulunmuyor."

Respond in JSON:
{
  "answer": "string",
  "source_quote": "exact quote from context",
  "confidence": "high | medium | low"
}`
    : SYSTEM_PROMPT; // context yoksa normal prompt

  const userMessage = context
    ? `CONTEXT:\n---\n${context}\n---\n\nSORT: ${userQuestion}`
    : userQuestion;

  try {
    const message = await client.messages.create({
      model: MODEL,
      max_tokens: 1024,
      system: systemPrompt,
      messages: [{ role: 'user', content: userMessage }],
    });
    return JSON.parse(message.content[0].text.trim());
  } catch (err) {
    console.error('[aiService] RAG error:', err.message);
    return FALLBACK_RESPONSE;
  }
}

module.exports = { analyze, analyzeWithContext, analyzeWithRAG };
```

---

## Adım 5: Belgeyi Sunucu Başlangıcında Yükle

`backend-node/src/index.js` dosyasına ekle:

```javascript
const { loadDocument } = require('./services/ragService');

// Sunucu başlarken belgeyi yükle
// Dosya yolunu yarışmada verilen belgeye göre değiştir
loadDocument('./data/document.pdf'); // veya .txt, .md
```

`backend-node/data/` klasörü oluştur, yarışmada verilen belgeyi oraya koy.

---

## Adım 6: Endpoint Ekle

`src/routes/api.js` dosyasına:

```javascript
const { analyzeWithRAG } = require('../services/aiService');

router.post('/ask', async (req, res, next) => {
  try {
    const { question } = req.body;
    if (!question) return res.status(400).json({ success: false, error: 'question gerekli' });

    const result = await analyzeWithRAG(question);
    res.json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
});
```

---

## Test Et

```bash
# Önce belgeyi data/ klasörüne koy, backend'i yeniden başlat

curl -X POST http://localhost:3001/api/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "belgede yazılan konu nedir"}'
```

---

## Dosya Upload ile RAG (Kullanıcı Belge Yüklüyorsa)

Eğer kullanıcı frontend'den belge yüklüyorsa, `multer` zaten `package.json`'da hazır:

```javascript
// src/routes/api.js
const multer = require('multer');
const upload = multer({ dest: 'uploads/' });
const { extractText, chunkText } = require('../services/documentService');

let sessionChunks = [];

router.post('/upload', upload.single('file'), async (req, res, next) => {
  try {
    const text = await extractText(req.file.path);
    sessionChunks = chunkText(text);
    res.json({ success: true, data: { chunkCount: sessionChunks.length } });
  } catch (err) {
    next(err);
  }
});
```

---

## Sorun Giderme

**"Chunk bulunamadı" — AI "bilgi yok" diyor ama belgede var**
→ Kelime eşleştirme kısmi eşleşmeyi bulamıyor. `queryWords` filtresini gevşet:
```javascript
.filter((c) => c.score >= 0)  // score > 0 yerine >= 0
```
veya `topK` değerini 5-8'e çıkar.

**Yanıt yavaş**
→ `max_tokens`'ı düşür (1024 → 512).
→ `topK`'yı küçült (3 → 2).

**JSON parse hatası**
→ System prompt'a: `"Never use markdown. Never use code fences."` ekle.
