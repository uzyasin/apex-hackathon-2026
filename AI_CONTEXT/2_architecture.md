# PROJECT ARCHITECTURE & SCHEMA
# ⚠️ FILL THIS IN THE FIRST 15 MINUTES OF THE HACKATHON ⚠️

---

## Product Summary
**What does this app do?**
IFTS AI Hackathon: AI-Powered Agile Manager
Tema: Yapay Zeka Destekli Akıllı Scrum/Kanban Asistanı ve Yönetim Paneli
Hedef: Geleneksel Scrum/Kanban süreçlerindeki sprint planlama ve raporlama iş yükünü optimize ederek, takımların tamamen "kod geliştirmeye" ve "değer üretmeye" odaklanmasını sağlamak.
📋 Beklenen Ana Özellikler (Epikler)
Yarışmacıların geliştireceği MVP (Minimum Uygulanabilir Ürün), aşağıdaki üç ana yeteneğe sahip olmalıdır:
• 1. Akıllı Planlama (Predictive Planning):
o Jira backlog'undan seçilen task’ları ve size’larını okuyabilmeli. (Jira üzerinde bir yazma işlemi yapılmamalıdır, sadece okuma.)
o Takımın geçmiş sprint verilerini (hız/velocity) analiz ederek, backlog’dan planlamaya dahil edilen yeni task’ların size’ları için objektif yapay zeka tahmini (Predictive Sizing) yapabilmeli.
o Bonus: Task’ların bloklanma nedenlerine AI desteği için çözümler önerebilmeli.
• 2. Otomatik Görev Kırılımı ve Akıllı Atama (Task Decomposition):
o Planlamaya dahil edilen bir task’ı saniyeler içinde mantıklı teknik alt görevlere (Frontend, Backend, DB, Test vb.) bölmeli.
o Takım üyelerinin yetkinlik matrisine ve mevcut sprint yüklerine (kapasite) bakarak, görevleri en uygun kişilere akıllı eşleştirme ile en uygun atamayı önermeli.
o Oluşturulan bu alt görevleri ve atama önerilerini rapor olarak sunabilmeli.
• 3. AI Sprint Review ve Yönetici Paneli (Dashboard):
o "Bu Sprint Ne Başardık?" odaklı otomatik bir demo raporu ve özet metni üretmeli.
o Planlanan vs. Gerçekleşen (Süre/Puan) sapma metriklerini görselleştiren bir dashboard sunmalı.
o Bonus: Gerçekleştirilemeyen task’lar için sprint’ler arası geçişkenlik (sonraki sprint’lere kalma durumu) metriği hesaplamalı.
o Bonus: Sprint için 1-100 skalasında sprint-health skoru hesaplamalı.

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
- **Primary feature:** Sprint analizi, task decomposition, sprint review raporu
- **Input format:** Serbest metin (sprint verileri, backlog task'ları, takım hız geçmişi)
- **Output format:** JSON — `AiResult` DTO (aşağıya bakın)
- **Fallback:** API hata verirse `AiResult.fallback()` (statik yanıt) döner

### AI Çıktı Şeması (AiResult DTO)
```json
{
  "sprint_health_score": 78,
  "summary": "Sprint durumu kısa özeti (2-3 cümle)",
  "task_breakdown": [
    {
      "title": "Alt görev adı",
      "type": "Frontend | Backend | DB | Test | DevOps",
      "story_points": 3,
      "suggested_assignee": "Rol veya isim"
    }
  ],
  "risks": ["Risk açıklaması"],
  "recommendations": ["Öneri açıklaması"],
  "verdict": "Planlanabilir | Revize Gerekli | Reddedilmeli"
}
```

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
