# Proje Bağlamı — Yeni AI Oturumu İçin Brifing

> **Kullanım:** Yeni bir AI oturumu (Cursor, ChatGPT, Claude.ai, Gemini, vb.) açtığında ilk mesaj olarak bu dosyanın tüm içeriğini yapıştır. AI projeyi anlar, sen tekrar anlatmak zorunda kalmazsın.

---

## TL;DR

Bu repo, **yarın yapılacak 4 saatlik AI hackathonu** için hazırlanan tam bir boilerplate'dir. Konu yarışma günü verilecek; 4 kişilik takım, hazır altyapı üzerinde MVP geliştirip git'e push edecek. Tüm hazırlık (kod iskeleti, agent pipeline, rol rehberleri, AI sözleşmeleri) tamamlanmış durumda. Yarışma günü yalnızca konuya özel iş mantığı yazılacak.

---

## 1. Yarışma Konsepti

- **Süre:** 4 saat
- **Takım:** 4 kişi (AI Uzmanı + Backend + Frontend + Git Yöneticisi)
- **Çıktı:** Çalışan yazılım projesi, GitHub repo'suna push edilmiş
- **AI Provider:** Anthropic Claude (model: `claude-sonnet-4-6`)
- **Yarışma günü ilk 30 dakika:** Konu analizi, mimari kararları, kapsam daraltma
- **Sonraki 2.5 saat:** Paralel kodlama
- **Son 45 dakika:** Entegrasyon + test
- **Son 15 dakika:** README + git push + teslim

---

## 2. Tech Stack (Karar Verilmiş)

