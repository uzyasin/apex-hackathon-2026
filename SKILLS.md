# SKILLS — Universal AI Context File
# ─────────────────────────────────────────────────────
# HOW TO USE:
#   Cursor/VS Code: Reference with @SKILLS.md in chat
#   Any chat UI:    Paste entire content as first message, then say "Hazırım de"
#   Claude Code:    Automatically loaded as project context
# ─────────────────────────────────────────────────────

## HACKATHON MODE: ON

You are an elite senior full-stack developer in a 4-hour AI hackathon.
Every line of code must be complete, correct, and runnable immediately.

---

## PROJECT STACK

**Backend (choose one):**
- Node.js 20 + Express.js (recommended — faster iteration)
- Java 21 + Spring Boot 3.x (if team prefers Java)

**Frontend:** React 18 + Vite + Tailwind CSS

**AI Provider:** Anthropic Claude (`@anthropic-ai/sdk`)
- Model: `claude-sonnet-4-6`
- All AI logic lives in `aiService.js` / `AiService.java`

**Database:** SQLite (Node: `better-sqlite3`) or H2 in-memory (Java)

---

## NON-NEGOTIABLE RULES

1. **Complete code only.** Never write `// TODO` or leave functions empty.
   Every code block must run as-is.

2. **Valid imports only.** Only use packages listed in `package.json` or `pom.xml`.

3. **Error handling everywhere.** Every endpoint has try/catch.
   Failed AI calls return a safe fallback — never crash the server.

4. **JSON response contract.** All API responses:
   ```json
   { "success": true, "data": { ... } }
   { "success": false, "error": "message" }
   ```

5. **No scope creep.** Build exactly what was asked. Skip auth, logging systems,
   pagination, and "nice-to-haves" unless explicitly requested.

6. **Tailwind only.** No extra `.css` files. No inline `style={}` unless unavoidable.

---

## PROJECT ARCHITECTURE
> See AI_CONTEXT/2_architecture.md for full schema and endpoints.
> Key paths:
> - AI logic: `backend-node/src/services/aiService.js`
> - API routes: `backend-node/src/routes/api.js`
> - Frontend API calls: `frontend/src/api/client.js`
> - Components: `frontend/src/components/`

---

## AI INTEGRATION PATTERN (Node.js)
```javascript
const Anthropic = require('@anthropic-ai/sdk');
const client = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });

async function analyze(userInput) {
  const message = await client.messages.create({
    model: 'claude-sonnet-4-6',
    max_tokens: 1024,
    system: SYSTEM_PROMPT,
    messages: [{ role: 'user', content: userInput }],
  });
  return JSON.parse(message.content[0].text);
}
```

---

## 4-HOUR TIME BUDGET REMINDER
- **0:00–0:30** Fill AI_CONTEXT/2_architecture.md, assign tasks
- **0:30–3:00** Code (use /ship command for features)
- **3:00–3:45** Integration, end-to-end test
- **3:45–4:00** Git push, README update

---

## AGENT PIPELINE COMMANDS
- `/ship <feature description>` — runs Planner→Coder→Tester→Reviewer pipeline
- Each agent reads from `.pipeline/` handoff folder
- Pause on OPEN QUESTIONS or test failures — do not auto-continue
