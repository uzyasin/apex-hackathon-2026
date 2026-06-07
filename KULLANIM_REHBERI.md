# Hackathon Kullanım Rehberi

Bu rehber, hazırlanan boilerplate yapısını yarışma gününde nasıl kullanacağını adım adım anlatır.

---

## Yarışmadan Önce (Bu Gece)

### 1. API Anahtarını Hazırla

[console.anthropic.com](https://console.anthropic.com) adresine gir, API key oluştur ve not al.
Bakiyeni kontrol et — yarışmada kredi bitmemeli.

### 2. Environment Variable Set Et

Java backend `ANTHROPIC_API_KEY` system environment variable'ını okur:

```bash
# Windows PowerShell (kalıcı set etmek için Settings → Environment Variables)
$env:ANTHROPIC_API_KEY="sk-ant-..."

# Windows cmd
set ANTHROPIC_API_KEY=sk-ant-...

# Linux/Mac
export ANTHROPIC_API_KEY=sk-ant-...
# Kalıcı: ~/.bashrc veya ~/.zshrc'ye ekle
```

**Veya:** `backend/src/main/resources/application.yml` içindeki `${ANTHROPIC_API_KEY:set-via-env-var}` default'unu doğrudan anahtarınla değiştir (ama git'e commit etme!).

### 3. Bağımlılıkları Yükle (Şimdi Yükle, Yarışmada Zaman Kaybetme)

```bash
# Backend dependency'lerini önceden indir
cd backend
mvn dependency:resolve
mvn clean compile

# Frontend
cd ../frontend
npm install
```

### 4. Çalıştığını Test Et

```bash
# Terminal 1 — Backend
cd backend
mvn spring-boot:run
# → "Started HackathonApplication in X seconds" görmelisin
# → http://localhost:3001/health → {"success":true,"data":{"status":"ok"}}

# Terminal 2 — Frontend
cd frontend
npm run dev
# → "Local: http://localhost:5173" görmelisin
```

Tarayıcıda `http://localhost:5173` aç. Arayüz geliyorsa her şey hazır.

**AI test:**
```bash
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"test\"}"
# → success: true ile JSON gelmeli
```

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
Java Spring Boot

## Database Schema
### Table: analyses (zaten DbService.init()'te var, gerekirse ek tablolar)
- id: BIGINT AUTO_INCREMENT PRIMARY KEY
- input: CLOB NOT NULL
- context: CLOB
- result_json: CLOB
- score: INT
- created_at: TIMESTAMP

### Table: positions (yeni)
- id: BIGINT AUTO_INCREMENT PRIMARY KEY
- title: VARCHAR(255)
- requirements: CLOB
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
  "summary": "string — 1 cümle özet",
  "insights": ["string", "string"],
  "score": number 0-100,
  "recommendation": "string — Mülakata Çağır veya Reddet"
}

