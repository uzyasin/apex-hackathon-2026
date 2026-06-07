# Hackathon Boilerplate

4 saatlik AI hackathon başlangıç kiti — Java Spring Boot + React + Anthropic Claude.

---

## İlk 15 Dakika Checklist

- [ ] `ANTHROPIC_API_KEY` env variable'ı set et (aşağıdaki komut)
- [ ] `AI_CONTEXT/2_architecture.md` dosyasını ürün açıklaması ve DB şeması ile doldur
- [ ] `SKILLS.md` başlığına proje adını yaz (opsiyonel)
- [ ] Rolleri ata: AI Uzmanı / Backend / Frontend / Git Yöneticisi

---

## Hızlı Başlangıç

### 1. Anthropic API Anahtarını Set Et

```bash
# Windows PowerShell
$env:ANTHROPIC_API_KEY="sk-ant-..."

# Windows cmd
set ANTHROPIC_API_KEY=sk-ant-...

# Linux/Mac
export ANTHROPIC_API_KEY=sk-ant-...
```

### 2. Backend (Java Spring Boot)

```bash
cd backend
mvn spring-boot:run
# → http://localhost:3001 üzerinde çalışıyor
```

### 3. Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
# → http://localhost:5173 üzerinde çalışıyor
```

Tarayıcıda `http://localhost:5173` aç.

---

## Proje Yapısı

```
.
├── .claude/
│   ├── agents/             ← 4-agent pipeline (planner, coder, tester, reviewer)
│   └── commands/           ← /ship slash command
├── .pipeline/              ← Agent handoff dosyaları (otomatik oluşur)
├── AI_CONTEXT/
│   ├── 1_system_rules.md   ← AI araçları için kodlama standartları
│   ├── 2_architecture.md   ← ⚠️ HACKATHON BAŞINDA DOLDUR
│   └── 3_ai_prompts.md     ← Claude system prompt şablonları
├── SKILLS.md               ← Universal AI context (herhangi bir AI chat'ine yapıştır)
├── backend/                ← Java 21 + Spring Boot 3.x backend
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hackathon/
│       │   ├── HackathonApplication.java
│       │   ├── controller/ApiController.java
│       │   ├── service/  (AiService, DbService, DocumentService)
│       │   ├── dto/      (ApiResponse, AnalyzeRequest, AiResult)
│       │   └── config/   (CorsConfig, GlobalExceptionHandler)
│       └── resources/application.yml
└── frontend/               ← React + Vite + Tailwind
```

---

## 4-Agent Pipeline (Claude Code)

Claude Code terminalinde tam Planner→Coder→Tester→Reviewer zincirini tetikle:

```
/ship file upload endpoint ekle, metin çıkarsın ve Claude'a göndersin
```

Pipeline:
1. **Planner** spec yazar → `.pipeline/spec.md`
2. **Coder** kodu yazar → `.pipeline/changes.md`
3. **Tester** test yazar + çalıştırır → `.pipeline/test-results.md`
4. **Reviewer** SHIP / NEEDS WORK / BLOCK verdict verir

OPEN QUESTIONS veya test hatası → pipeline durur, human-in-the-loop devam eder.

---

## AI Context Files (Herhangi Bir AI Tool için)

ChatGPT, Copilot, Cursor veya başka bir tool kullanıyorsan:

1. `SKILLS.md` içeriğini ilk mesaj olarak yapıştır
2. "Hazırım" cevabını al
3. Kod istekleri yapmaya başla — AI stack'ini ve kurallarını biliyor

---

## 4 Saatlik Zaman Bütçesi

| Süre | Aktivite |
|------|----------|
| 0:00–0:30 | architecture.md doldur, görev dağılımı, /ship ile ilk feature |
| 0:30–3:00 | Feature geliştirme (/ship pipeline veya manuel) |
| 3:00–3:45 | Entegrasyon, uçtan uca manuel test |
| 3:45–4:00 | Git push, README'yi "nasıl çalıştırılır" ile güncelle |

---

## API Sözleşmesi

Tüm endpoint'ler bu formatta yanıt verir:
```json
{ "success": true, "data": { ... } }
{ "success": false, "error": "mesaj" }
```

Endpoint'ler:
- `GET /api/health` → durum kontrolü
- `POST /api/analyze` → `{ "input": "string", "context": "opsiyonel" }` — ana AI çağrısı
- `GET /api/results` → geçmiş analizler
- `GET /api/results/{id}` → tek kayıt
- `POST /api/upload` → multipart dosya yükleme (PDF/TXT/MD)

---

## H2 Database Console

Geliştirme sırasında DB'yi browser üzerinden gör:
```
http://localhost:3001/h2-console
JDBC URL: jdbc:h2:mem:hackathon
Username: sa
Password: (boş)
```