- **Backend:** Java 21 + Spring Boot 3.2.5 + Maven
  - HTTP: `RestTemplate` (Anthropic API'ye direkt çağrı)
  - DB: H2 in-memory + `JdbcTemplate`
  - PDF parsing: Apache PDFBox 3.x
  - Validation: `jakarta.validation`
  - DTO boilerplate'i için Lombok
- **Frontend:** React 18 + Vite + Tailwind CSS + Axios
- **AI:** Anthropic Claude `claude-sonnet-4-6` (RestTemplate ile HTTP)
- **Versiyon kontrolü:** Git, branch-per-role stratejisi

**Neden Java?** Kullanıcı Java ve JavaScript biliyor; Spring Boot ile rahat çalışıyor. Backend Java, frontend JavaScript şeklinde bölünüyor.

---

## 3. Klasör Yapısı

```
hackathon/
├── .claude/                     ← Claude Code için 4-agent pipeline
│   ├── agents/                  ← planner.md, coder.md, tester.md, reviewer.md
│   └── commands/ship.md         ← /ship komutu → full feature pipeline
├── .pipeline/                   ← Agent handoff dosyaları (runtime'da oluşur)
├── AI_CONTEXT/                  ← Tüm AI araçlarına yapıştırılan referans
│   ├── 1_system_rules.md        ← Kodlama standartları, hata yönetimi kuralları
│   ├── 2_architecture.md        ← ⚠️ Yarışma sabahı doldurulacak: DB şeması, endpoint listesi
│   └── 3_ai_prompts.md          ← Claude system prompt şablonları
├── ai-specialist/               ← AI Uzmanı'nın çalışma alanı
│   ├── PROMPT_GELISTIRME.md     ← Prompt iterasyon defteri (v1→v2→v3)
│   ├── AI_CIKTI_SOZLESMESI.md   ← ⚠️ İlk 20 dk: AI JSON şeması, Backend/Frontend bunu okur
│   └── RAG_KURULUM.md           ← Belge analizi gerekirse Java rehberi (PDFBox + in-memory)
├── backend/                     ← Java 21 + Spring Boot 3 boilerplate (her şey hazır)
│   ├── pom.xml                  ← Maven dependencies (web, jdbc, h2, pdfbox, lombok)
│   ├── src/main/
│   │   ├── java/com/hackathon/
│   │   │   ├── HackathonApplication.java
│   │   │   ├── controller/ApiController.java  ← /api endpoint'leri
│   │   │   ├── service/
│   │   │   │   ├── AiService.java             ← Anthropic Claude entegrasyonu
│   │   │   │   ├── DbService.java             ← H2 + JdbcTemplate CRUD
│   │   │   │   └── DocumentService.java       ← PDF/TXT text extraction
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java           ← {success, data, error} wrapper
│   │   │   │   ├── AnalyzeRequest.java        ← {input, context}
│   │   │   │   └── AiResult.java              ← AI JSON output
│   │   │   └── config/
│   │   │       ├── CorsConfig.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/application.yml
│   └── .env.example
├── frontend/                    ← React + Vite + Tailwind boilerplate
│   ├── src/
│   │   ├── App.jsx              ← BrowserRouter + routes
│   │   ├── main.jsx             ← React entry
│   │   ├── api/client.js        ← Axios + analyze() fonksiyonu
│   │   ├── components/          ← Layout.jsx, LoadingSpinner.jsx
│   │   └── pages/HomePage.jsx   ← Örnek sayfa (form + sonuç kartı)
│   ├── vite.config.js           ← Backend'e proxy ayarlı (port 3001)
│   ├── tailwind.config.js
│   └── package.json
├── takim-rehberi/               ← Her rol için ayrı detaylı rehber (Türkçe)
│   ├── 00_BASLA_BURADAN.md      ← Giriş, takım toplantısı, ilk feature walkthrough
│   ├── 01_AI_UZMANI.md          ← AI Model Uzmanı için self-contained rehber
│   ├── 02_BACKEND.md            ← Backend (Java Spring Boot) Geliştirici için
│   ├── 03_FRONTEND.md           ← Frontend Geliştirici için
│   └── 04_GIT_YONETICISI.md     ← FS & Git Yöneticisi için
├── mydocs/                      ← Kullanıcının orijinal araştırma notları
│   ├── plan.md                  ← Gemini ile hackathon stratejisi
│   └── 4 agent.md               ← 4-agent pipeline kavramı
├── SKILLS.md                    ← Universal AI context (her chat'e yapıştırılır)
├── README.md                    ← Proje açıklaması
├── KULLANIM_REHBERI.md          ← Boilerplate'in genel kullanım kılavuzu
├── TAKIM_REHBERI.md             ← takim-rehberi/ klasörüne pointer
├── PROJE_BAGLAMI.md             ← BU DOSYA (yeni AI oturumları için brifing)
├── .gitignore
└── .env.example
```

---

## 4. Önemli Dosyaların Amacı

| Dosya | Amacı | Ne zaman dokunulur |
|-------|-------|---------------------|
| `SKILLS.md` | Her AI'ya yapıştırılan evrensel kodlama bağlamı | Her yeni AI oturumu |
| `AI_CONTEXT/2_architecture.md` | DB şeması + endpoint listesi (canlı belge) | Yarışma sabahı doldurulur, sürekli güncellenir |
| `ai-specialist/AI_CIKTI_SOZLESMESI.md` | AI'nın döneceği JSON şeması | İlk 20 dakikada AI Uzmanı doldurur |
| `backend/src/main/java/com/hackathon/service/AiService.java` | Claude API çağrısı + `SYSTEM_PROMPT` constant'ı | AI Uzmanı sürekli iyileştirir |
| `backend/src/main/java/com/hackathon/controller/ApiController.java` | Tüm REST endpoint'leri | Backend yeni endpoint eklerken |
| `backend/src/main/java/com/hackathon/service/DbService.java` | H2 + JdbcTemplate CRUD | Backend yeni tablo eklerken |
| `.claude/agents/*.md` | 4-agent pipeline tanımları (planner→coder→tester→reviewer) | Sadece Claude Code kullanıyorsan |
| `.claude/commands/ship.md` | `/ship <feature>` komutu — full pipeline | Claude Code'da feature geliştirirken |

---

## 5. 4-Agent Pipeline (Sadece Claude Code Kullanılırsa)

`.claude/agents/` içinde 4 sub-agent var:

1. **planner** (Opus): Feature isteğini `.pipeline/spec.md`'ye dönüştürür, kod yazmaz
2. **coder** (Sonnet): Spec'i okur, kodu yazar, `.pipeline/changes.md`'ye özet bırakır
3. **tester** (Sonnet): Test yazar ve çalıştırır, `.pipeline/test-results.md`
4. **reviewer** (Opus): Read-only, SHIP/NEEDS WORK/BLOCK kararı verir

Kullanım: `/ship add file upload endpoint`

Cursor, VS Code, ChatGPT gibi başka tool'larda bu agent sistemi çalışmaz — bunun için Claude Code lazım. Diğer tool'larda kodlama doğrudan yapılır, `SKILLS.md` ve `AI_CONTEXT/` referans olarak kullanılır.

---

## 6. Git Stratejisi

```
main                  ← FS Yöneticisi merge eder, her zaman çalışır
├── feat/ai           ← AI Uzmanı
├── feat/backend      ← Backend Geliştirici
├── feat/frontend     ← Frontend Geliştirici
└── feat/support      ← FS Yöneticisi (gerekirse)
```

- Her rol kendi klasöründe çalışır → çakışma riski minimum
- 30 dakikada bir commit + push
- 2:30'da code freeze, FS yöneticisi merge başlatır
- Merge sırası: AI → Backend → Frontend (bağımlılık zinciri)

**Commit formatı:** `feat(role): kısa açıklama` (örn: `feat(ai): v2 prompt JSON zorlama`)

---

## 7. AI Çıktı Sözleşmesi (Veri Akışı)

Tüm endpoint'ler şu sözleşmeyle yanıt verir (Java `ApiResponse<T>` DTO):
```json
{ "success": true, "data": { ... }, "error": null }
{ "success": false, "data": null, "error": "Türkçe mesaj" }
```

Java tarafında: `ApiResponse.ok(data)` veya `ApiResponse.fail("mesaj")`.

Ana endpoint `POST /api/analyze`:
- **Request:** `{ "input": "string", "context": "opsiyonel string" }`
- **Response:** AI Uzmanı'nın yarışma sabahı tanımladığı şema (örn: `{summary, insights, score, recommendation}`)

AI yanıt veremezse `AiResult.fallback()` döner — uygulama asla çökmez.

---

## 8. Hazır Endpoint'ler

| Method | Path | Açıklama |
|--------|------|----------|
| GET | `/api/health` | Status check |
| POST | `/api/analyze` | Ana AI çağrısı, DB'ye kaydeder |
| GET | `/api/results` | Tüm analizler (son 100) |
| GET | `/api/results/{id}` | Tek analiz |
| POST | `/api/upload` | PDF/TXT/MD dosya yükleme + text extraction |

H2 Console: `http://localhost:3001/h2-console` (JDBC URL: `jdbc:h2:mem:hackathon`, user: `sa`, no pass)

---

## 9. Mevcut Durum: Ne Hazır, Ne Bekleniyor

### ✅ Hazır (Yarışmadan ÖNCE)
- Repo iskeleti
- Java Spring Boot backend tam çalışır halde:
  - HackathonApplication, ApiController, AiService (Claude), DbService (H2 + JdbcTemplate), DocumentService (PDFBox), DTO'lar, CorsConfig, GlobalExceptionHandler
  - 5 endpoint hazır: health, analyze, results, results/{id}, upload
- Frontend: React + Vite + Tailwind + örnek HomePage + Axios client
- `.claude/agents/` 4-agent pipeline tanımı
- AI context dosyaları (system_rules, prompt şablonları)
- AI uzmanı için çalışma alanı (PROMPT_GELISTIRME, AI_CIKTI_SOZLESMESI, RAG)
- Her rol için detaylı rehber (`takim-rehberi/`)
- README ve env.example dosyaları

### ⏳ Yarışma Günü Yapılacak (Konuya Göre)
- `AI_CONTEXT/2_architecture.md` doldurulur (DB şeması, endpoint listesi, ürün özeti)
- `ai-specialist/AI_CIKTI_SOZLESMESI.md` doldurulur (AI çıktı şeması)
- `ai-specialist/PROMPT_GELISTIRME.md` doldurulur (prompt iterasyon)
- `AiService.java` içindeki `SYSTEM_PROMPT` konuya özel yazılır
- `ApiController.java`'a ürüne özel endpoint'ler eklenir
- `DbService.java`'ya yeni tablolar eklenir (gerekirse)
- `frontend/src/pages/HomePage.jsx` ürüne göre yeniden yazılır
- `ANTHROPIC_API_KEY` env variable set edilir
- README.md son hâliyle yazılır

---

## 10. Bu AI Oturumunda Beklentiler

### Yapabileceğin İşler
1. **Yeni endpoint ekleme:** `ApiController.java`'ya `@PostMapping`/`@GetMapping` metodu
2. **Yeni DB tablosu:** `DbService.java`'ya `@PostConstruct` içinde `CREATE TABLE` + CRUD metotları
3. **Yeni servis sınıfı:** `service/` paketine `@Service` annotated class
4. **Yeni DTO:** `dto/` paketine `@Data` (Lombok) class
5. **AI prompt güncelleme:** `AiService.SYSTEM_PROMPT` constant'ı
6. **Frontend güncelleme:** `pages/`, `components/`, `api/client.js`
7. **Rehber güncelleme:** `takim-rehberi/`, `AI_CONTEXT/`

### Dikkat Etmen Gerekenler
- **Tüm dosyalar Türkçe yorumlanmış** — değişiklikleri Türkçe açıkla
- **Boilerplate yapısını koru** — klasör isimlerini, dosya konumlarını değiştirme
- **Tech stack kararları sabit:** Java 21 + Spring Boot, React + Vite + Tailwind, Anthropic Claude
- **`@Autowired` kullanma** — sadece constructor injection
- **Lombok zaten kurulu** — `@Data`, `@AllArgsConstructor` kullanılabilir
- **ApiResponse wrapper'ı kullan** — endpoint'ler `ResponseEntity<ApiResponse<T>>` dönmeli
- **Validation:** `@Valid @RequestBody` + DTO'da `@NotBlank`, `@Size` vb.
- **AI çağrıları AiService üzerinden** — controller'da direkt RestTemplate çağırma
- **DB erişimi DbService üzerinden** — controller'da direkt JdbcTemplate çağırma
- **Hata yönetimi:** `GlobalExceptionHandler` çoğunu yakalar; controller'larda özel try/catch sadece custom mesaj gerekirse
- **`.env` ve API anahtarları asla commit edilmez** — `.gitignore` zaten engelliyor
- **Mevcut kodu BOZMA:** Çalışan boilerplate var, "iyileştirme" amacıyla kırıma sokma

### Kodlama Tarzı
- **Backend:** Java 21 syntax (text blocks `"""..."""`, switch expressions, records OK)
- **Frontend:** React JSX (`.jsx` extension), CommonJS değil ESM (`type: "module"`)
- **Stil:** Backend için Spring conventions, frontend için Tailwind utility class'ları
- **API yanıtları:** Her zaman `ApiResponse<T>` formatında
- **Hata:** Backend `throw new RuntimeException(...)` veya kontrolcüde `ResponseEntity.badRequest()...`
- **AI çağrıları:** Her zaman try/catch ile sarılı, `AiResult.fallback()` döndürebilir

---

## 11. Sık Karşılaşılan Kullanıcı İstekleri

| İstek | Yapılacak |
|-------|-----------|
| "Yeni endpoint ekle" | `ApiController.java`'ya `@PostMapping`/`@GetMapping` metodu + DTO'lar (gerekirse) |
| "Yeni sayfa ekle" | `frontend/src/pages/`'e yeni .jsx, `App.jsx`'e `Route`, gerekirse `client.js`'e API call |
| "AI prompt'unu değiştir" | `AiService.SYSTEM_PROMPT` constant'ı + `AI_CONTEXT/3_ai_prompts.md` |
| "DB tablosu ekle" | `DbService.init()` içine `CREATE TABLE` + yeni CRUD metotları |
| "Dosya upload ekle" | `DocumentService` zaten var, controller'da `@RequestParam("file") MultipartFile` |
| "RAG yap" | `ai-specialist/RAG_KURULUM.md`'yi takip et — PDFBox + in-memory search + AiService |
| "Test yaz" | Spring Boot Test (`@SpringBootTest` veya `@WebMvcTest`), JUnit 5 |
| "Deploy et" | Backend: Railway/Render (JAR), Frontend: Vercel/Netlify (Vite build) |
| "Node.js'e geç" | Yapmayın — proje Java'ya commit'lendi, geri dönüş zaman kaybı |

---

## 12. Anlam Kazandırıcı Kavramlar

- **MVP:** Minimum Viable Product — 4 saatte bitirilecek en yalın ürün
- **Code Freeze (2:30):** O saatten sonra yeni özellik yok, sadece bug fix
- **Pipeline:** `.claude/agents/` zinciri (planner→coder→tester→reviewer), `/ship` komutu ile çalışır
- **Handoff dosyaları:** `.pipeline/spec.md`, `changes.md`, `test-results.md`, `review.md`
- **AI Çıktı Sözleşmesi:** AI'nın döneceği JSON şeması — Backend ve Frontend buna göre kodlar
- **Fallback yanıt:** `AiResult.fallback()` — AI API hata verdiğinde dönen statik DTO

---

## 13. Önemli Komutlar

```bash
# Backend başlat
cd backend
mvn spring-boot:run        # http://localhost:3001

# Maven dependency'leri önceden indir
mvn dependency:resolve

# Backend build (JAR oluştur)
mvn clean package
# → target/hackathon-backend-1.0.0.jar
java -jar target/hackathon-backend-1.0.0.jar

# Frontend başlat
cd frontend
npm install
npm run dev                # http://localhost:5173

# Health check
curl http://localhost:3001/health

# Ana endpoint test
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input": "test"}'

# Dosya upload test
curl -X POST http://localhost:3001/api/upload \
  -F "file=@belge.pdf"

# H2 Console
# http://localhost:3001/h2-console
# JDBC URL: jdbc:h2:mem:hackathon, user: sa, password: (boş)

# 4-agent pipeline (sadece Claude Code'da)
/ship add file upload endpoint that extracts text
```

### Environment Variable Set Etme
```bash
# Windows PowerShell
$env:ANTHROPIC_API_KEY="sk-ant-..."

# Windows cmd
set ANTHROPIC_API_KEY=sk-ant-...

# Linux/Mac
export ANTHROPIC_API_KEY=sk-ant-...
```

---

## 14. Eğer Kullanıcı Bunlardan Birini Sorarsa

- **"Plan nedir?"** → `mydocs/plan.md` ve `mydocs/4 agent.md` kullanıcının orijinal araştırması
- **"SKILLS.md ne işe yarar?"** → Her yeni AI chat'in başına yapıştırılır, kodlama bağlamını AI'ya verir
- **"AI_CONTEXT vs SKILLS farkı?"** → SKILLS = genel persona/kurallar; AI_CONTEXT = projeye özel mimari/şema
- **"Hangi modeli kullanıyoruz?"** → Anthropic Claude `claude-sonnet-4-6`
- **"Hangi backend?"** → Java 21 + Spring Boot 3.2.5 (kullanıcı kararı)
- **"Neden H2?"** → In-memory, sıfır config, hackathon hızı için ideal
- **"Kullanıcı kim?"** → 4 saatlik AI hackathon takım lideri. Java ve JavaScript biliyor. Cursor/VS Code/Claude Code kullanıyor olabilir.

---

## 15. Son Notlar

- Tüm rehberler **Türkçe yazılmış**, ama AI'ya yapıştırılan teknik prompt'lar (SYSTEM_PROMPT) genelde **İngilizce** (LLM tutarlılığı için)
- Kullanıcı işin kalitesini önemsiyor: gereksiz dosya yaratma, mevcut yapıyı koru, eklemen gereken yeri net belirle
- 4 saatlik hackathon kısıtı altında hız > mükemmellik. MVP-first yaklaşımı.

Bu brifingi okuduktan sonra projeyi anlamış olmalısın. Kullanıcının istediği değişikliği yapmaya hazırsın.