No markdown, no extra text. Only the JSON object.
```

Bu prompt'u `backend/src/main/java/com/hackathon/service/AiService.java` dosyasında `SYSTEM_PROMPT` constant'ına yapıştır (text block `"""..."""` formatında).

---

### Adım 4: Rol Dağılımı (5 dk)

| Kişi | Görev |
|------|-------|
| **AI Uzmanı** | `AiService.java` içindeki system prompt'u mükemmelleştir, test et, geliştir |
| **Backend** | Yeni endpoint'ler ekle (`ApiController.java`), DB tabloları (`DbService.java`) |
| **Frontend** | `HomePage.jsx`'i ürüne göre düzenle, yeni sayfalar/component'ler ekle |
| **Full-Stack Destek** | Git yönetimi, birleştirme, deploy, eksik yerlere destek |

---

## Kodlama Aşaması — 3 Farklı Yöntem

### Yöntem A: /ship Komutu (En Güçlü — Claude Code Kullanıyorsan)

Bu komut **Planner → Coder → Tester → Reviewer** zincirini otomatik çalıştırır.

Claude Code terminaline yaz:
```
/ship CV metni alan POST endpoint'i ekle, DbService ile analyses tablosuna kaydet
```

Ne olur:
1. **Planner** (Opus): Kodu yazmadan önce spec.md oluşturur — hangi dosya, hangi metod
2. **Coder** (Sonnet): Spec'i okur, Java kodunu yazar
3. **Tester** (Sonnet): Test yazar ve `mvn test` ile çalıştırır
4. **Reviewer** (Opus): SHIP / NEEDS WORK / BLOCK kararı verir

Pipeline OPEN QUESTION veya test hatası görürse sana sorar, devam etmez.
Her feature için 5-10 dakika alır ama kaliteli kod çıkar.

---

### Yöntem B: Cursor / VS Code Copilot ile Çalışma

Chat panelinde `@` ile dosyaları referans ver:

```
@AI_CONTEXT/1_system_rules.md ve @AI_CONTEXT/2_architecture.md dosyalarına uygun olarak
backend/src/main/java/com/hackathon/controller/ApiController.java içine
POST /api/cv-analyze endpoint'i ekle.
Request: { cvText: string, position: string }
Response: DbService ile kaydet, AiService.analyze ile AI sonucunu döndür.
```

Planlama modu için önce şunu yaz:
```
Henüz kod yazma. @AI_CONTEXT/2_architecture.md şemasına bakarak bu feature'ı
hangi adımlarda yapacağını listele. Hangi sınıfları değiştireceksin?
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
Şimdi backend/src/main/java/com/hackathon/controller/ApiController.java dosyasına
POST /api/cv-analyze endpoint'i ekle. architecture.md'deki analyses tablosuna
DbService ile kaydet.
```

---

## Kodlama Sırasında Dikkat Edilecekler

### Herkes Git'e Düzenli Push Yapsın

```bash
git add .
git commit -m "feat(be): cv analyze endpoint"
git push
```

Merge çakışması yaşamamak için:
- Backend kişisi sadece `backend/` dokunur
- Frontend kişisi sadece `frontend/` dokunur
- AI uzmanı sadece `AiService.java`, `3_ai_prompts.md` ve `ai-specialist/` klasörüne dokunur

### AI Prompt'unu Iteratif Geliştir

Test ederken AI saçma cevap veriyorsa:
1. `3_ai_prompts.md` dosyasını aç, prompt'u düzelt
2. `AiService.java`'daki `SYSTEM_PROMPT`'u güncelle
3. Backend'i yeniden başlat (`Ctrl+C` → `mvn spring-boot:run`)
4. Endpoint'i tekrar test et

Bunu 3-4 kez döngü yaparak prompt'u mükemmelleştir.

**Hot reload için:** Spring Boot DevTools eklemek istersen pom.xml'e ekle ve `spring-boot:run` otomatik restart yapar.

### Hata Yönetimi Zaten Hazır

Backend'de `GlobalExceptionHandler` tüm exception'ları yakalıyor, `AiService.callClaude()` fallback yanıt döndürüyor.
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
  -d "{\"input\": \"test girdisi\"}"

# Results endpoint?
curl http://localhost:3001/api/results

# Upload?
curl -X POST http://localhost:3001/api/upload -F "file=@test.pdf"
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
1. ANTHROPIC_API_KEY env variable set et
2. `cd backend && mvn spring-boot:run`
3. `cd frontend && npm install && npm run dev`
4. Tarayıcıda http://localhost:5173 aç
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
| `AI_CONTEXT/1_system_rules.md` | Herkes | Kodlama standartları (Java + React) | Cursor/Copilot'a @ile referans ver |
| `AI_CONTEXT/2_architecture.md` | Herkes | DB şeması, endpoint'ler | İlk 15 dakikada doldur, sürekli güncelle |
| `AI_CONTEXT/3_ai_prompts.md` | AI Uzmanı | Claude system prompt şablonları | AI özelliği geliştirirken düzenle |
| `ai-specialist/PROMPT_GELISTIRME.md` | AI Uzmanı | Prompt iterasyon defteri | Prompt yazarken, her versiyonu buraya not al |
| `ai-specialist/AI_CIKTI_SOZLESMESI.md` | AI Uzmanı | Backend↔Frontend veri anlaşması | İlk 20 dk içinde doldur, ekiple paylaş |
| `ai-specialist/RAG_KURULUM.md` | AI Uzmanı | PDF/belge bağlama (Java) rehberi | Yarışmada belge verilirse aç ve uygula |
| `backend/.../service/AiService.java` | AI Uzmanı | Anthropic API entegrasyonu | `SYSTEM_PROMPT` buraya kopyalanır |
| `backend/.../controller/ApiController.java` | Backend | HTTP endpoint'leri | Yeni route'lar buraya eklenir |
| `backend/.../service/DbService.java` | Backend | H2 + JdbcTemplate CRUD | Yeni tablo/sorgu buraya eklenir |
| `frontend/src/api/client.js` | Frontend | Backend API çağrıları | Yeni axios fonksiyonları buraya |
| `frontend/src/pages/HomePage.jsx` | Frontend | Ana sayfa | Ürüne göre tamamen değiştirilebilir |
| `.claude/commands/ship.md` | Herkes | /ship pipeline komutu | Claude Code'da /ship ile tetiklenir |

---

## Sık Karşılaşılan Sorunlar

**"set-via-env-var" yanıt geliyor**
→ `ANTHROPIC_API_KEY` env variable set edilmedi. Yukarıdaki "Environment Variable Set Et" bölümüne bak. Set ettikten sonra terminal'i yeniden aç ve `mvn spring-boot:run`'u tekrar başlat.

**Frontend API'ye ulaşamıyor (CORS hatası)**
→ Backend'in çalıştığından emin ol (port 3001). `frontend/vite.config.js`'deki proxy 3001'e yönlendiriyor. `CorsConfig.java`'da `http://localhost:5173` izinli.

**AI JSON dışında bir şey dönüyor**
→ `AiService.SYSTEM_PROMPT` constant'ına "Respond ONLY with valid JSON, no markdown" ekle. Backend'i restart et.

**Maven dependency indiremiyor**
→ İnternet bağlantısını kontrol et. `~/.m2/repository` silmeyi dene (problem dosyaları). `mvn dependency:resolve -U` ile force update.

**`mvn` komutu bulunamadı**
→ Maven kurulu mu? `mvn -version` ile kontrol et. Değilse [maven.apache.org](https://maven.apache.org/download.cgi) → kur → PATH'e ekle.

**Java sürümü uyumsuz**
→ Java 21 gerekli. `java -version` kontrol et. [adoptium.net](https://adoptium.net) → Temurin 21 LTS indir.

**Git push reddedildi**
→ `git pull --rebase origin main` çalıştır, çakışmaları çöz, tekrar push et.

**H2 DB'deki veriyi görmek istiyorum**
→ Browser'da `http://localhost:3001/h2-console` aç. JDBC URL: `jdbc:h2:mem:hackathon`, user: `sa`, password boş.

**Port 3001 zaten kullanılıyor**
→ Windows: `netstat -ano | findstr :3001` → `taskkill /PID <PID> /F`
→ Linux/Mac: `lsof -i :3001` → `kill -9 <PID>`
→ Veya `application.yml`'de `server.port: 3002` yap, frontend `vite.config.js` proxy'sini de güncelle.
