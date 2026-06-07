# PROJECT ARCHITECTURE & SCHEMA
# ⚠️ FILL THIS IN THE FIRST 15 MINUTES OF THE HACKATHON ⚠️

---

## Product Summary
**What does this app do?**
> [FILL IN: One sentence description of the product]

**Core User Flow:**
> [FILL IN: User does X → system does Y → user sees Z]

---

## Backend
**Active Backend:** Java 21 + Spring Boot 3.2.5

**Build & Run:**
```bash
cd backend
mvn spring-boot:run    # http://localhost:3001
```

**API Base URL:**
- Development: `http://localhost:3001/api`
- Frontend proxy: `frontend/vite.config.js` üzerinden `/api` → `http://localhost:3001`

---

## Directory Map
```
hackathon/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hackathon/
│       │   ├── HackathonApplication.java
│       │   ├── controller/
│       │   │   └── ApiController.java          ← HTTP endpoints
│       │   ├── service/
│       │   │   ├── AiService.java              ← Claude API calls + SYSTEM_PROMPT
│       │   │   ├── DbService.java              ← H2 + JdbcTemplate CRUD
│       │   │   └── DocumentService.java        ← PDF/TXT text extraction (PDFBox)
│       │   ├── dto/
│       │   │   ├── ApiResponse.java            ← {success, data, error} wrapper
│       │   │   ├── AnalyzeRequest.java         ← {input, context}
│       │   │   └── AiResult.java               ← AI çıktı şeması
│       │   └── config/
│       │       ├── CorsConfig.java
│       │       └── GlobalExceptionHandler.java
│       └── resources/
│           └── application.yml
├── frontend/src/
│   ├── api/client.js                    ← Axios calls to backend
│   ├── components/                      ← Reusable UI pieces
│   ├── pages/                           ← Full page views
│   └── App.jsx                          ← Routing
└── AI_CONTEXT/                          ← This folder (AI reference)
```

---

## Database Schema
**Database:** H2 in-memory (`jdbc:h2:mem:hackathon`) — zero config

### Table: analyses (zaten DbService.init() içinde mevcut)
- `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
- `input`: CLOB NOT NULL
- `context`: CLOB
- `result_json`: CLOB
- `score`: INT
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Table: [YENİ_TABLO_1]
> [FILL IN ürüne göre — DbService.init()'e CREATE TABLE eklenecek]
- `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
- `created_at`: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- `field_1`: VARCHAR(255) NOT NULL
- `field_2`: CLOB

### Table: [YENİ_TABLO_2]
> [FILL IN]
- `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
- `parent_id`: BIGINT REFERENCES [YENİ_TABLO_1](id)
- `status`: VARCHAR(50) DEFAULT 'pending'  -- pending | completed | failed

**H2 Console:** http://localhost:3001/h2-console (JDBC URL: `jdbc:h2:mem:hackathon`, user: `sa`, no password)

---

## API Endpoints

| Method | Path | Description | Request Body | Response.data |
|--------|------|-------------|--------------|---------------|
| GET | /api/health | Health check | — | `{ status: "ok" }` |
| POST | /api/analyze | Main AI analysis | `{ input, context }` | `{ id, summary, insights, score, recommendation }` |
| GET | /api/results | Tüm analizler (son 100) | — | `[{ id, input, score, created_at }]` |
| GET | /api/results/{id} | Tek kayıt | — | `{ id, input, context, result_json, score, created_at }` |
| POST | /api/upload | Dosya yükleme (PDF/TXT/MD) | `multipart: file` | `{ filename, size, text }` |

> [FILL IN as you build — keep this updated so all agents know the contract]

---

## AI Integration
- **Provider:** Anthropic Claude (via direct HTTP POST with `RestTemplate`)
- **Model:** `claude-sonnet-4-6`
- **Primary feature:** [FILL IN: e.g., document analysis / text classification / RAG]
- **Input format:** [FILL IN: plain text / JSON / file upload]
- **Output format:** JSON with fields: `{ summary, insights, score, recommendation }` (AiResult DTO)
- **Fallback:** API hata verirse `AiResult.fallback()` (statik yanıt) döner

---

## Environment Variables Required
```
ANTHROPIC_API_KEY=sk-ant-...
```

Set etme:
- Windows PowerShell: `$env:ANTHROPIC_API_KEY="sk-ant-..."`
- Windows cmd: `set ANTHROPIC_API_KEY=sk-ant-...`
- Linux/Mac: `export ANTHROPIC_API_KEY=sk-ant-...`

application.yml otomatik okur: `${ANTHROPIC_API_KEY:set-via-env-var}`

---

## Dependencies (pom.xml'de var)
- spring-boot-starter-web (REST, JSON, RestTemplate)
- spring-boot-starter-jdbc (JdbcTemplate)
- spring-boot-starter-validation
- h2 (in-memory DB)
- jackson-databind (JSON parsing)
- pdfbox 3.0.1 (PDF text extraction)
- lombok (DTO boilerplate)
