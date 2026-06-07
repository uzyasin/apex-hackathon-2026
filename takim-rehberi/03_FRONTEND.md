# 🎨 Frontend Geliştirici — Detaylı Rehber

> Bu rehber **Frontend Geliştirici** içindir.
> Diğer roller: [AI Uzmanı](01_AI_UZMANI.md) · [Backend](02_BACKEND.md) · [Git Yöneticisi](04_GIT_YONETICISI.md)
> Başlangıç: [00_BASLA_BURADAN.md](00_BASLA_BURADAN.md)

---

## 0. Senin Rolün — Özet

Sen ekibin **yüzü**sün. Jüri ne görür? Senin yaptığını.

Görevin:
- React arayüzünü kullanıcı dostu, şık ve hızlı yapmak
- Backend API'siyle bağlantı kurmak (`frontend/src/api/client.js`)
- Loading, hata, boş state ekranlarını yönetmek
- Mobil ve desktop'ta düzgün görünmesini sağlamak

Kısaca: Backend ne kadar iyi çalışırsa çalışsın, jüri arayüzü göremezse "kötü proje" der. Senin işin **çalışan, şık, anlaşılır UI**.

---

## 1. ORTAK: Proje Özeti

4 saatlik AI hackathon. 4 kişilik ekip. Konu yarışma günü verilecek.
Boilerplate hazır → React frontend + (Node.js veya Java) backend + Anthropic Claude.

**Tech Stack:**
- **Backend:** Java 21 + Spring Boot 3.2.5 (port 3001)
- **Frontend:** React 18 + Vite + Tailwind CSS + Axios
- **AI:** Anthropic Claude — `claude-sonnet-4-6` (AI Uzmanı yönetir)
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
- **0:20:** AI sözleşmesi geldi, hangi alanları gösteriyor olduğunu biliyorsun
- **1:00:** Mock veriyle UI iskeleti hazır
- **2:00:** Backend'e bağlanmış, gerçek veriyle çalışıyor
- **2:30:** Tüm sayfalar tamam, loading/error state'leri var
- **3:00:** Yeni özellik yok, sadece cila

---

## 3. ORTAK: Tüm Roller

| Rol | Sorumluluk | Dokunduğu |
|-----|-----------|-----------|
| **AI Uzmanı** | Prompt, AiService, çıktı sözleşmesi | `ai-specialist/`, `AiService.java` |
| **Backend** | Java endpoint'leri, DB, file upload | `backend/src/main/java/com/hackathon/` (AiService dışı) |
| **Frontend (sen)** | UI, kullanıcı akışı | `frontend/src/` |
| **FS Yöneticisi** | Git, merge, koordinasyon | Her yer (dikkatli) |

---

## 4. ORTAK: Bu Gece Yapılacaklar

