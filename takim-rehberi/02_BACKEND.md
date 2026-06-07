# ⚙️ Backend Geliştirici — Detaylı Rehber

> Bu rehber **Backend Geliştirici (Java Spring Boot)** içindir.
> Diğer roller: [AI Uzmanı](01_AI_UZMANI.md) · [Frontend](03_FRONTEND.md) · [Git Yöneticisi](04_GIT_YONETICISI.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **motor odası**sın. Görevin:
- Spring Boot REST endpoint'lerini yazmak (`ApiController.java`)
- H2 in-memory DB tablolarını ve CRUD'u kurmak (`DbService.java`)
- AI Uzmanı'nın `AiService`'ini çağırıp veriyi DB'ye yazmak
- API'nin **çökmediğinden** ve **doğru JSON döndürdüğünden** emin olmak
- Dosya upload, query parametreleri, validation gibi standart işleri halletmek

Kısaca: Frontend "şu endpoint'i çağıracağım" diyecek, sen onun beklediği yanıtın geldiğinden emin olacaksın. AI Uzmanı'nın çıktısını DB'ye yazıp frontend'e iletmek senin işin.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + Java Spring Boot backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Java 21 + Spring Boot 3.2.5 + Maven
- **Frontend:** React 18 + Vite + Tailwind CSS
- **AI:** Anthropic Claude — `claude-sonnet-4-6` (AI Uzmanı yönetir)
- **DB:** H2 in-memory + JdbcTemplate (sıfır config)
- **HTTP Client:** `RestTemplate` (Anthropic API'ye)
- **PDF parsing:** Apache PDFBox 3.x
- **Validation:** jakarta.validation
- **Lombok:** DTO boilerplate
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
- **0:20:** `AI_CONTEXT/2_architecture.md`'ye DB şeması ve endpoint listesi yazılmış olmalı
- **0:45:** Yeni DB tablosu (gerekirse) hazır, ilk endpoint çalışıyor
- **1:30:** Ana endpoint (POST /api/analyze) tam çalışıyor, frontend bağlanabilir
- **2:30:** Tüm endpoint'ler hazır, code freeze
- **3:00:** Yeni endpoint yok, sadece bug fix

---

## 3. ORTAK: Tüm Roller

| Rol | Sorumluluk | Dokunduğu |
|-----|-----------|-----------|
| **AI Uzmanı** | Prompt, AiService, çıktı sözleşmesi | `ai-specialist/`, `AiService.java`, `AiResult.java` |
| **Backend (sen)** | Endpoint'ler, DB, file upload | `ApiController.java`, `DbService.java`, `DocumentService.java`, yeni DTO'lar |
| **Frontend** | UI | `frontend/src/` |
| **FS Yöneticisi** | Git, merge, koordinasyon | Her yer (dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar

- [ ] Boilerplate'i clone'la
- [ ] Java 21 kurulu mu? `java -version` ile kontrol
  - Yoksa: [adoptium.net](https://adoptium.net) → Temurin 21 LTS
- [ ] Maven kurulu mu? `mvn -version`
  - Yoksa: [maven.apache.org](https://maven.apache.org/download.cgi)
- [ ] `ANTHROPIC_API_KEY` env variable set et (FS Yöneticisi paylaşacak)
  - Windows PowerShell: `$env:ANTHROPIC_API_KEY="sk-ant-..."`
  - Linux/Mac: `export ANTHROPIC_API_KEY=sk-ant-...`
- [ ] `cd backend && mvn dependency:resolve` — Bağımlılıkları önceden indir (~3-5 dk ilk seferde)
- [ ] `mvn spring-boot:run` — Başlatabildiğini doğrula
- [ ] `curl http://localhost:3001/health` → `{"success":true,"data":{"status":"ok"}}` gelmeli
- [ ] `curl -X POST localhost:3001/api/analyze -d "{\"input\":\"test\"}" -H "Content-Type: application/json"` — AI yanıt veriyor mu
- [ ] H2 Console'u tanı: http://localhost:3001/h2-console (JDBC URL: `jdbc:h2:mem:hackathon`)
- [ ] IDE: IntelliJ Community veya VS Code + Extension Pack for Java
- [ ] Spring Boot ve JdbcTemplate hakkında biraz bilgi tazele

**Sorun yaşarsan:** Yarışma günü değil, bu gece çöz.

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika

```bash
# Konu açıklandıktan sonra:
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout feat/backend

# API key set et
$env:ANTHROPIC_API_KEY="sk-ant-..."   # PowerShell
# veya: export ANTHROPIC_API_KEY=sk-ant-...

# Backend'i ayağa kaldır
cd backend
mvn spring-boot:run   # dependency'leri zaten dün indirdin
# → "Started HackathonApplication" mesajı görmelisin
```

Backend ayakta. Şimdi AI Uzmanı'nın sözleşmesini bekle (10 dk içinde gelecek).

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **Mimari Karar:** `AI_CONTEXT/2_architecture.md`'ye DB tabloları ve endpoint listesi yaz
2. **Yeni DB Tabloları:** `DbService.init()` içine `CREATE TABLE` ekle, CRUD metotları yaz
3. **Ana Endpoint:** `POST /api/analyze` zaten var — DB'ye kaydetmesini sağlandı
4. **Yardımcı Endpoint'ler:** Geçmiş kayıtlar (`/api/results`), tek kayıt, dosya upload — çoğu zaten hazır
5. **Hata Yönetimi:** `GlobalExceptionHandler` çoğu durumu yakalar; özel hata mesajları için controller'da `ResponseEntity.badRequest()`
6. **Validation:** DTO'lara `@NotBlank`, `@Size(max=...)`, `@Email` vb. ekle

### Yan Görevler
- AI Uzmanı'nın `AiService`'ini doğru çağırdığından emin ol
- Frontend'in beklediği response formatını sözleşmeye uygun tut (`ApiResponse<T>` wrapper)
- Postman/curl ile her endpoint'i kendin test et
- H2 Console'da DB durumunu kontrol et

---

## 7. SENIN: Dokunduğun Dosyalar

```
backend/src/main/java/com/hackathon/
├── controller/ApiController.java       ← Yeni endpoint'ler buraya
├── service/
│   ├── DbService.java                  ← Yeni tablo ve CRUD metotları
│   ├── DocumentService.java            ← Dosya işleme (zaten hazır, ekleyebilirsin)
│   └── (yeni servisler — örn: NotificationService.java)
├── dto/                                ← Yeni request/response DTO'ları
│   ├── ApiResponse.java                ← Generic wrapper (zaten hazır, değiştirme)
│   ├── AnalyzeRequest.java             ← Şema değişirse güncelle
│   └── (yeni DTO'lar)
└── config/                             ← Yeni @Configuration sınıfları (gerekirse)

backend/src/main/resources/application.yml   ← DB veya başka config değişiklikleri
backend/pom.xml                              ← Yeni dependency eklemek gerekirse
AI_CONTEXT/2_architecture.md                 ← DB şeması + endpoint listesi (DOLDUR)
```

**Dokunmayacağın yerler:**
- `service/AiService.java` (AI Uzmanı'nın — sen sadece inject edip çağırırsın)
- `dto/AiResult.java` (AI Uzmanı'nın — şema değişikliklerini O yapar)
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
Java Spring Boot

## Database Schema (H2 in-memory)

### Table: analyses (zaten DbService.init()'te var)
- id: BIGINT AUTO_INCREMENT PRIMARY KEY
- input: CLOB NOT NULL
- context: CLOB
- result_json: CLOB
- score: INT
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Table: positions (YENİ — DbService.init()'e ekle)
- id: BIGINT AUTO_INCREMENT PRIMARY KEY
- title: VARCHAR(255) NOT NULL
- requirements: CLOB
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

## API Endpoints

| Method | Path | Body | Response.data |
|--------|------|------|---------------|
| GET | /api/health | — | {status} |
| POST | /api/analyze | {input, context?} | {id, summary, insights, score, recommendation} |
| GET | /api/results | — | [{id, input, score, created_at}] |
| GET | /api/results/{id} | — | full row |
| POST | /api/upload | multipart file | {filename, size, text} |
| GET | /api/positions | — | [{id, title}] (YENİ) |
| POST | /api/positions | {title, requirements} | {id} (YENİ) |
```

**Bildirim:** Frontend ve FS'ye yaz:
> "Backend mimarisi hazır. Endpoint'ler `architecture.md`'de. /api/analyze 10 dk içinde DB'ye kayıt yapar halde olacak."

**Commit:**
```bash
git add AI_CONTEXT/2_architecture.md
git commit -m "feat(be): architecture - endpoint ve DB şeması"
git push
```

---

### 🟢 [0:20 – 0:45] DB Tabloları ve Yeni Endpoint'ler

**Hedef:** Yeni tablolar oluştu, CRUD metotları hazır.

`backend/src/main/java/com/hackathon/service/DbService.java`'yı aç. `init()` metoduna yeni tabloyu ekle:

```java
@PostConstruct
public void init() {
    jdbc.execute("""
        CREATE TABLE IF NOT EXISTS analyses (
          id BIGINT AUTO_INCREMENT PRIMARY KEY,
          input CLOB NOT NULL,
          context CLOB,
          result_json CLOB,
          score INT,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """);

    // YENİ TABLO
    jdbc.execute("""
        CREATE TABLE IF NOT EXISTS positions (
          id BIGINT AUTO_INCREMENT PRIMARY KEY,
          title VARCHAR(255) NOT NULL,
          requirements CLOB,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """);

    System.out.println("[DbService] tablolar hazır");
}
```

**Yeni tablo için CRUD metotları:**

```java
// DbService.java içine ekle

public Long savePosition(String title, String requirements) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbc.update(conn -> {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO positions (title, requirements) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );
        ps.setString(1, title);
        ps.setString(2, requirements);
        return ps;
    }, keyHolder);
    return keyHolder.getKey().longValue();
}

public List<Map<String, Object>> getAllPositions() {
    return jdbc.queryForList(
            "SELECT id, title, created_at FROM positions ORDER BY created_at DESC"
    );
}

public Map<String, Object> getPositionById(Long id) {
    List<Map<String, Object>> rows = jdbc.queryForList(
            "SELECT * FROM positions WHERE id = ?", id
    );
    return rows.isEmpty() ? null : rows.get(0);
}
```

**Test (H2 Console):**
```
http://localhost:3001/h2-console
JDBC URL: jdbc:h2:mem:hackathon
User: sa, Password: (boş)

SQL'i çalıştır:
SELECT * FROM positions;
```

**Commit:**
```bash
git add backend/
git commit -m "feat(be): positions tablosu + CRUD metotları"
git push
```

---

### 🟢 [0:45 – 1:30] Yeni Endpoint'ler

**Hedef:** Frontend'in çağırabileceği yeni endpoint'ler hazır.

`backend/src/main/java/com/hackathon/controller/ApiController.java`'yı aç. Önce yeni request DTO oluştur:

`backend/src/main/java/com/hackathon/dto/PositionRequest.java`:
```java
package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PositionRequest {
    @NotBlank(message = "title gerekli")
    @Size(max = 255, message = "title en fazla 255 karakter")
    private String title;

    private String requirements;
}
```

Sonra controller'a endpoint ekle:

```java
// ApiController.java içine ekle

@PostMapping("/positions")
public ResponseEntity<ApiResponse<Map<String, Object>>> createPosition(
        @Valid @RequestBody PositionRequest req
) {
    Long id = dbService.savePosition(req.getTitle(), req.getRequirements());
    return ResponseEntity.ok(ApiResponse.ok(Map.of("id", id, "title", req.getTitle())));
}

@GetMapping("/positions")
public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllPositions() {
    return ResponseEntity.ok(ApiResponse.ok(dbService.getAllPositions()));
}

@GetMapping("/positions/{id}")
public ResponseEntity<ApiResponse<Map<String, Object>>> getPosition(@PathVariable Long id) {
    Map<String, Object> position = dbService.getPositionById(id);
    if (position == null) {
        return ResponseEntity.status(404).body(ApiResponse.fail("Position bulunamadı"));
    }
    return ResponseEntity.ok(ApiResponse.ok(position));
}
```

**Test her endpoint için (Spring Boot DevTools yoksa restart gerekir):**

```bash
# POST /api/positions
curl -X POST http://localhost:3001/api/positions \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Senior Java Developer\",\"requirements\":\"Java 21, Spring Boot\"}"
# → { success: true, data: { id: 1, title: "..." } }

# GET /api/positions
curl http://localhost:3001/api/positions
# → { success: true, data: [{ id: 1, title: "...", created_at: "..." }] }

# GET /api/positions/1
curl http://localhost:3001/api/positions/1
# → { success: true, data: { id: 1, title: "...", requirements: "..." } }

# Validation testi (boş title)
curl -X POST http://localhost:3001/api/positions \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"\"}"
# → 400, { success: false, error: "title gerekli" }
```

**Bildirim:** Frontend'e söyle:
> "POST /api/positions ve GET /api/positions hazır. Detaylar `architecture.md`'de. Bağlayabilirsin."

**Commit:**
```bash
git add backend/
git commit -m "feat(be): /api/positions CRUD endpoint'leri"
git push
```

---

### 🟢 [1:30 – 2:30] Ek Özellikler (Gerekirse)

**Hedef:** Frontend'in ihtiyaç duyabileceği tüm endpoint'ler hazır.

#### Dosya Upload (zaten var, gerekirse genişlet)

`/api/upload` endpoint'i `DocumentService` ile çalışıyor. PDF, TXT, MD kabul ediyor.
Eğer Word veya başka format gerekirse `DocumentService.extractText()` metodunu genişlet:

```java
// DocumentService.java
public String extractText(MultipartFile file) throws IOException {
    String name = file.getOriginalFilename();
    String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();

    if ("pdf".equals(ext)) {
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(doc);
        }
    }
    if ("txt".equals(ext) || "md".equals(ext)) {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }
    if ("docx".equals(ext)) {
        // Apache POI dependency ekleyip kullan
        // <dependency><groupId>org.apache.poi</groupId>...</dependency>
    }
    throw new IOException("Desteklenmeyen dosya tipi: ." + ext);
}
```

#### İstatistikler Endpoint'i (opsiyonel ama jüriye iyi görünür)

```java
// ApiController.java
@GetMapping("/stats")
public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
    Map<String, Object> stats = jdbcTemplate.queryForMap("""
        SELECT
          COUNT(*) AS total,
          AVG(score) AS avg_score,
          MAX(score) AS max_score
        FROM analyses
    """);
    return ResponseEntity.ok(ApiResponse.ok(stats));
}
```

Not: `JdbcTemplate jdbcTemplate`'i ya `DbService`'e yeni metot olarak ekle, ya da controller'a direkt inject et (genelde service'te tutmak daha temiz).

#### Sıralama / Filtreleme

```java
@GetMapping("/results")
public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
        @RequestParam(value = "minScore", defaultValue = "0") int minScore,
        @RequestParam(value = "sort", defaultValue = "created_at") String sort
) {
    // SQL injection'a karşı: sort parametresini whitelist'le
    String safeSort = List.of("created_at", "score").contains(sort) ? sort : "created_at";
    List<Map<String, Object>> rows = dbService.getAllAnalysesFiltered(minScore, safeSort);
    return ResponseEntity.ok(ApiResponse.ok(rows));
}
```

Eşleşen `DbService.getAllAnalysesFiltered`:
```java
public List<Map<String, Object>> getAllAnalysesFiltered(int minScore, String sortColumn) {
    String sql = "SELECT id, input, score, created_at FROM analyses WHERE score >= ? ORDER BY " + sortColumn + " DESC LIMIT 100";
    return jdbc.queryForList(sql, minScore);
}
```

**Her büyük değişiklikten sonra commit:**
```bash
git add backend/
git commit -m "feat(be): stats ve filtreleme endpoint'leri"
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
  -d "{\"input\": \"normal metin\"}"

# 2. Boş body
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{}"
# → 400 hatası "input gereklidir"

# 3. Yanlış tip (number)
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": 123}"
# → 400 hatası (Jackson type mismatch)

# 4. Çok uzun metin (10001 karakter)
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\":\"$(printf 'a%.0s' {1..10001})\"}"
# → 400 hatası "input en fazla 10000 karakter olabilir"

# 5. Olmayan endpoint
curl localhost:3001/api/foo
# → 404 (Spring otomatik)

# 6. Geçersiz JSON
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "not json"
# → 400 (Jackson parse error)

# 7. File upload — yanlış tip
echo "test" > test.xyz
curl -X POST localhost:3001/api/upload -F "file=@test.xyz"
# → 400 "Desteklenmeyen dosya tipi"
```

#### H2 Console İncelemesi

http://localhost:3001/h2-console — JDBC URL: `jdbc:h2:mem:hackathon`, user: `sa`, password boş.

```sql
SELECT COUNT(*) FROM analyses;
SELECT * FROM analyses ORDER BY score DESC LIMIT 10;
SELECT AVG(score) FROM analyses;
```

#### Final Commit

```bash
git add backend/
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
cd backend && mvn spring-boot:run
```

#### Uçtan uca test:
1. Frontend'i aç (Frontend'ci başlatmış olmalı)
2. Form gönder → backend log'da çağrı görmeli misin
3. H2 Console'da yeni satırı gör

#### Sunum Notları (1 dakika)

> "Backend tarafında Java 21 + Spring Boot 3 kullandık.
> Anthropic API'sini direkt çağırmak yerine `AiService` adında bir soyutlama katmanı oluşturduk — RestTemplate ile HTTP POST yapıyor, dilersek başka modele kolayca geçebiliriz.
> Her endpoint'te validation (jakarta.validation) ve global exception handler var.
> Veriyi H2 in-memory DB'ye `JdbcTemplate` ile yazıyoruz — sıfır konfigürasyon, hızlı geliştirme.
> PDF analizi için Apache PDFBox 3.x entegre, kullanıcı belge yükleyebiliyor.
> Hata durumunda `AiResult.fallback()` yanıt dönüyor — kullanıcı boş bir ekran görmüyor."

---

## 9. SENIN: Sık Karşılaştığın Hatalar

### Hata 1: `mvn spring-boot:run` çalışmıyor — "JAVA_HOME not set"

**Çözüm:**
```bash
# Java'nın yolunu bul
where java        # Windows
which java        # Linux/Mac

# JAVA_HOME set et (Windows PowerShell, kalıcı için sistem ayarları)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21..."
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

Yeniden terminal aç, tekrar dene.

---

### Hata 2: H2 "database is locked" veya tablolar bulunamıyor

**Sebep:** H2 in-memory zaten her restart'ta sıfırlanır. Sorun varsa muhtemelen `@PostConstruct` çalışmıyor.

**Çözüm:**
- `DbService.java`'da `@PostConstruct` import edildi mi? (`jakarta.annotation.PostConstruct`)
- `@Service` annotation'ı var mı? (Spring bean olarak yüklenmesi için)
- Log'larda "[DbService] tablolar hazır" mesajı var mı?

---

### Hata 3: CORS hatası (Frontend bağlanamıyor)

**Belirti:** Frontend console'da:
```
Access to XMLHttpRequest at 'http://localhost:3001/api/...' from origin 'http://localhost:5173' blocked by CORS policy
```

**Çözüm:** `CorsConfig.java`'da origin doğru mu?
```java
.allowedOrigins("http://localhost:5173")
```

Frontend farklı port'taysa burayı güncelle.

Geçici hızlı çözüm (test için):
```java
.allowedOriginPatterns("*")
.allowedMethods("*")
```

---

### Hata 4: `@Valid` çalışmıyor (validation atlanıyor)

**Çözüm:**
- `pom.xml`'de `spring-boot-starter-validation` var mı? (var)
- DTO'da annotation doğru mu? `@NotBlank(message = "...")` ve field `private String x`
- Controller'da `@Valid @RequestBody` her ikisi de var mı?
- `GlobalExceptionHandler.handleValidation` set edilmiş mi? (var)

---

### Hata 5: AI çağrısı 401 Unauthorized

**Sebep:** `ANTHROPIC_API_KEY` env variable set değil veya yanlış.

**Çözüm:**
```bash
# Terminal'i kapat, yeniden aç
echo $ANTHROPIC_API_KEY      # Linux/Mac
echo $env:ANTHROPIC_API_KEY  # PowerShell

# Boş veya yanlışsa set et
export ANTHROPIC_API_KEY=sk-ant-...
$env:ANTHROPIC_API_KEY="sk-ant-..."

# Backend'i restart et (aynı terminal'de)
cd backend && mvn spring-boot:run
```

Veya geçici olarak `application.yml`'de default'u değiştir:
```yaml
anthropic:
  api-key: ${ANTHROPIC_API_KEY:sk-ant-gerçek-key-buraya}
```
**ASLA git'e commit etme!**

---

### Hata 6: Port 3001 zaten kullanılıyor

**Çözüm:**
```bash
# Windows:
netstat -ano | findstr :3001
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :3001
kill -9 <PID>
```

Veya `application.yml`'de farklı port:
```yaml
server:
  port: 3002
```

Bu durumda frontend `vite.config.js`'i de güncelle:
```javascript
proxy: { '/api': { target: 'http://localhost:3002' } }
```

---

### Hata 7: PDFBox NoClassDefFoundError veya OutOfMemoryError

**Çözüm:**
- `pom.xml`'de `pdfbox 3.0.1` var mı?
- `mvn clean install` ile dependency'leri yeniden indir
- Büyük PDF'lerde memory artışı için `application.yml`'e:
  ```yaml
  spring:
    servlet:
      multipart:
        max-file-size: 5MB
        max-request-size: 5MB
  ```

---

### Hata 8: AiService değişikliği reload olmuyor

**Çözüm:** Spring Boot DevTools eklenmediyse her değişiklik için backend'i restart et:
```bash
Ctrl+C
mvn spring-boot:run
```

Otomatik restart için `pom.xml`'e:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```
IDE'de "Build project automatically" açık olmalı.

---

## 10. SENIN: Demo Hazırlığı

### Sunumda göstereceğin 2 şey:

1. **Mimari (sözlü)**
   - "Spring Boot REST API → AiService (RestTemplate, Anthropic) → H2 in-memory DB"
   - "Her endpoint'te validation, GlobalExceptionHandler, fallback yanıt"

2. **Canlı Endpoint Testi (opsiyonel ama etkileyici)**
   - H2 Console'u ekranda aç, DB'deki kayıtları göster
   - "Bakın, /api/results endpoint'i geçmiş analizleri SQL ile çekiyor"

### Soru-cevap için hazırlık:
- **"Neden H2?"** → 4 saatte sıfır config, restart'ta temiz state. Prod için PostgreSQL'e geçmek `application.yml` değişikliğiyle 10 dakika sürer.
- **"Auth yok mu?"** → MVP scope dışında, Spring Security + JWT eklemek 30 dk sürer.
- **"Ölçeklendirme?"** → Stateless, horizontal scale OK. DB için PostgreSQL ve Redis cache.
- **"Neden Spring Boot?"** → Java ekosistemi olgun, Anthropic API HTTP olduğu için ekstra SDK gerektirmiyor, JdbcTemplate ile JPA boilerplate'ten kaçtık.
- **"JPA kullanmadınız mı?"** → 4 saatte JPA entity setup'ı vakit alır. JdbcTemplate daha düşük seviyeli ve hızlı.

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
git add backend/
git commit -m "feat(be): [ne değişti]"
git push

# main'den güncelleme al (FS merge yaptıktan sonra)
git checkout feat/backend
git merge main
```

### Commit Mesaj Formatı
```
feat(be): POST /api/positions endpoint
feat(be): H2 positions tablosu kuruldu
fix(be): boş input 400 dönmüyordu
docs(be): architecture.md güncellendi
```

### Yasak Komutlar
- `git push --force` — asla
- `git reset --hard` — asla
- `main` branch'e direkt commit — sadece FS

---

## 12. ORTAK: Takım İletişim Protokolü

### Senin Bildirmen Gerekenler

| Olay | Kime | Mesaj Örneği |
|------|------|--------------|
| Mimari hazır | Tüm ekip | "DB şeması ve endpoint listesi `architecture.md`'de" |
| /api/analyze çalışıyor | Frontend, FS | "POST /api/analyze hazır, DB'ye kaydediyor, response: `{success, data}`" |
| Yeni endpoint | Frontend | "POST /api/positions eklendi, body: `{title, requirements}`" |
| DB şeması değişti | AI Uzmanı, FS | "positions tablosu eklendi" |
| Tıkandım | Tüm ekip | "Multer file upload'da hata var, 15 dk içinde çözeceğim" |

### Sana Bildirilenler
- AI Uzmanı: "AiResult DTO'sunda yeni alan eklendi, controller'da ekrana basıyor musun?"
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
- `GlobalExceptionHandler` aktif mi? (`@RestControllerAdvice` annotation)
- Console log'larına bak (`mvn spring-boot:run` çıktısı)
- Bean ya da dependency injection sorunu olabilir — Stack trace'i oku

### Senaryo 2: H2 verisi kayboldu
**Sebep:** Backend restart edildi (in-memory, restart'ta temizlenir)
**Çözüm:** Demo verileri yeniden ekle. Eğer kalıcılık şart ise:
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:file:./data/hackathon  # file:./ ile dosyaya yaz
```

### Senaryo 3: AI çağrısı timeout
**Çözüm:**
- AI Uzmanı'na söyle: prompt'u kısalt, `max_tokens`'ı düşür
- `AiService`'te `restTemplate` timeout ayarla:
  ```java
  // AiService constructor'ında veya @Bean ile:
  HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
  factory.setConnectTimeout(5000);
  factory.setReadTimeout(15000);
  restTemplate = new RestTemplate(factory);
  ```
- Fallback yanıt zaten aktif (AiService.callClaude'de)

### Senaryo 4: Frontend bağlanamıyor (CORS)
**Çözüm:** Yukarıda "Hata 3" çözümüne bak. Hızlı: `.allowedOriginPatterns("*")`.

### Senaryo 5: AI Uzmanı'nın AiService'i bozuldu
**Çözüm:**
- `AiService.java`'nın son çalışan versiyonuna git'ten geri dön:
  ```bash
  git log --oneline backend/src/main/java/com/hackathon/service/AiService.java
  git checkout <hash> -- backend/src/main/java/com/hackathon/service/AiService.java
  ```
- AI Uzmanı ile birlikte fix yap

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce:

- [ ] `feat/backend` branch'in `main`'e merge edilmiş
- [ ] `main` üzerinde tüm endpoint'ler çalışıyor
- [ ] `mvn spring-boot:run` sorunsuz başlatıyor
- [ ] H2 Console erişilebilir (`/h2-console`)
- [ ] `ANTHROPIC_API_KEY` git'e commit edilmemiş (env variable kullanılıyor)
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
```java
@PostMapping("/path")
public ResponseEntity<ApiResponse<YourDto>> yourEndpoint(@Valid @RequestBody YourRequest req) {
    // Validation @Valid ile yapıldı, ek kontrol gerekirse:
    if (someCondition) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("anlamlı mesaj"));
    }

    // İş mantığı
    YourDto result = someService.doSomething(req.getX());

    // DB
    dbService.save(...);

    // Yanıt
    return ResponseEntity.ok(ApiResponse.ok(result));
}
```

### JdbcTemplate hızlı sorgular
```java
// SELECT — multiple rows
List<Map<String, Object>> rows = jdbc.queryForList("SELECT * FROM t WHERE x = ?", value);

// SELECT — single row (Map)
Map<String, Object> row = jdbc.queryForMap("SELECT * FROM t WHERE id = ?", id);
// Yoksa EmptyResultDataAccessException atar → try/catch ile null dön

// SELECT — single value
String name = jdbc.queryForObject("SELECT name FROM t WHERE id = ?", String.class, id);

// INSERT with generated key
KeyHolder kh = new GeneratedKeyHolder();
jdbc.update(conn -> {
    PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO t (a,b) VALUES (?,?)",
        Statement.RETURN_GENERATED_KEYS
    );
    ps.setString(1, "x");
    ps.setString(2, "y");
    return ps;
}, kh);
Long newId = kh.getKey().longValue();

// UPDATE / DELETE
int affected = jdbc.update("UPDATE t SET x = ? WHERE id = ?", newVal, id);
jdbc.update("DELETE FROM t WHERE id = ?", id);
```

### DTO şablonu (Lombok)
```java
package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class YourRequest {
    @NotBlank(message = "alan gerekli")
    @Size(max = 255)
    private String yourField;
}
```

### Maven komutları
```bash
mvn dependency:resolve    # bağımlılıkları indir
mvn clean compile         # temizle + derle
mvn spring-boot:run       # çalıştır
mvn clean package         # JAR oluştur (target/...jar)
java -jar target/hackathon-backend-1.0.0.jar   # JAR'ı direkt çalıştır
mvn test                  # testleri çalıştır
```

### H2 Console
```
URL:      http://localhost:3001/h2-console
JDBC URL: jdbc:h2:mem:hackathon
User:     sa
Password: (boş)
```

### `/ship` komutu (Claude Code'da)
```
/ship ApiController.java'ya POST /api/feedback endpoint'i ekle, DbService'e feedback tablosu kur
```
4 agent (planner→coder→tester→reviewer) otomatik çalışır.

---

**Başarılar! Motor odası senin. ⚙️**
