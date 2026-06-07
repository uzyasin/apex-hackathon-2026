# PROJECT ARCHITECTURE & SCHEMA
# ⚠️ FILL THIS IN THE FIRST 15 MINUTES OF THE HACKATHON ⚠️

---

## Product Summary
**What does this app do?**
> [FILL IN: One sentence description of the product]

**Core User Flow:**
> [FILL IN: User does X → system does Y → user sees Z]

---

## Active Backend
> [FILL IN: Node.js or Java]

## API Base URL
- Development: `http://localhost:3001/api`
- Frontend proxy: configured in `frontend/vite.config.js`

---

## Directory Map
```
hackathon/
├── backend-node/src/
│   ├── routes/api.js          ← HTTP endpoints
│   ├── services/aiService.js  ← Anthropic Claude calls
│   └── index.js               ← Express server
├── backend-java/src/main/java/com/hackathon/
│   ├── controller/            ← HTTP endpoints
│   ├── service/               ← Business + AI logic
│   └── dto/                   ← Request/Response objects
├── frontend/src/
│   ├── api/client.js          ← All fetch calls to backend
│   ├── components/            ← Reusable UI pieces
│   ├── pages/                 ← Full page views
│   └── App.jsx                ← Routing
└── AI_CONTEXT/                ← This folder (AI reference)
```

---

## Database Schema
> Database: SQLite (Node) / H2 in-memory (Java) — zero config

### Table: [TABLE_NAME_1]
> [FILL IN after deciding product]
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `created_at`: DATETIME DEFAULT CURRENT_TIMESTAMP
- `field_1`: TEXT NOT NULL
- `field_2`: TEXT

### Table: [TABLE_NAME_2]
> [FILL IN]
- `id`: INTEGER PRIMARY KEY AUTOINCREMENT
- `parent_id`: INTEGER REFERENCES [TABLE_NAME_1](id)
- `result`: TEXT
- `status`: TEXT DEFAULT 'pending'  -- pending | completed | failed

---

## API Endpoints
> [FILL IN as you build — keep this updated so all agents know the contract]

| Method | Path | Description | Request Body | Response |
|--------|------|-------------|--------------|----------|
| POST | /api/analyze | Main AI analysis | `{ input: string }` | `{ success, data }` |
| GET | /api/health | Health check | — | `{ status: "ok" }` |

---

## AI Integration
- **Provider:** Anthropic Claude
- **Model:** `claude-sonnet-4-6`
- **Primary feature:** [FILL IN: e.g., document analysis / text classification / RAG]
- **Input format:** [FILL IN: plain text / JSON / file upload]
- **Output format:** JSON with fields: `{ ... }`

---

## Environment Variables Required
```
ANTHROPIC_API_KEY=sk-ant-...
PORT=3001
```