- [ ] Boilerplate'i clone'la
- [ ] `cd frontend && npm install` — bağımlılıkları indir (~2 dk)
- [ ] `npm run dev` — tarayıcıda http://localhost:5173 açılmalı
- [ ] Tarayıcıda DevTools (F12) aç, Network sekmesini gör
- [ ] React DevTools eklentisini kur (Chrome/Firefox)
- [ ] Tailwind CSS hızlıca tara: https://tailwindcss.com/docs/utility-first
- [ ] **Cursor veya VS Code** kurulu olsun + Tailwind CSS IntelliSense eklentisi
- [ ] Hızlı tasarım için önerilen referans: [tailwindui.com](https://tailwindui.com), [ui.shadcn.com](https://ui.shadcn.com) — örneklere göz at

**Sorun yaşarsan:** Yarışma günü değil, bu gece çöz.

---

## 5. ORTAK: Yarışma Günü İlk 10 Dakika

```bash
# Konu açıklandıktan sonra:
git clone https://github.com/TAKIM/REPO.git
cd REPO
git checkout feat/frontend

# Frontend'i ayağa kaldır
cd frontend
npm install   # dün yaptıysan gerek yok
npm run dev   # → http://localhost:5173 açılmalı
```

Tarayıcıda "Hackathon App" görüyorsan hazırsın.

---

## 6. SENIN: Detaylı Sorumluluğun

### Ana Görevler
1. **Ana Sayfa:** Kullanıcı giriş ekranı (form, dosya upload, vs)
2. **Sonuç Ekranı:** AI çıktısını şık göstermek (skor, liste, kart)
3. **API Entegrasyonu:** `client.js` ile backend'e bağlanmak
4. **State Yönetimi:** Loading, error, success state'leri
5. **Tasarım:** Tailwind ile temiz, modern görünüm
6. **Responsive:** Mobil ve desktop'ta düzgün

### Yan Görevler
- Sayfa başlığı, logo, branding
- Boş veri state'i ("Henüz analiz yok")
- Hata mesajları kullanıcı dostu Türkçe
- Form validation (frontend tarafı)

---

## 7. SENIN: Dokunduğun Dosyalar

```
frontend/src/pages/                ← Sayfalar (HomePage.jsx vb.)
frontend/src/components/           ← Tekrar kullanılan bileşenler
frontend/src/api/client.js         ← API çağrıları
frontend/src/App.jsx               ← Routing
frontend/src/main.jsx              ← React entry
frontend/src/index.css             ← Tailwind import (dokunma genelde)
frontend/index.html                ← Sayfa başlığı (title tag)
frontend/tailwind.config.js        ← Tema (renkler vb.)
```

**Dokunmayacağın yerler:**
- `backend/` (Backend'in)
- `ai-specialist/` (AI Uzmanı'nın)
- `frontend/vite.config.js` (FS'in)

---

## 8. SENIN: Adım Adım Timeline

### 🟢 [0:00 – 0:20] AI Sözleşmesini Bekle, Yapıyı Anla

**Hedef:** AI'nın döneceği alanları öğren, UI'da neyi nasıl gösterecek olduğunu plansa.

```bash
git checkout feat/frontend
git pull origin feat/frontend

cd frontend
npm run dev
# Tarayıcı: http://localhost:5173
```

**AI Uzmanı sözleşmeyi paylaştığında** (`ai-specialist/AI_CIKTI_SOZLESMESI.md`):

Örnek:
```json
{
  "score": 85,
  "summary": "...",
  "verdict": "Onayla",
  "details": [...]
}
```

**Plan:**
- `score` → büyük renk kodlu sayı
- `summary` → açıklama kutusu
- `verdict` → buton/etiket (yeşil/kırmızı)
- `details` → liste

`Layout.jsx`'i aç, başlığı ürün adıyla güncelle:
```jsx
<h1 className="text-xl font-semibold text-gray-900">
  CV Değerlendirici
</h1>
```

`index.html`'i de güncelle:
```html
<title>CV Değerlendirici</title>
```

**Commit:**
```bash
git add frontend/
git commit -m "feat(fe): proje başlığı eklendi"
git push
```

---

### 🟢 [0:20 – 1:30] Mock Veriyle UI İskeleti

**Hedef:** Backend hazır değil, ama UI'ı mock veriyle şekillendiriyorsun.

`frontend/src/pages/HomePage.jsx`'i tamamen ürüne göre yeniden yaz.

**Örnek (CV Değerlendirici için):**

```jsx
import { useState } from 'react';
import LoadingSpinner from '../components/LoadingSpinner';

// Geçici mock — backend hazır olunca kaldır
const MOCK_RESULT = {
  score: 85,
  summary: 'Aday güçlü teknik bilgiye sahip, liderlik tecrübesi sınırlı.',
  verdict: 'Onayla',
  details: [
    'Java ve Spring Boot tecrübesi pozisyonla uyumlu',
    'PostgreSQL bilgisi mevcut',
    'Takım yönetimi tecrübesi belirtilmemiş',
  ],
};

export default function HomePage() {
  const [input, setInput] = useState('');
  const [position, setPosition] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    if (!input.trim()) return;
    setLoading(true);
    setError('');
    setResult(null);

    try {
      // MOCK — bağlama sonra kaldır
      await new Promise((r) => setTimeout(r, 800));
      setResult(MOCK_RESULT);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      {/* Başlık */}
      <div>
        <h2 className="text-2xl font-bold text-gray-900">CV Değerlendirme</h2>
        <p className="text-sm text-gray-500 mt-1">
          Aday CV'sini ve aranan pozisyonu girin, AI uygunluğu değerlendirsin.
        </p>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Aranan Pozisyon
          </label>
          <input
            type="text"
            value={position}
            onChange={(e) => setPosition(e.target.value)}
            placeholder="Örn: Senior Java Developer"
            className="w-full rounded-lg border border-gray-300 px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            CV Metni
          </label>
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            rows={8}
            placeholder="CV'nin tüm metnini buraya yapıştırın..."
            className="w-full rounded-lg border border-gray-300 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
          />
        </div>

        <button
          type="submit"
          disabled={loading || !input.trim()}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          {loading ? 'Analiz Ediliyor...' : 'Değerlendir'}
        </button>
      </form>

      {/* Loading */}
      {loading && <LoadingSpinner text="AI değerlendiriyor..." />}

      {/* Error */}
      {error && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Result */}
      {result && (
        <ResultCard result={result} />
      )}
    </div>
  );
}

// Result Card bileşeni
function ResultCard({ result }) {
  const scoreColor =
    result.score >= 70 ? 'text-green-600' :
    result.score >= 40 ? 'text-yellow-600' : 'text-red-600';

  const verdictStyle =
    result.verdict === 'Onayla'
      ? 'bg-green-100 text-green-700'
      : 'bg-red-100 text-red-700';

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-6 space-y-4 shadow-sm">
      {/* Skor + Verdict */}
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500">Uygunluk Skoru</p>
          <p className={`text-4xl font-bold ${scoreColor}`}>{result.score}/100</p>
        </div>
        <span className={`px-3 py-1 rounded-full text-sm font-medium ${verdictStyle}`}>
          {result.verdict}
        </span>
      </div>

      {/* Summary */}
      <p className="text-sm text-gray-700">{result.summary}</p>

      {/* Details */}
      {result.details?.length > 0 && (
        <div>
          <h3 className="text-sm font-medium text-gray-700 mb-2">Detaylar</h3>
          <ul className="space-y-1">
            {result.details.map((item, i) => (
              <li key={i} className="text-sm text-gray-600 flex gap-2">
                <span className="text-blue-500">→</span>
                {item}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
```

**Tarayıcıda dene:** Form doldur, butona bas, mock sonucun ekranda göründüğünü gör.

**Commit:**
```bash
git add frontend/
git commit -m "feat(fe): ana sayfa UI - mock veriyle"
git push
```

---

### 🟢 [1:30 – 2:30] Backend'e Gerçek Bağlantı

**Hedef:** Mock'u kaldır, gerçek API'ye bağlan.

Backend POST /api/analyze hazır olduğunda (Backend'ci bildiriminde söyleyecek):

`frontend/src/api/client.js` zaten boilerplate'te hazır. İhtiyaca göre özelleştir:

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.error || error.message || 'Bilinmeyen hata';
    return Promise.reject(new Error(message));
  }
);

// Sözleşmeye göre fonksiyonları yaz
export const analyze = (input, context = null) =>
  api.post('/analyze', { input, context });

export const getResults = () => api.get('/results');
export const getResultById = (id) => api.get(`/results/${id}`);

export default api;
```

`HomePage.jsx`'te mock'u kaldır:

```jsx
import { analyze } from '../api/client';

async function handleSubmit(e) {
  e.preventDefault();
  if (!input.trim()) return;
  setLoading(true);
  setError('');
  setResult(null);

  try {
    // ESKİ: setResult(MOCK_RESULT);
    // YENİ:
    const response = await analyze(input, position);
    setResult(response.data);
  } catch (err) {
    setError(err.message || 'Bir hata oluştu, lütfen tekrar deneyin.');
  } finally {
    setLoading(false);
  }
}
```

**Test:**
1. Backend çalışıyor mu? (Backend'ciye sor)
2. Tarayıcı'da DevTools (F12) → Network tab
3. Formu doldur, gönder
4. Network'te `/api/analyze` çağrısını gör (200 dönüyor mu?)
5. Response body'sini incele
6. Ekranda sonuç görünüyor mu?

**Hata olursa:** DevTools → Console → kırmızı satırı oku. CORS hatası varsa Backend'ciye söyle.

**Commit:**
```bash
git add frontend/
git commit -m "feat(fe): backend API entegrasyonu - mock kaldırıldı"
git push
```

**Bildirim:** Backend ve FS'ye:
> "Frontend backend'e bağlandı, uçtan uca akış çalışıyor."

---

### 🟢 [2:30 – 3:00] Cila ve Edge Case'ler

**Hedef:** Demo'da kötü görünmeyecek şekilde her durumu yönet.

#### Checklist

- [ ] **Loading state:** Spinner görünüyor, buton disabled
- [ ] **Hata state:** API çökerse kırmızı kutu çıkıyor, kullanıcı dostu mesaj
- [ ] **Boş input:** Buton disabled veya validation mesajı
- [ ] **Çok uzun input:** Textarea scroll çalışıyor, gönderildiğinde 10000 karakter limiti uyarısı
- [ ] **Mobil görünüm:** DevTools telefon ikonu ile test et
- [ ] **Refresh sonrası:** Sayfa açılıyor (state kaybediyor ama sorun değil)
- [ ] **Backend kapalıyken:** "Sunucuya ulaşılamıyor" mesajı

#### Geçmiş kayıtları gösterme (opsiyonel, etkileyici)

Yeni sayfa: `frontend/src/pages/HistoryPage.jsx`

```jsx
import { useEffect, useState } from 'react';
import { getResults } from '../api/client';

export default function HistoryPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getResults()
      .then((res) => setItems(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p className="text-gray-500">Yükleniyor...</p>;
  if (items.length === 0) return <p className="text-gray-500">Henüz analiz yapılmadı.</p>;

  return (
    <div className="space-y-3">
      <h2 className="text-2xl font-bold text-gray-900">Geçmiş Analizler</h2>
      {items.map((item) => (
        <div key={item.id} className="rounded-lg border border-gray-200 bg-white p-4">
          <div className="flex justify-between items-center">
            <p className="text-sm text-gray-600 truncate flex-1">{item.input.slice(0, 100)}...</p>
            <span className="text-2xl font-bold text-blue-600 ml-4">{item.score}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
```

`App.jsx`'e route ekle:

```jsx
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import HomePage from './pages/HomePage';
import HistoryPage from './pages/HistoryPage';
import Layout from './components/Layout';

export default function App() {
  return (
    <BrowserRouter>
      <Layout>
        <nav className="flex gap-4 mb-6">
          <Link to="/" className="text-blue-600 hover:underline">Analiz</Link>
          <Link to="/history" className="text-blue-600 hover:underline">Geçmiş</Link>
        </nav>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/history" element={<HistoryPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}
```

**Final commit:**
```bash
git add frontend/
git commit -m "feat(fe): edge case'ler + geçmiş sayfası"
git push
```

---

### 🟢 [3:00 – 4:00] Entegrasyon + Sunum Hazırlığı

**Hedef:** main üzerinde her şey güzel görünüyor.

```bash
# FS merge yaptıktan sonra
git checkout main
git pull origin main

cd frontend && npm run dev
```

#### Demo için son düzeltmeler:
- [ ] Tarayıcıyı temizle (Ctrl+Shift+R)
- [ ] Tam ekran aç (F11)
- [ ] Etkileyici test girdileri hazırla:
  - 1. Normal CV → yüksek skor
  - 2. Kötü CV → düşük skor
  - 3. Alakasız metin → score 0 + fallback mesajı
- [ ] Her senaryoyu 1 kez dene, çalıştığını doğrula
- [ ] Backend kapalıyken arayüzün nasıl davrandığını da gör

#### Sunum Notları (2 dakika)

> "Arayüzü React + Vite + Tailwind ile yaptık. Hız ve şıklık için bu üçlü ideal.
>
> [Canlı demo yap]
> - Buraya CV yapıştırıyorum
> - Pozisyonu giriyorum
> - 'Değerlendir'e bastığımda backend'e gidiyor, AI yanıtı geliyor
> - Skor, özet, detaylar ekranda
>
> Edge case yönetimi: Boş girdi engelleniyor. Backend ulaşılamazsa anlamlı hata gösteriyor.
> Mobil görünüm de düzenli, [DevTools'ta telefon ikonuna basıp göster]."

---

## 9. SENIN: Sık Karşılaştığın Hatalar

### Hata 1: API çağrısı 404 dönüyor

**Belirti:** Network'te `/api/analyze` 404 hatası.

**Çözüm:**
- Backend çalışıyor mu? (Backend'ciye sor)
- `frontend/vite.config.js`'de proxy ayarı var mı?
  ```javascript
  proxy: {
    '/api': { target: 'http://localhost:3001', changeOrigin: true }
  }
  ```
- Vite'i yeniden başlat (`Ctrl+C`, `npm run dev`)

---

### Hata 2: CORS hatası

**Belirti:** Console'da:
```
Access-Control-Allow-Origin error
```

**Çözüm:** Backend tarafı sorunu, Backend'ciye söyle. `cors()` middleware'i çağırması lazım.

---

### Hata 2.1: Java backend çalışmıyor

**Belirti:** API çağrısı 502/503 veya "connection refused"

**Çözüm:**
- Backend ekibinden kontrol iste: `mvn spring-boot:run` çalışıyor mu?
- Port 3001 dinleniyor mu? `curl http://localhost:3001/health` ile dene
- Genelde `ANTHROPIC_API_KEY` env variable eksik olur — Backend'ciye sor

---

### Hata 3: Tailwind class'ları çalışmıyor

**Belirti:** `className="bg-blue-500"` yazıyorum ama mavi olmuyor.

**Çözüm:**
1. `frontend/src/index.css`'de Tailwind import var mı?
   ```css
   @tailwind base;
   @tailwind components;
   @tailwind utilities;
   ```
2. `frontend/src/main.jsx`'te `import './index.css'` var mı?
3. `frontend/tailwind.config.js`'de content path doğru mu?
   ```javascript
   content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}']
   ```
4. Vite'i restart et.

---

### Hata 4: State güncellemiyor

**Belirti:** `setResult(...)` çağırdım ama ekran değişmiyor.

**Çözüm:**
- `setResult`'tan sonra console.log ile dönüş değerini gör
- React DevTools'ta state'i incele
- Async fonksiyonda state'i mutasyona uğratıyor olabilirsin:
  ```jsx
  // Yanlış:
  result.score = 100; setResult(result);

  // Doğru:
  setResult({ ...result, score: 100 });
  ```

---

### Hata 5: Sayfa boş açılıyor

**Belirti:** http://localhost:5173 beyaz ekran, console'da hata.

**Çözüm:**
- Console hatasını oku — genelde syntax error
- `npm run dev` çıktısında hata var mı?
- `main.jsx` ve `App.jsx`'i kontrol et
- React import ediyor musun? `import React from 'react'` (React 18'de gerek yok ama bazı bağlamlarda lazım)

---

### Hata 6: Form submit edince sayfa yenileniyor

**Belirti:** Butona basıyorum, ekran flash ediyor, sonuç görünmüyor.

**Çözüm:** Form'un default davranışını engelle:
```jsx
async function handleSubmit(e) {
  e.preventDefault();   // ← bu satır kritik
  // ... rest
}
```

---

### Hata 7: Axios "Network Error"

**Belirti:** API çağrısında "Network Error".

**Çözüm:**
- Backend çalışıyor mu?
- `baseURL: '/api'` mı yoksa `http://localhost:3001/api` mı? Genelde proxy ile `/api` yeter
- Tarayıcı DevTools → Network → request'in URL'ini kontrol et

---

## 10. SENIN: Demo Hazırlığı

### Sunumda göstereceğin akış (2 dakika):

1. **Anasayfa açıklama (15 sn)**
   > "Burada kullanıcı [INPUT TÜRÜ]'nü giriyor"

2. **Canlı demo — başarı (45 sn)**
   - Etkileyici bir test girdisi yapıştır
   - Submit et
   - Loading'i göster
   - Sonuç kartının nasıl şık geldiğini göster

3. **Edge case (30 sn)**
   - Boş gönder → validation çalışıyor
   - Alakasız girdi → fallback mesaj

4. **Geçmiş sayfası (15 sn) — eğer yaptıysan**
   - "Tüm önceki analizleri burada saklıyoruz"

5. **Mobil görünüm (15 sn)**
   - DevTools telefon ikonu
   - Tasarımın bozulmadığını göster

### Soru-cevap için hazırlık:
- **"Neden React?"** → 4 saatlik MVP için en hızlı, ekibin bildiği framework
- **"Neden Tailwind?"** → CSS yazmadan hızlı, modern görünüm. Hackathon hızı için ideal.
- **"State management?"** → useState yeterli. Karmaşık olsa Zustand veya Context kullanırdık.
- **"Erişilebilirlik (a11y)?"** → Semantic HTML, label'lar var. Geliştirilebilir.
- **"Mobil app yapacak mısınız?"** → React Native ile aynı stack mantığı, kolay port.

---

## 11. ORTAK: Git İş Akışı

### Branch Stratejisi
```
main                           ← FS yöneticisi merge eder
├── feat/ai
├── feat/backend
└── feat/frontend              ← SENİN BRANCH'İN
```

### Senin Günlük Akış
```bash
# Sabah
git checkout feat/frontend
git pull origin feat/frontend

# Çalışırken her ~30 dk
git add frontend/
git commit -m "feat(fe): [ne değişti]"
git push

# main'den güncelleme al (FS merge yaptıktan sonra)
git checkout feat/frontend
git merge main
```

### Commit Mesaj Formatı
```
feat(fe): ana sayfa UI iskeleti
feat(fe): sonuç kartı bileşeni
fix(fe): mobile'da buton taşıyordu
docs(fe): README ekran görüntüleri
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
| Mock UI hazır | FS | "Mock veriyle ana sayfa hazır, backend bekliyorum" |
| Backend'e bağlandım | Backend, FS | "API entegrasyonu bitti, /api/analyze çağırıyorum" |
| Yeni alan/endpoint istiyorum | AI Uzmanı veya Backend | "Sözleşmeye `confidence` alanı eklenebilir mi?" |
| Hata aldım | Backend | "POST /api/analyze 500 dönüyor, [hata mesajı]" |
| Tıkandım | Tüm ekip | "Tailwind class çalışmıyor, 10 dk içinde çözeceğim" |

### Sana Bildirilenler
- AI Uzmanı: "Sözleşme hazır: `score, summary, verdict, details`"
- Backend: "POST /api/analyze hazır, body `{input}`, response `{success, data}`"
- FS: "Merge yapıyorum, 2 dk commit atma"

### Saatlik Sync (FS yönetir)
Her saat 2 dakika dur:
- Ne yaptın
- 30 dk sonra ne yapacaksın
- Tıkanmış mısın

---

## 13. ORTAK: Acil Durum

### Senaryo 1: Backend hiç çalışmıyor
**Çözüm:**
- Mock veriyle UI'ı geliştirmeye devam et
- Backend'ciye yardıma git
- Son çare: Anthropic API'sini direkt frontend'den çağır (CORS sorunu olur, anahtar açığa çıkar — sadece son çare)

### Senaryo 2: Build hatası, sayfa açılmıyor
**Çözüm:**
```bash
# Cache temizle
rm -rf node_modules .vite
npm install
npm run dev
```

### Senaryo 3: Tasarımım acayip görünüyor demo öncesi
**Çözüm:**
- Tüm padding'leri standartlaştır: `p-6` veya `p-8`
- Renkleri sınırla: gri tonlar + 1 ana renk (mavi/yeşil)
- Border-radius tutarlı: `rounded-lg` her yerde
- Shadow ekle: `shadow-sm` veya `shadow`
- Inter font kullan (Tailwind default zaten okunaklı)

### Senaryo 4: Mobile'da bozuk
**Çözüm:**
- `flex` ve `grid`'i mobile-first yaz
- `md:` prefix'iyle desktop override
- `w-full` mobile, `md:w-1/2` desktop
- DevTools'ta sürekli telefon viewport'ta test

### Senaryo 5: Production'a build alıp deploy edemiyorum
**Çözüm:** Hackathon'da deploy gerekli mi? Genelde sadece git push yeterli. Eğer gerekiyorsa:
```bash
npm run build
# dist/ klasörü oluşur
# Vercel, Netlify, GitHub Pages — herhangi biri
```

---

## 14. ORTAK: Final Teslim Checklist

3:45'ten önce:

- [ ] `feat/frontend` branch'in `main`'e merge edilmiş
- [ ] `main` üzerinde npm install + npm run dev sorunsuz çalışıyor
- [ ] Tarayıcıda tüm sayfalar açılıyor
- [ ] Tüm test girdileri ile akış başarılı
- [ ] Mobil viewport'ta düzenli
- [ ] `package-lock.json` git'te var (build'in tutarlı olması için)

### Senin son commit'in
```bash
git checkout feat/frontend
git status
git log --oneline -5
git push origin feat/frontend
```

---

## 15. Hızlı Referans

### Tailwind sık kullanılan sınıflar
```
Spacing:    p-4, m-6, gap-3, space-y-4
Layout:     flex, grid, items-center, justify-between
Color:      bg-blue-600, text-gray-700, border-gray-200
Border:     rounded-lg, border, border-2
Shadow:     shadow-sm, shadow, shadow-md
Font:       text-sm, text-lg, font-medium, font-bold
Hover:      hover:bg-blue-700, hover:underline
Disabled:   disabled:opacity-50, disabled:cursor-not-allowed
Responsive: md:flex, lg:grid-cols-3
Transition: transition-colors, duration-200
```

### React pattern'ler

#### Form state
```jsx
const [input, setInput] = useState('');
<input value={input} onChange={(e) => setInput(e.target.value)} />
```

#### Async submit
```jsx
async function handleSubmit(e) {
  e.preventDefault();
  setLoading(true);
  try {
    const res = await analyze(input);
    setResult(res.data);
  } catch (err) {
    setError(err.message);
  } finally {
    setLoading(false);
  }
}
```

#### Conditional render
```jsx
{loading && <Spinner />}
{error && <ErrorMessage>{error}</ErrorMessage>}
{result && <ResultCard data={result} />}
```

#### useEffect data fetch
```jsx
useEffect(() => {
  getResults().then((res) => setItems(res.data));
}, []);
```

### `/ship` komutu (Claude Code'da)
```
/ship frontend/src/pages/HistoryPage.jsx oluştur, geçmiş analizleri liste halinde göster
```

---

**Başarılar! Jürinin gördüğü her şey senin elinden çıkıyor. 🎨**
