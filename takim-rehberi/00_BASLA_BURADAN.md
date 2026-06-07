# 🚀 Takım Rehberi — Buradan Başla

Bu klasör, 4 kişilik takımın hackathonu kazanması için hazırlandı.

---

## Hangi Dosyayı Kim Okumalı?

| Rol | Dosya | İçerik |
|-----|-------|--------|
| **AI Model Uzmanı** | [`01_AI_UZMANI.md`](01_AI_UZMANI.md) | Prompt mühendisliği, AiService.java, çıktı sözleşmesi |
| **Backend Geliştirici** | [`02_BACKEND.md`](02_BACKEND.md) | Java Spring Boot endpoint'leri, H2 DB, API entegrasyonu |
| **Frontend Geliştirici** | [`03_FRONTEND.md`](03_FRONTEND.md) | UI, sayfalar, API client, kullanıcı akışı |
| **Full-Stack & Git Yöneticisi** | [`04_GIT_YONETICISI.md`](04_GIT_YONETICISI.md) | Merge, koordinasyon, deploy, README |

**Önemli:** Her dosya **kendi içinde komple**. Her dosyada ortak bilgi (proje, tech stack, time budget, git iş akışı, iletişim, acil durum) tekrar var. Yalnızca kendi dosyanı okuyarak başarılı olabilirsin.

---

## ⏰ Bu Gece (Yarışma Öncesi) Takım Toplantısı

**Süre:** 30-45 dakika.

### 1. Takım Üzerinde Konuş (5 dk)
- [ ] Roller kim olacak? (4 rol, 4 kişi)
- [ ] Kim hangi rolünü alıyor? (yetenekleri ve tecrübeyi tartış)
- [ ] Yarışma günü buluşma yeri/saati netleşti mi?

### 2. Repo Hazırlığı (10 dk) — **FS Yöneticisi yönetir**
- [ ] GitHub'da boş bir repo oluştur (ÖRN: `takim-adi/hackathon-2026`)
- [ ] Bu boilerplate'i ilk commit olarak push et (komutlar aşağıda)
- [ ] Herkesi collaborator ekle (Settings → Collaborators)
- [ ] Her ekip üyesi için branch oluştur (`feat/ai`, `feat/backend`, `feat/frontend`)
- [ ] Herkes repo URL'sini alsın

```bash
cd C:\Users\tcyuz\Desktop\hackaton
git init
git add .
git commit -m "chore: hackathon boilerplate"
git branch -M main
git remote add origin https://github.com/TAKIM_ADI/REPO_ADI.git
git push -u origin main

# Branch'ler
git checkout -b feat/ai && git push -u origin feat/ai && git checkout main
git checkout -b feat/backend && git push -u origin feat/backend && git checkout main
git checkout -b feat/frontend && git push -u origin feat/frontend && git checkout main
```

### 3. API Anahtarı Kararı (5 dk)
**Soru:** Tek ortak Anthropic API anahtarı mı kullanılacak, her kişi kendisi mi açacak?

**Öneri:** Tek ortak anahtar (AI Uzmanı veya FS Yöneticisi açar).
- Hız avantajı: Yarışma günü herkesin .env'inde aynı anahtar
- Risk: Bir kişi limiti tüketirse herkes etkilenir
- Çözüm: Yedek olarak 2. anahtar bulundur

