# Hackathon Boilerplate

4-hour AI hackathon starter kit — React + (Node.js or Java Spring Boot) + Anthropic Claude.

---

## First 15 Minutes Checklist

- [ ] Copy `.env.example` → `.env` and fill `ANTHROPIC_API_KEY`
- [ ] Fill `AI_CONTEXT/2_architecture.md` with product description and DB schema
- [ ] Update `SKILLS.md` header with project name (optional)
- [ ] Assign roles: AI Specialist / Backend / Frontend / Full-Stack Support
- [ ] Decide backend: **Node.js** or **Java** (see below)

---

## Quick Start

### Option A — Node.js Backend

```bash
cd backend-node
cp .env.example .env   # fill ANTHROPIC_API_KEY
npm install
npm run dev            # runs on :3001
```

```bash
cd frontend
npm install
npm run dev            # runs on :5173
```

### Option B — Java Spring Boot Backend

```bash
cd backend-java
# Set ANTHROPIC_API_KEY as env var or edit application.yml
mvn spring-boot:run    # runs on :3001
```

```bash
cd frontend
npm install
npm run dev            # runs on :5173
```

---

## Project Structure

```
.
├── .claude/
│   ├── agents/         ← 4-agent pipeline (planner, coder, tester, reviewer)
│   └── commands/       ← /ship slash command
├── .pipeline/          ← Agent handoff files (auto-generated during /ship)
├── AI_CONTEXT/
│   ├── 1_system_rules.md   ← Coding standards for AI assistants
│   ├── 2_architecture.md   ← ⚠️ FILL AT HACKATHON START
│   └── 3_ai_prompts.md     ← Claude system prompts for the product
├── SKILLS.md               ← Universal context — paste into any AI chat
├── backend-node/           ← Express.js backend
├── backend-java/           ← Spring Boot backend
└── frontend/               ← React + Vite + Tailwind
```

---

## Using the 4-Agent Pipeline

In Claude Code terminal, trigger the full Planner→Coder→Tester→Reviewer chain:

```
/ship add a file upload endpoint that extracts text and sends to Claude
```

The pipeline will:
1. **Planner** writes a spec to `.pipeline/spec.md`
2. **Coder** implements it and writes `.pipeline/changes.md`
3. **Tester** writes + runs tests, writes `.pipeline/test-results.md`
4. **Reviewer** gives a SHIP / NEEDS WORK / BLOCK verdict

Pipeline pauses for OPEN QUESTIONS and test failures — always human-in-the-loop.

---

## AI Context Files (for any AI tool)

When using ChatGPT, Copilot, or any other tool:

1. Open `SKILLS.md` and paste its content as the first message
2. Say "Hazırım de" (or "Acknowledged")
3. Start coding requests — the AI now knows your stack and rules

---

## 4-Hour Time Budget

| Time | Activity |
|------|----------|
| 0:00–0:30 | Fill architecture.md, assign tasks, /ship first skeleton feature |
| 0:30–3:00 | Feature development via /ship pipeline |
| 3:00–3:45 | Integration, end-to-end manual test |
| 3:45–4:00 | Git push, update README with "how to run" |

---

## API Contract

All endpoints respond with:
```json
{ "success": true, "data": { ... } }
{ "success": false, "error": "message" }
```

Main endpoint: `POST /api/analyze` — `{ "input": "string", "context": "optional string" }`
