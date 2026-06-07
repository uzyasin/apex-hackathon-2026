# 🔀 Full-Stack & Git Yöneticisi — Detaylı Rehber

> Bu rehber **Full-Stack & Git Yöneticisi** içindir.
> Diğer roller: [AI Uzmanı](01_AI_UZMANI.md) · [Backend](02_BACKEND.md) · [Frontend](03_FRONTEND.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **kaptanısın**. 3 kişi kendi tüneline daldığında, tek büyük resmi sen görüyorsun.

Görevin:
- Git repo'sunu yönet: branch oluştur, merge et, çakışma çöz
- Her 30 dakikada bir takımdan durum al, tıkanan yere gir
- Frontend ↔ Backend ↔ AI arasındaki entegrasyonu izle
- `main` branch'in **her zaman çalışır** durumda olmasını sağla
- README'yi son hâliyle yaz, projeyi teslim et

Sen olmadan ekip 4 saatte 4 farklı kod üretir; sayende 1 ürün olur.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + (Node.js veya Java) backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Node.js 20 + Express (önerilen) veya Java 21 + Spring Boot
- **Frontend:** React 18 + Vite + Tailwind CSS
- **AI:** Anthropic Claude — `claude-sonnet-4-6`
- **Git:** Her rol kendi branch'inde, sen merge'leri yapıyorsun

---

## 2. ORTAK: 4 Saatlik Zaman Planı

```
[0:00–0:30]  Fikir + kapsam + görev dağılımı (SEN KOORDINE EDERSİN)
[0:30–3:00]  Tam odak kodlama
[3:00–3:45]  Entegrasyon + uçtan uca test
[3:45–4:00]  Git push + README + teslim
```

**Senin için kritik anlar:**
- **0:00:** Kickoff toplantısı — herkesin rolü net mi?
- **0:30:** Herkes branch'inde, kod yazmaya başladı
- **1:00, 1:30, 2:00:** Saat sync (her saatte 2 dk durup durum al)
- **2:30:** 🚨 CODE FREEZE ilanı — yeni özellik yok
- **2:30–3:00:** Kademeli merge başlıyor
- **3:00:** Uçtan uca test, hata bulursan ilgili kişiyle birlikte düzelt
- **3:45:** README final, son push
- **4:00:** Teslim

---

## 3. ORTAK: Tüm Roller

| Rol | Sorumluluk | Dokunduğu |
|-----|-----------|-----------|
| **AI Uzmanı** | Prompt, aiService, çıktı sözleşmesi | `ai-specialist/`, `aiService.js` |
| **Backend** | Endpoint'ler, DB | `backend-node/src/routes/`, `services/` (AI dışı) |
| **Frontend** | UI | `frontend/src/` |
| **FS Yöneticisi (sen)** | Git, merge, koordinasyon, destek | Her yer (ama dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar — SENİN SORUMLULUĞUN

### A. Repo Hazırlığı (15 dk)
```bash
# 1. GitHub'da boş repo oluştur (private veya public)
# https://github.com/new → adı: takim-adi/hackathon-2026

# 2. Boilerplate'i push et
cd C:\Users\tcyuz\Desktop\hackaton
git init
git add .
git commit -m "chore: hackathon boilerplate"
git branch -M main
git remote add origin https://github.com/TAKIM_ADI/REPO_ADI.git
git push -u origin main

# 3. Feature branch'lerini oluştur
git checkout -b feat/ai && git push -u origin feat/ai && git checkout main
git checkout -b feat/backend && git push -u origin feat/backend && git checkout main
git checkout -b feat/frontend && git push -u origin feat/frontend && git checkout main

# 4. Settings → Collaborators → 3 kişiyi ekle
```

### B. API Anahtarı Yönetimi (10 dk)
- [ ] [console.anthropic.com](https://console.anthropic.com) → API Keys → yeni anahtar
- [ ] $20-50 kredi yükle (4 saat yoğun kullanım için yeterli)
- [ ] **Yedek anahtar oluştur** (rate limit veya hesap sorunu için)
- [ ] Anahtarları güvenli kanalda (WhatsApp DM, Signal, 1Password) takımla paylaş

**ASLA:**
- ❌ Public GitHub'a commit
- ❌ Slack/Discord public channel
- ❌ Ekran paylaşımı sırasında görsel olarak

### C. Takım Testi (15 dk)
Her takım üyesi:
- [ ] Repo'yu clone'ladı mı?
- [ ] `.env` oluşturdu mu?
- [ ] Backend ayağa kalktı mı?
- [ ] Frontend ayağa kalktı mı?
- [ ] `/api/analyze` test edildi mi?

**Tamamlanmamış bir kişi varsa, yarışma günü zaman kaybedeceksiniz. Bu gece çözün.**

### D. İletişim Kanalı Sabitle (5 dk)
- [ ] WhatsApp / Discord / Telegram — hangi platform?
- [ ] Sesli haberleşme (varsa) test edildi mi?
- [ ] Yarışma günü yerleşim planı net mi? (yan yana oturmak ideal)

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika — SENIN ROLÜN

Konu açıklandığında **diğer 3 kişi konuyu okurken, sen şunları yapıyorsun:**

```bash
# Repo'yu açıyorsun, main'i pull
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout main
git pull origin main

# Backend ve Frontend'i ayağa kaldırıyorsun
cd backend-node && npm install && npm run dev &
cd ../frontend && npm install && npm run dev &
```

Sonra ilk 30 dakikalık toplantıyı sen yönetiyorsun.

### İlk Toplantı (15-20 dk) — Sen Yöneteceksin

**Soruları sor, kararı yaz:**
1. **Ürün ne olacak?** (1 cümlede tanımla)
2. **MVP'de ne var, ne yok?** (kapsamı daralt)
3. **Hangi backend?** (Node mu Java mı?)
4. **Belge analizi var mı?** (RAG gerekecek mi?)
5. **Kim hangi sayfada/endpoint'te?**

Kararları **AI_CONTEXT/2_architecture.md**'ye yaz, herkes görsün.

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **Koordinasyon:** Her 30 dakikada bir durum al
2. **Git Yönetimi:** Branch'leri oluştur, merge et, çakışma çöz
3. **Quality Gate:** main her zaman çalışmalı — bozuk kodu merge etme
4. **Destek:** Tıkanan kişinin yanına git, birlikte çöz
5. **README:** Son hâliyle yaz
6. **Teslim:** Repo URL'sini jüriye ver

### Yan Görevler
- Dış servis (Vercel/Railway) deploy (gerekirse)
- Demo öncesi tarayıcı setup
- Sunum koordinasyonu
- Saat ilanları (1:00, 2:00, 2:30 — code freeze!)

---

## 7. SENIN: Dokunduğun Dosyalar

```
README.md                           ← Final hâliyle yazacaksın
AI_CONTEXT/2_architecture.md        ← Toplantıda güncel tut
takim-rehberi/                      ← Bu klasörü update etmen gerekmiyor
```

**Branch:** `feat/support` (gerekirse kod yazarsan)

**main branch'ine sadece merge yaparak gir, direkt commit etme.**

Diğer dosyalar — başkalarının. Ama destek için her yere bakabilirsin:
- AI Uzmanı tıkandı → `aiService.js`'e bak, beraber çöz
- Backend tıkandı → `api.js` veya `dbService.js`'e bak
- Frontend tıkandı → `HomePage.jsx`'e bak

---

## 8. SENIN: Adım Adım Timeline

### 🟢 [0:00 – 0:30] Kickoff ve Koordinasyon

**Hedef:** Herkes branch'inde, konsept netleşti, kod yazmaya başlandı.

```bash
git checkout main
git pull origin main
```

**Yapacakların:**
1. **Konuyu birlikte oku** (3 dk)
2. **Ürün ve MVP'yi netleştir** (10 dk) — yukarıdaki 5 soruyu sor
3. **`architecture.md`'yi takımla doldur** (5 dk):
   - Active Backend: Node.js / Java
   - Product Summary
   - Core User Flow
4. **Rol ve görev dağılımı** (5 dk):
   - AI: Sözleşme + v1 prompt
   - BE: DB şeması + ilk endpoint
   - FE: Mock UI iskeleti
   - SEN: Koordinasyon + README başlangıcı
5. **Push:**
   ```bash
   git add AI_CONTEXT/2_architecture.md
   git commit -m "docs: ürün kararları ve mimari"
   git push origin main
   ```
6. **Herkese:**
   > "Tamam, hadi başlayalım! 30 dk sonra ilk sync olacak. Tıkanan haber versin."

---

### 🟢 [0:30 – 1:00] İlk Sync ve Destek

```bash
# Branch'lerin durumu
git fetch --all
git log --all --oneline | head -15
```

**Sor (2-3 dk):**
- **AI:** "Sözleşme hazır mı? v1 prompt'u curl ile test ettin mi?"
- **BE:** "DB tablosu oluştu mu? Endpoint listesi?"
- **FE:** "Mock UI başladı mı? Hangi alanları göstereceğin belli mi?"

**Tıkanan varsa:**
- AI prompt'ta sorun → Birlikte iterasyon yap
- BE endpoint'te takıldı → Boilerplate'teki örneği göster, birlikte yaz
- FE'nin tasarımı yok → Tailwind örnekleri öner

**README başlangıcı yaz:**
```bash
git checkout main
code README.md
```

README'nin başına projeyi yaz:
```markdown
# [PROJE ADI]

[1 cümle açıklama]

## Özellikler
- [Özellik 1]
- [Özellik 2]
```

```bash
git add README.md
git commit -m "docs: proje adı ve özet"
git push origin main
```

---

### 🟢 [1:00 – 2:00] Sürekli Destek + Aralıklı Merge

**Sync (1:00):**
- Backend ilk endpoint'i bitirdi mi? **Frontend bağlanabilir mi?**
- AI v1 çalışıyor mu? Çıktı şeması değişti mi?
- FE mock'tan gerçek API'ye geçebilir mi?

**Kritik soru:** Frontend backend'e bağlanabiliyor mu? **Hayır ise sen müdahale et.**

**Hızlı entegrasyon testi:**
```bash
# Backend çağrısı
curl -X POST localhost:3001/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"input":"test"}'

# Frontend'in proxy'sini test et
curl http://localhost:5173/api/analyze \
  -X POST -H "Content-Type: application/json" \
  -d '{"input":"test"}'
```

İkisi de çalışıyorsa entegrasyon OK.

**Erken merge (opsiyonel, kademeli):**
Eğer AI ve Backend bağımsız bitmiş ve test edilmişse, FE'nin merge'i için bekleme:
```bash
git checkout main
git merge feat/ai --no-ff -m "merge: ai progress"
git push origin main

# AI ve BE'ye haber ver: main'i pull et
```

**Sync (1:30):**
- FE backend'e bağlandı mı?
- AI'ın prompt'u kararlı mı?
- Eksik bir özellik var mı?

---

### 🟢 [2:00 – 2:30] Tıkanma Dönemi — Sen Aktif Ol

Bu saat çoğunlukla zor saat. Kişiler hata ile boğuşur.

**Yapılacaklar:**
1. **Sync (2:00):** Kim ne durumda?
2. **Tıkanan varsa direkt yanına git** ve sorunu birlikte çöz
3. **Code freeze hatırlatıcısı:** 2:25'te ekibe:
   > "5 dakika sonra code freeze. Eklemek istediğiniz son özellik varsa bitirin, sonrası sadece hata düzeltme."

**Yapılacak ortak hatalar:**
- AI çıktısı bazen JSON parse edilemiyor → AI Uzmanı'na fallback'i kontrol ettir
- Backend bir endpoint'te 500 dönüyor → log'ları beraber oku
- Frontend mobil'de bozuk → DevTools'tan birlikte düzelt

---

### 🟢 🚨 [2:30] CODE FREEZE — Ciddi Şekilde İlan Et

**Yüksek sesle ya da kanalda:**
> "🚨 CODE FREEZE — yeni özellik YOK. Şu anki branch'iniz çalışır halde olmalı.
> 5 dk içinde merge başlayacak. Lütfen son commit'lerinizi push edin."

```bash
git fetch --all
# Her branch'in son commit'ini kontrol et
git log --oneline -1 origin/feat/ai
git log --oneline -1 origin/feat/backend
git log --oneline -1 origin/feat/frontend
```

---

### 🟢 [2:30 – 3:00] Kademeli Merge

**Sıra önemli: AI → Backend → Frontend** (bağımlılık zinciri).

```bash
git checkout main
git pull origin main

# 1. AI branch'ini merge et
git merge feat/ai --no-ff -m "merge: ai specialist final"

# Test: backend çalışıyor mu hâlâ?
cd backend-node && npm run dev
# Başka terminal:
curl -X POST localhost:3001/api/analyze -d '{"input":"test"}' -H "Content-Type: application/json"
# 200 dönüyorsa OK
```

Sorun yoksa push:
```bash
git push origin main
```

```bash
# 2. Backend branch'ini merge et
git merge feat/backend --no-ff -m "merge: backend final"

# Test: tüm endpoint'ler çalışıyor mu?
curl localhost:3001/api/results
curl localhost:3001/health

git push origin main
```

```bash
# 3. Frontend branch'ini merge et
git merge feat/frontend --no-ff -m "merge: frontend final"

# Test: tarayıcıda her şey?
cd ../frontend && npm run dev
# Tarayıcıda http://localhost:5173

git push origin main
```

### Merge Çakışması Olursa

```bash
git status
# Çakışan dosyaları gösterir, örneğin:
# both modified: AI_CONTEXT/2_architecture.md

# Dosyayı aç
code AI_CONTEXT/2_architecture.md
# <<<<<<< HEAD
# (main'in versiyonu)
# =======
# (feat/branch'ın versiyonu)
# >>>>>>> feat/branch
```

**Çakışmayı çöz:**
1. `<<<<<<<`, `=======`, `>>>>>>>` işaretlerini ve aralarındakileri okuyup hangisini tutacağına karar ver
2. Genelde **her iki tarafın değişikliklerini** birleştir
3. İşaretleri sil, temiz dosya bırak
4. ```bash
   git add [çakışan_dosya]
   git commit -m "fix: merge conflict çözüldü"
   git push origin main
   ```

---

### 🟢 [3:00 – 3:45] Uçtan Uca Test

**Hedef:** main üzerinde tam akış çalışıyor.

```bash
git checkout main
git pull origin main

# Sıfır state'ten dene
rm backend-node/hackathon.db   # eski DB'yi sil
cd backend-node && npm install && npm run dev &
cd ../frontend && npm install && npm run dev &
```

#### Test Senaryoları (her birini elle yap)

| Test | Beklenen | Sonuç |
|------|----------|-------|
| Tarayıcıda http://localhost:5173 açılıyor | Anasayfa görünür | ☐ |
| Normal girdi gönder | Sonuç kartı, score görünür | ☐ |
| Boş girdi gönder | Buton disabled / hata mesajı | ☐ |
| Çok uzun girdi (>10000) | Backend 400 dönüyor, UI hata gösteriyor | ☐ |
| Sayfayı yenile (F5) | Anasayfa açılıyor, çökmüyor | ☐ |
| Backend kapalı, gönder | UI'da anlamlı hata | ☐ |
| Mobile viewport | Düzgün görünüyor | ☐ |
| Geçmiş kayıtlar (varsa) | Listeleniyor | ☐ |

**Hata bulursan:**
- Küçük bir hataysa, ilgili kişiyi çağır, kendi branch'inde düzeltsin, sen merge et
- Kritik hataysa, GÖNDERMEMEK için git reset düşün (son çare)

```bash
# Eğer merge tamamen yanlış gittiyse (son çare):
git reset --hard HEAD~1   # son merge'i geri al
# Sonra tekrar merge et
```

---

### 🟢 [3:45 – 4:00] README + Teslim

**README.md'yi tam versiyona getir:**

```markdown
# [PROJE ADI]

[2-3 cümle açıklama: ne yapar, hangi sorunu çözer, kim için]

## 🎯 Demo

[Bir cümle: "Kullanıcı X yapar, sistem Y dönderir"]

## 🚀 Çalıştırma

### Gereksinimler
- Node.js 20+
- Anthropic API anahtarı

### Adımlar
\`\`\`bash
# Backend
cd backend-node
cp .env.example .env
# .env içine ANTHROPIC_API_KEY yaz
npm install
npm run dev

# Frontend (başka terminal)
cd frontend
npm install
npm run dev

# Tarayıcıda http://localhost:5173 aç
\`\`\`

## 🤖 Kullanılan AI

- **Model:** Anthropic Claude Sonnet 4.6 (`claude-sonnet-4-6`)
- **Prompt Stratejisi:** Yapısal JSON çıktı, fallback mekanizması
- **Token Yönetimi:** max_tokens=1024 ile optimize edildi

## 📐 Mimari

\`\`\`
React (frontend) → Express (backend) → Anthropic API
                       ↓
                   SQLite (DB)
\`\`\`

## 👥 Takım

- [İsim 1] — AI Model Uzmanı
- [İsim 2] — Backend Geliştirici
- [İsim 3] — Frontend Geliştirici
- [İsim 4] — Full-Stack & Git Yöneticisi

## 📄 Lisans

MIT (veya hackathon kuralı ne diyorsa)
```

**Final push:**
```bash
git add README.md
git commit -m "docs: final README"
git push origin main

# Son kontrol
git log --oneline -10
git status
# Uncommitted bir şey kalmamış olmalı
```

**Repo URL'sini al:**
```bash
git remote get-url origin
# https://github.com/TAKIM/REPO.git
```

**Jüriye teslim** edilecek bilgiler:
- Repo URL
- Final commit hash (üst commit)
- Takım isimleri
- Çalıştırma talimatı (README'de zaten var)

---

## 9. SENIN: Sık Karşılaştığın Hatalar

### Hata 1: Merge çakışması her yerde

**Önleme:**
- Herkes farklı klasörde çalışırsa çakışma çok az olur
- `AI_CONTEXT/2_architecture.md` herkesin dokunduğu tek dosya → bunu güncellerken takımı uyar

**Çözümlerde:**
```bash
# Çakışma görüldü
git status

# Hangi dosyalar çakışıyor?
git diff --name-only --diff-filter=U

# Tek tek aç, düzelt
code [çakışan_dosya]

# Düzelttikten sonra
git add [çakışan_dosya]
git commit
```

---

### Hata 2: Yanlış branch'e merge yaptım

**Çözüm (henüz push etmediysen):**
```bash
git reset --hard HEAD~1   # son commit'i geri al
```

**Çözüm (push edildiyse):**
```bash
git revert HEAD   # ters commit oluştur, history korunur
git push origin main
```

**Asla:** `git push --force` (collaborators'ın history'sini bozar)

---

### Hata 3: Bir branch deprecate oldu, gereksiz commit'ler var

**Çözüm:** Dürüst ol, hackathon'da history kozmetiği önemli değil. Çalışan main daha önemli.

---

### Hata 4: `.env` git'e gitti

**Acil çözüm:**
```bash
# Anahtarı iptal et (Anthropic dashboard)
# Yenisini oluştur
# Tüm collaborators'a güvenli kanalda yeni anahtarı paylaş

# .env'yi history'den temizle
git rm --cached .env
git commit -m "fix: .env removed from tracking"
echo ".env" >> .gitignore
git add .gitignore
git commit -m "chore: .env to gitignore"
git push origin main
```

**Not:** History temizleme (`git filter-branch`) çok riskli, hackathon'da yapma. Anahtarı iptal etmek yeter.

---

### Hata 5: 3:45'te kritik bug bulundu

**Karar matrisi:**
- Bug demo'da görünür mü? **EVET** → düzelt
- Bug edge case mi? **EVET** → bırak, demo'da bahsetme
- Düzeltmek 15 dk içinde mümkün mü? **EVET** → düzelt
- 15 dk'dan fazla sürerse → bırak, demo'da bahsetme

---

### Hata 6: GitHub push reddetti

**Belirti:**
```
! [rejected] main -> main (fetch first)
```

**Çözüm:**
```bash
git pull origin main --rebase
# Çakışma varsa çöz
git push origin main
```

---

## 10. SENIN: Demo Hazırlığı ve Sunum Koordinasyonu

### Demo Öncesi (3:30-3:45)
- [ ] Tarayıcı temizle (Ctrl+Shift+Del → cache)
- [ ] Tam ekran (F11)
- [ ] DevTools kapalı (sunum sırasında dikkat dağıtır)
- [ ] Backend ve frontend her ikisi çalışıyor
- [ ] Tekrar git pull yap, en son main'de misin?

### Sunum Akışı (sen koordine ediyorsun)

| Sıra | Kim | Konu | Süre |
|------|-----|------|------|
| 1 | **Sen** | Açılış + proje özeti | 1 dk |
| 2 | Frontend | Canlı demo | 2 dk |
| 3 | AI Uzmanı | AI mimarisi | 1.5 dk |
| 4 | Backend | Mimari + tech stack | 1 dk |
| 5 | **Sen** | Kapanış + GitHub linki + Q&A | 0.5 dk |

**Açılış konuşmanı yaz (1 dakika):**
> "Merhaba, biz [TAKIM ADI] olarak [SORUN]'a [ÇÖZÜM] geliştirdik.
> 4 saatte tamamladığımız bu MVP'de [ANA ÖZELLİK 1] ve [ÖZELLİK 2] var.
> Kullanıcı [INPUT] giriyor, sistemimiz Claude AI ile [İŞ] yapıp [OUTPUT] dönderiyor.
> Şimdi arkadaşım [FE İSİM] size canlı demoyu gösterecek."

**Kapanış (30 saniye):**
> "Projemizin tüm kodu GitHub'da: [REPO URL]
> Sorularınızı bekliyoruz."

### Q&A Hazırlığı
Olası sorular ve cevaplar:
- **"Ne kadar zamanda yaptınız?"** → 4 saatte
- **"Hangi modeli kullandınız?"** → Anthropic Claude Sonnet 4.6
- **"Maliyet?"** → Token başına çok düşük, prod'da scale edilebilir
- **"Production'a deploy ettiniz mi?"** → Henüz değil, MVP. Vercel/Railway ile 10 dk'da yapılır.
- **"Sıradaki adım?"** → [Şu özelliği eklemek, kullanıcı testi]

---

## 11. ORTAK: Git İş Akışı

### Branch Stratejisi
```
main                           ← BURADA SEN MERGE EDERSIN
├── feat/ai
├── feat/backend
├── feat/frontend
└── feat/support               ← SENİN BRANCH (gerekirse kod yazarsan)
```

### Merge Sırası (Bağımlılığa Göre)
1. AI (Backend'in çağırdığı `aiService.js`)
2. Backend (Frontend'in çağırdığı endpoint'ler)
3. Frontend (Backend'i kullanan UI)

### Yasak Komutlar (Sen Dahil)
- `git push --force` — sadece kendi support branch'inde olabilir, main'de ASLA
- `git reset --hard origin/main` — sadece kendi makinende, kimseye push etme

### Acil Komutlar
```bash
# Tüm branch'lerin son durumu
git fetch --all
git branch -avv

# Son N commit (tüm branch'lerde)
git log --all --oneline --graph | head -20

# Bir dosyanın son değiştiriciler
git log --oneline -- path/to/file

# Commit'i geri al (push'tan ÖNCE)
git reset --soft HEAD~1

# Commit'i geri al (push'tan SONRA)
git revert HEAD
```

---

## 12. ORTAK: Takım İletişim Protokolü — Sen Yönetiyorsun

### Saatlik Sync — Sen başlatıyorsun

Her saat (1:00, 2:00, 3:00) 2 dakika dur:
- Yüksek sesle sor: "Herkes 30 saniyede ne yaptığını anlatsın"
- Sıra ile: AI → Backend → Frontend
- Sen son anlatırsın: "Şu anda main şu durumda, sırada merge'ler var"
- Tıkanan varsa "10 dk sonra sana geleceğim, beraber çözeriz"

### Kritik Anonslar

| Saat | Anons |
|------|-------|
| 0:30 | "Tamam herkes kendi branch'inde. İlk commit'lerinizi 30 dk içinde bekliyorum." |
| 2:25 | "5 dakika sonra code freeze. Son commit'lerinizi atın." |
| 2:30 | "🚨 CODE FREEZE. Merge başlıyorum. 30 dakikada uçtan uca akış çalışmalı." |
| 3:00 | "Merge tamam. Şimdi uçtan uca test. Hata bulursanız bildirin." |
| 3:45 | "Son 15 dakika. README ve push yapıyorum. Hiçbir şey eklenmesin." |

---

## 13. ORTAK: Acil Durum

### Senaryo 1: main bozuk, çalışmıyor
**Çözüm:**
```bash
# Son çalışan commit'e dön
git log --oneline main
git reset --hard <son_çalışan_hash>
git push origin main --force-with-lease   # collaborators'a haber ver!
```

### Senaryo 2: GitHub down
**Çözüm:** Local commit yapmaya devam. GitHub geri gelince push.

### Senaryo 3: 2 takım üyesi aynı dosyaya yazmış (kaçınılmaz çakışma)
**Çözüm:** Her ikisini de çağır, dosyayı beraber merge edin.

### Senaryo 4: Bir takım üyesi laptop'unu kaybetti
**Çözüm:**
- Son push'unun nerede olduğunu öğren
- Başka laptop'a clone'la
- Branch'inden devam et

### Senaryo 5: API key ortaya çıktı, exploit edildi
**Çözüm:**
- Anahtarı hemen iptal et (Anthropic dashboard)
- Yedek anahtarı dağıt
- Demo'da kullanılan anahtarı tutmak için yeni bir anahtar oluştur

### Senaryo 6: Hiçbir şey çalışmıyor, panik (3:30'da)
**Çözüm:**
- Sakin ol, demo için ne göstereceğine karar ver
- Çalışan parçaları birleştir
- Anlatım ile boşlukları kapat
- "Bu prototip MVP, eksik olan X özelliği yarınki versiyonda olacak"

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce sen tek tek kontrol et:

- [ ] `main` branch'i çalışıyor
- [ ] `cd backend-node && npm install && npm run dev` sorunsuz
- [ ] `cd frontend && npm install && npm run dev` sorunsuz
- [ ] Tarayıcıda anasayfa açılıyor
- [ ] Test girdisi gönderince sonuç dönüyor
- [ ] README.md güncel ve detaylı
- [ ] `.env` git'te değil (`.gitignore`'da `.env` var)
- [ ] Tüm commit'ler push edilmiş (`git status` clean)
- [ ] Tüm takım üyelerinin commit'i main'de var

### Senin son commit'in
```bash
git checkout main
git pull origin main
git log --oneline -10
git status   # clean olmalı

# Final push
git push origin main
```

### Teslim
- Repo URL'sini al: `git remote get-url origin`
- Organizasyon'un teslim formuna gir
- Takım isimleri, GitHub URL, contact bilgisi

---

## 15. Hızlı Referans

### Tüm git komutları (sen sık kullanacaksın)

```bash
# Durum kontrolü
git status
git fetch --all
git branch -avv
git log --all --oneline --graph | head -20

# Merge
git checkout main
git merge feat/[branch] --no-ff -m "merge: [açıklama]"
git push origin main

# Çakışma
git status                    # çakışanları göster
git diff --name-only --diff-filter=U   # sadece çakışanlar
# dosyayı düzelt
git add [dosya]
git commit
git push

# Geri alma
git reset --soft HEAD~1       # commit'i geri al, değişikleri tut
git reset --hard HEAD~1       # commit'i ve değişiklikleri geri al
git revert <hash>             # ters commit oluştur (push sonrası)

# Acil durum
git stash                     # değişiklikleri sakla
git stash pop                 # geri getir
git reflog                    # tüm hareketlerin geçmişi (kurtarma için)
```

### `/ship` komutu (Claude Code'da)
```
/ship README.md'yi güncelle, proje özeti, çalıştırma adımları ve mimari ekle
```

### Tarayıcı sekmeleri (yarışma boyunca açık kalsın)
- GitHub repo
- console.anthropic.com (API key, usage)
- localhost:5173 (frontend)
- localhost:3001/health (backend status)
- WhatsApp/Discord (takım iletişim)

---

**Başarılar! Sen takımın kaptanısın, sakin kal, koordine et. 🔀**