**Aksiyon:**
- [ ] [console.anthropic.com](https://console.anthropic.com) → API Keys → Yeni anahtar oluştur
- [ ] $20-50 kredi yükle (4 saat yoğun kullanım için yeterli)
- [ ] Anahtarı **güvenli bir kanalda** (Signal, WhatsApp DM, 1Password) ekiple paylaş
- [ ] **Asla** Slack/Discord public channel veya GitHub'a koyma

### 4. Herkes Boilerplate'i Çalıştırsın (15 dk)
Her takım üyesi kendi laptop'unda şunu test etsin:

```bash
# Repo'yu clone'la
git clone https://github.com/TAKIM_ADI/REPO_ADI.git
cd REPO_ADI

# Anthropic API anahtarını environment variable olarak set et
# Windows PowerShell:
$env:ANTHROPIC_API_KEY="sk-ant-..."
# Linux/Mac:
export ANTHROPIC_API_KEY=sk-ant-...

# Backend test (Java Spring Boot)
cd backend
mvn dependency:resolve   # dependency'leri önceden indir (~3-5 dk ilk seferde)
mvn spring-boot:run
# Başka terminalde:
curl http://localhost:3001/health
# → {"success":true,"data":{"status":"ok"}} gelmeli

# AI çağrısı test
curl -X POST http://localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"input\": \"merhaba\"}"
# → success: true ile JSON gelmeli

# Frontend test
cd ../frontend
npm install
npm run dev
# → Tarayıcıda http://localhost:5173 açılmalı
```

**Java 21 ve Maven kurulu mu?** `java -version` ve `mvn -version` ile kontrol et.

**Sorun yaşayan varsa şimdi çöz. Yarışma günü çözmeye çalışmak felaket olur.**

### 5. Java/Maven Hazırlığı (5 dk)
Bu proje **Java 21 + Spring Boot 3.2.5** kullanır.

**Aksiyon:**
- [ ] Her ekip üyesi `java -version` → Java 21 olduğunu doğrulasın
  - Yoksa: [adoptium.net](https://adoptium.net) → Temurin 21 LTS
- [ ] Her ekip üyesi `mvn -version` → Maven 3.8+ olduğunu doğrulasın
  - Yoksa: [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
- [ ] `cd backend && mvn dependency:resolve` ile bağımlılıkları önceden indirin (yarışmada zaman kaybetmeyin)
- [ ] IDE setup: IntelliJ Community veya VS Code + Extension Pack for Java

### 6. İletişim Kanalı (5 dk)
- [ ] WhatsApp grubu / Discord channel / Slack — hangisini kullanacaksınız?
- [ ] Sesli haberleşme nasıl? (yan yana oturuyorsanız sesli konuşun)
- [ ] Acil durum: kim kimi nasıl çağıracak?

---

## 🎬 İlk Feature — Uçtan Uca Akış (Herkes Anlamalı)

Yarışma günü ilk feature'ın **nasıl** ekipten geçtiğini bilmek kritik. İşte örnek senaryo:

### Senaryo: "Akıllı CV Değerlendirme" konusu verildi

#### T+0:00 — Konu açıklanır
Hepiniz birlikte konuyu okur, ürünü tartışır, MVP'yi netleştirirsiniz.

**Karar:** Kullanıcı CV metnini yazar → AI puanlar → ekrana skor + öneri çıkar.

#### T+0:10 — AI Uzmanı çıktı sözleşmesini tanımlar
```json
{
  "score": 85,
  "summary": "string",
  "strengths": ["string"],
  "gaps": ["string"],
  "verdict": "Mülakata Çağır" veya "Reddet"
}
```

**Bildirir:** "Arkadaşlar, AI bu JSON'ı dönecek. Backend ve Frontend buna göre kod yazsın."

#### T+0:15 — Backend mimarisini ve DB şemasını yazar
`AI_CONTEXT/2_architecture.md`'ye:
- Tablo: `analyses(id, input, context, result_json, score, created_at)` — zaten boilerplate'te var
- Ek tablo: `positions(id, title, requirements)` — yeni eklenecek
- Endpoint: `POST /api/analyze`, `GET /api/results` zaten var; eklenecekler de yazılır

**Bildirir:** "Mimari hazır, ben DbService'e yeni tablo ekleyip ApiController'a endpoint yazmaya başlıyorum."

#### T+0:20 — Herkes kendi işine başlar (PARALEL)
- **AI:** v1 prompt'unu yazar, `AiService.SYSTEM_PROMPT`'a koyar, `curl` ile test eder
- **Backend:** `DbService.init()`'e yeni tablo ekler, `ApiController.java`'ya endpoint yazar
- **Frontend:** Mock veriyle UI'ı şekillendirir (`HomePage.jsx`)
- **FS:** Herkesi kontrol eder, eksik var mı diye sorar

#### T+0:45 — AI Uzmanı ilk testini geçer
```bash
curl -X POST localhost:3001/api/analyze -d '{"input": "Yasin, 5 yıl Java"}'
# → {"success":true,"data":{"score":75,"verdict":"...","strengths":[...]}}
```
**Commit ve push:** `git commit -m "feat(ai): v1 prompt"`

#### T+1:00 — Backend endpoint çalışıyor
Backend, `AiService.analyze()` metodunu çağıran endpoint'i yazdı, DbService ile DB'ye kaydetti. Test geçti.
**Bildirir:** "POST /api/analyze hazır, DB'ye kaydediyor. Frontend bağlanabilirsiniz."

#### T+1:30 — Frontend mock'u kaldırır, gerçek API'ye bağlanır
`HomePage.jsx`'teki `mockResult` yerine `const response = await analyze(input)` kodu girer.
Tarayıcıda test eder → sonuç ekranda görünüyor.
**Bildirir:** "Frontend backend'e bağlandı, çalışıyor."

#### T+2:00 — İlk uçtan uca akış tamam
Kullanıcı tarayıcıdan CV girer → arka plan AI'ya gider → sonuç ekranda görünür. **Demo yapılabilir durumda.**

#### T+2:00–3:00 — İyileştirme döngüsü
- AI: prompt'u iyileştirir, daha tutarlı çıktı verir
- Backend: history endpoint'i ekler, dosya upload ekler
- Frontend: tasarımı güzelleştirir, ek alanları ekrana koyar

#### T+3:00 — Code freeze
**Hiç kimse yeni özellik eklemiyor.** FS yöneticisi tüm branch'leri main'e merge eder.

#### T+3:00–3:45 — Uçtan uca test
- Boş input
- Çok uzun input
- API key yanlış (geçici test için)
- Tüm endpoint'ler

#### T+3:45–4:00 — README ve teslim
README'yi güncelle, push et, repo URL'sini jüriye teslim et.

---

## 🎤 Jüri Sunumu — Kim Ne Anlatır?

Sunum sırasında **herkesin söz alması** etkili.

| Sıra | Kim | Konu | Süre |
|------|-----|------|------|
| 1 | FS Yöneticisi | Proje özeti, ne yaptık, sorunu nasıl çözdük | 1 dk |
| 2 | Frontend | Canlı demo: arayüz, kullanıcı akışı | 2 dk |
| 3 | AI Uzmanı | AI'ın "akıllı" kısmı: prompt stratejisi, neden Claude | 1.5 dk |
| 4 | Backend | Mimari: nasıl ölçeklenebilir, neden bu seçimler | 1 dk |
| 5 | FS Yöneticisi | Kapanış, Github linki, soru-cevap | 0.5 dk |

**Toplam:** 6 dakika sunum + soru-cevap

---

## 🚨 Genel Kurallar — Hepimiz Uyalım

1. **Branch'inden çıkma.** Sadece `feat/[rolun]` üzerinde çalış. `main`'e direkt commit YOK.
2. **30 dakikadan fazla commit'siz çalışma.** Düzenli push = veri kaybı yok.
3. **Tıkandın mı? 10 dk içinde haber ver.** Sessiz kalan kişi tüm takımı yavaşlatır.
4. **Çıktı sözleşmesi değiştirilemez** (yarışma ortasında). Mecbur değiştirilirse herkese duyur.
5. **2:30'dan sonra yeni özellik YOK.** Sadece düzeltme ve cila.
6. **`.env` dosyası git'e gitmez.** `.gitignore` zaten engelliyor, ama her halükarda kontrol et.
7. **AI hata verirse panik yok.** Fallback yanıt zaten kodda var, uygulama çökmez.
8. **Soru sormaktan utanma.** "Aptal soru" diye bir şey yok.

---

## 📂 Proje Yapısı — Genel Bakış

```
hackathon/
├── takim-rehberi/              ← Bu klasör — kişi başına rehber
├── .claude/agents/             ← 4 AI agent (planner, coder, tester, reviewer)
├── .claude/commands/ship.md    ← /ship komutu — feature pipeline'ı
├── .pipeline/                  ← Agent handoff dosyaları (otomatik)
├── AI_CONTEXT/                 ← Tüm AI araçları için referans
│   ├── 1_system_rules.md       ← Kodlama standartları
│   ├── 2_architecture.md       ← ⚠️ İLK 15 DAKİKADA DOLDUR
│   └── 3_ai_prompts.md         ← Prompt şablonları
├── ai-specialist/              ← AI Uzmanı'nın çalışma alanı
│   ├── PROMPT_GELISTIRME.md    ← Prompt iterasyon defteri
│   ├── AI_CIKTI_SOZLESMESI.md  ← ⚠️ İLK 20 DAKİKADA DOLDUR
│   └── RAG_KURULUM.md          ← Belge bağlama (gerekirse)
├── backend/                    ← Java 21 + Spring Boot 3.x
├── frontend/                   ← React + Vite + Tailwind
├── SKILLS.md                   ← AI araçlarına yapıştırılan bağlam
├── README.md                   ← Proje açıklaması
└── KULLANIM_REHBERI.md         ← Genel kullanım kılavuzu
```

---

## ⚡ Sonraki Adımın

Rolüne göre kendi dosyanı aç ve **baştan sona oku**:
- AI uzmanıysan → [`01_AI_UZMANI.md`](01_AI_UZMANI.md)
- Backend'ciysen → [`02_BACKEND.md`](02_BACKEND.md)
- Frontend'ciysen → [`03_FRONTEND.md`](03_FRONTEND.md)
- FS yöneticisiysen → [`04_GIT_YONETICISI.md`](04_GIT_YONETICISI.md)

**Bu dosyaları sadece bir kez oku.** Yarışma günü zaten çok meşgul olacaksın — şimdi içselleştir.
