# API CONTRACT — AI-Powered Agile Manager
# FROZEN: Bu dosya iki ekibin ortak anayasasıdır.
# Değişiklik yapmadan önce backend VE frontend sorumlusuna haber ver.

---

## Ortak Kurallar (DEĞİŞMEZ)

| Kural | Detay |
|-------|-------|
| **Zarf** | Tüm yanıtlar `ApiResponse<T>` ile sarılır: `{ "success": true, "data": {...}, "error": null }` |
| **Hata** | `{ "success": false, "data": null, "error": "Türkçe mesaj" }` |
| **Alan adı** | Tüm JSON alanları `camelCase` (Jackson varsayılanı) |
| **Auth (Jira)** | `Authorization: Bearer <token>` — sadece backend kullanır, frontend görmez |
| **Jira yazma** | **YOK** — Jira'ya sadece GET yapılır |
| **Fallback** | Her AI çağrısı exception'da statik fallback döner; uygulama asla boş ekran göstermez |
| **Base URL** | `http://localhost:3001/api` (dev) — frontend `vite.config.js` proxy üzerinden `/api` prefix ile çağırır |

### Ortak Enum Değerleri

```
discipline   : FRONTEND | BACKEND | DB | TEST | DEVOPS
statusCategory: TODO | IN_PROGRESS | DONE
sprintState  : active | closed | future
confidence   : HIGH | MEDIUM | LOW
issueType    : Story | Task | Bug | Epic
priority     : Highest | High | Medium | Low | Lowest
impact       : positive | neutral | negative
```

### Mock Dosya Konumları

Her endpoint'in `data` payload'u `contract/mocks/` klasöründedir.
Frontend bu JSON'ları `VITE_USE_MOCKS=true` ile doğrudan kullanır.
Backend bu JSON'ların şekline birebir uyan yanıt üretir.

---

## Genel Endpoint'ler

### GET /api/health
Sistem sağlık kontrolü.

**Yanıt `data`:**
```json
{ "status": "ok" }
```
Mock: `contract/mocks/health.json`

---

## Jira Okuma Endpoint'leri

> Tüm Jira endpoint'leri read-only. Backend Jira'dan veriyi çekip sözleşme DTO'larına mapler.

### GET /api/jira/sprints
Board'daki tüm sprint'leri döner (aktif + kapalı + gelecek).

**Yanıt `data`:** `SprintDto[]`
```json
[
  {
    "id": 101,
    "name": "Sprint 12",
    "state": "closed",
    "startDate": "2026-05-01",
    "endDate": "2026-05-14",
    "goal": "Ödeme akışını tamamla ve kullanıcı profili API'lerini yayınla"
  }
]
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `id` | integer | Jira sprint ID |
| `name` | string | Sprint adı |
| `state` | string | `active \| closed \| future` |
| `startDate` | string (ISO date) | Başlangıç tarihi, nullable |
| `endDate` | string (ISO date) | Bitiş tarihi, nullable |
| `goal` | string | Sprint hedefi, nullable |

Mock: `contract/mocks/jira-sprints.json`

---

### GET /api/jira/backlog
Board'un backlog'undaki (sprint'e atanmamış) tüm issue'lar.

**Yanıt `data`:** `IssueDto[]`
```json
[
  {
    "key": "PROJ-201",
    "summary": "Kullanıcı şifre sıfırlama e-posta akışı",
    "issueType": "Story",
    "status": "To Do",
    "statusCategory": "TODO",
    "storyPoints": null,
    "assignee": null,
    "priority": "High",
    "labels": ["auth", "email"],
    "description": "Kullanıcı şifresini unuttuğunda..."
  }
]
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `key` | string | Jira issue key (ör. PROJ-201) |
| `summary` | string | Kısa başlık |
| `issueType` | string | `Story \| Task \| Bug \| Epic` |
| `status` | string | Jira status adı (ham) |
| `statusCategory` | string | `TODO \| IN_PROGRESS \| DONE` |
| `storyPoints` | integer \| null | Story point, atanmamışsa null |
| `assignee` | string \| null | Atanmış kişi adı, yoksa null |
| `priority` | string | Jira önceliği |
| `labels` | string[] | Jira etiketleri |
| `description` | string \| null | Açıklama metni |

Mock: `contract/mocks/jira-backlog.json`

---

### GET /api/jira/sprints/{sprintId}/issues
Belirtilen sprint'e ait tüm issue'lar.

**Path Param:** `sprintId` — integer

**Yanıt `data`:** `SprintIssueDto[]` (IssueDto + iki ek alan)
```json
[
  {
    "key": "PROJ-181",
    "summary": "Ödeme sayfası checkout formu",
    "issueType": "Story",
    "status": "Done",
    "statusCategory": "DONE",
    "storyPoints": 5,
    "assignee": "Ali",
    "priority": "High",
    "labels": ["payment", "ui"],
    "description": "...",
    "resolved": true,
    "resolvedDate": "2026-05-10"
  }
]
```
| Ek Alan | Tip | Açıklama |
|---------|-----|----------|
| `resolved` | boolean | Tamamlandıysa true |
| `resolvedDate` | string (ISO date) \| null | Kapanış tarihi |

Mock: `contract/mocks/jira-sprint-issues.json`

---

## Epik 1 — Akıllı Planlama (Predictive Planning)

### GET /api/velocity
Son kapalı sprintlerden hesaplanan hız özeti.

**Yanıt `data`:** `VelocityDto`
```json
{
  "averageVelocity": 23.5,
  "trend": "stable",
  "sprints": [
    {
      "sprintId": 101,
      "name": "Sprint 12",
      "committedPoints": 26,
      "completedPoints": 24
    }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `averageVelocity` | number | Son N sprint ortalaması |
| `trend` | string | `improving \| stable \| declining` |
| `sprints[].sprintId` | integer | Sprint ID |
| `sprints[].name` | string | Sprint adı |
| `sprints[].committedPoints` | integer | Sprint başı plan |
| `sprints[].completedPoints` | integer | Sprint sonu tamamlanan |

Mock: `contract/mocks/velocity.json`

---

### POST /api/planning/predict-size
Seçili backlog task'larına AI tahmini yapar (story point önerisi).

**İstek body:**
```json
{ "issueKeys": ["PROJ-201", "PROJ-203", "PROJ-205"] }
```
| Alan | Tip | Zorunlu | Açıklama |
|------|-----|---------|----------|
| `issueKeys` | string[] | Evet | Tahmin istenilen Jira key'leri (min 1) |

**Yanıt `data`:** `SizePredictionResponseDto`
```json
{
  "predictions": [
    {
      "key": "PROJ-201",
      "summary": "Kullanıcı şifre sıfırlama e-posta akışı",
      "currentStoryPoints": null,
      "predictedStoryPoints": 5,
      "confidence": "MEDIUM",
      "rationale": "Benzer auth akışları 4-6 puan arasında tamamlandı..."
    }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `predictions[].key` | string | Jira issue key |
| `predictions[].summary` | string | Issue başlığı |
| `predictions[].currentStoryPoints` | integer \| null | Mevcut atanmış puan |
| `predictions[].predictedStoryPoints` | integer | AI'ın önerdiği puan |
| `predictions[].confidence` | string | `HIGH \| MEDIUM \| LOW` |
| `predictions[].rationale` | string | Tahmin gerekçesi |

Mock: `contract/mocks/planning-predict-size.json`

---

### POST /api/planning/blockers (BONUS)
Bloklanmış bir task için AI destekli çözüm önerisi.

**İstek body:**
```json
{
  "issueKey": "PROJ-205",
  "summary": "Redis önbellek entegrasyonu",
  "description": "...",
  "blockerReason": "Redis altyapısı kurulu değil"
}
```
| Alan | Tip | Zorunlu | Açıklama |
|------|-----|---------|----------|
| `issueKey` | string | Evet | |
| `summary` | string | Evet | Issue başlığı |
| `description` | string | Hayır | Issue açıklaması |
| `blockerReason` | string | Evet | Bloker sebebi (kullanıcı girdisi) |

**Yanıt `data`:** `BlockerSuggestionDto`
```json
{
  "key": "PROJ-205",
  "rootCause": "Redis altyapısının sunucu ortamında kurulu olmaması...",
  "suggestions": [
    "Docker Compose'a Redis servisi ekle...",
    "spring-boot-starter-data-redis bağımlılığını ekle..."
  ],
  "recommendedAction": "DevOps ile önce Docker Compose ortamını hazırlayın..."
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `key` | string | Jira issue key |
| `rootCause` | string | Kök neden analizi |
| `suggestions` | string[] | Adım adım öneriler (2-4 madde) |
| `recommendedAction` | string | En öncelikli önerilen eylem |

Mock: `contract/mocks/planning-blockers.json`

---

## Epik 2 — Görev Kırılımı ve Akıllı Atama

### GET /api/team
Seed takım üyeleri, yetkinlik matrisi ve kapasite.

**Yanıt `data`:** `TeamMemberDto[]`
```json
[
  {
    "memberId": "u1",
    "name": "Ali",
    "skills": {
      "FRONTEND": 4,
      "BACKEND": 2,
      "DB": 3,
      "TEST": 2,
      "DEVOPS": 1
    },
    "capacityHours": 60,
    "currentLoadHours": 18
  }
]
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `memberId` | string | Üye ID (seed'den) |
| `name` | string | Ad |
| `skills` | map<string, integer> | Disiplin → yetkinlik (0-5 skalası) |
| `capacityHours` | integer | Sprint kapasitesi (saat) |
| `currentLoadHours` | integer | Mevcut atanmış yük (saat) |

Mock: `contract/mocks/team.json`

---

### POST /api/tasks/decompose
Bir task'ı teknik alt görevlere böler (AI).

**İstek body:**
```json
{
  "issueKey": "PROJ-201",
  "summary": "Kullanıcı şifre sıfırlama e-posta akışı",
  "description": "...",
  "storyPoints": 5
}
```
| Alan | Tip | Zorunlu | Açıklama |
|------|-----|---------|----------|
| `issueKey` | string | Evet | |
| `summary` | string | Evet | Issue başlığı |
| `description` | string | Hayır | Issue açıklaması |
| `storyPoints` | integer | Hayır | Mevcut puan (bağlam için) |

**Yanıt `data`:** `DecomposeResponseDto`
```json
{
  "parentKey": "PROJ-201",
  "parentSummary": "Kullanıcı şifre sıfırlama e-posta akışı",
  "subtasks": [
    {
      "tempId": "st1",
      "title": "Şifre sıfırlama token üretimi ve DB kaydı",
      "discipline": "BACKEND",
      "estimateHours": 4,
      "description": "UUID token üret, 24 saatlik TTL ile kaydet."
    }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `parentKey` | string | Kaynak issue key |
| `parentSummary` | string | Kaynak issue başlığı |
| `subtasks[].tempId` | string | Geçici ID (st1, st2...) — Jira'ya yazılmaz |
| `subtasks[].title` | string | Alt görev başlığı |
| `subtasks[].discipline` | string | `FRONTEND \| BACKEND \| DB \| TEST \| DEVOPS` |
| `subtasks[].estimateHours` | integer | Tahmini süre (saat) |
| `subtasks[].description` | string | Alt görev açıklaması |

Mock: `contract/mocks/tasks-decompose.json`

---

### POST /api/tasks/assign
Alt görevlere takım kapasite ve yetkinliğine göre kişi önerisi yapar.

**İstek body:**
```json
{
  "subtasks": [
    {
      "tempId": "st1",
      "title": "Şifre sıfırlama token üretimi ve DB kaydı",
      "discipline": "BACKEND",
      "estimateHours": 4
    }
  ]
}
```

**Yanıt `data`:** `AssignResponseDto`
```json
{
  "assignments": [
    {
      "tempId": "st1",
      "discipline": "BACKEND",
      "estimateHours": 4,
      "suggestedAssignee": {
        "memberId": "u2",
        "name": "Veli"
      },
      "matchScore": 92,
      "reason": "BACKEND yetkinliği 5/5, mevcut yük 24/60 saat — kapasite uygun.",
      "memberLoadAfterHours": 28,
      "memberCapacityHours": 60
    }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `assignments[].tempId` | string | `decompose` çıktısındaki tempId ile eşleşir |
| `assignments[].discipline` | string | Alt görev disiplini |
| `assignments[].estimateHours` | integer | Süre tahmini |
| `assignments[].suggestedAssignee.memberId` | string | Önerilen üye ID |
| `assignments[].suggestedAssignee.name` | string | Önerilen üye adı |
| `assignments[].matchScore` | integer (0-100) | Yetkinlik + kapasite uyum skoru |
| `assignments[].reason` | string | Atama gerekçesi |
| `assignments[].memberLoadAfterHours` | integer | Atama sonrası üye yükü |
| `assignments[].memberCapacityHours` | integer | Üye sprint kapasitesi |

Mock: `contract/mocks/tasks-assign.json`

---

### POST /api/tasks/decompose-and-assign  ← Kolaylık endpoint'i
`decompose` + `assign` işlemini tek çağrıda döndürür.

**İstek body:** `decompose` ile aynı.

**Yanıt `data`:**
```json
{
  "parentKey": "...",
  "parentSummary": "...",
  "subtasks": [ /* decompose yanıtı */ ],
  "assignments": [ /* assign yanıtı */ ]
}
```

Mock: `contract/mocks/tasks-decompose-and-assign.json`

---

## Epik 3 — AI Sprint Review ve Yönetici Paneli

### GET /api/sprint/{sprintId}/dashboard
Sprint planlanan vs gerçekleşen metrikleri ve burndown verisi.

**Path Param:** `sprintId` — integer

**Yanıt `data`:** `SprintDashboardDto`
```json
{
  "sprintId": 101,
  "name": "Sprint 12",
  "startDate": "2026-05-01",
  "endDate": "2026-05-14",
  "plannedPoints": 26,
  "completedPoints": 24,
  "plannedCount": 6,
  "completedCount": 5,
  "deviationPercent": -7.7,
  "statusBreakdown": {
    "TODO": 1,
    "IN_PROGRESS": 0,
    "DONE": 5
  },
  "burndown": [
    { "date": "2026-05-01", "remaining": 26 },
    { "date": "2026-05-14", "remaining": 2 }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `plannedPoints` | integer | Sprint başındaki toplam puan |
| `completedPoints` | integer | Tamamlanan puan |
| `plannedCount` | integer | Sprint'e alınan issue sayısı |
| `completedCount` | integer | Tamamlanan issue sayısı |
| `deviationPercent` | number | Sapma % (negatif = altında, pozitif = üstünde) |
| `statusBreakdown` | map | Her kategori için issue sayısı |
| `burndown` | array | Her gün için kalan puan (sadece iş günleri) |

Mock: `contract/mocks/sprint-dashboard.json`

---

### GET /api/sprint/{sprintId}/review
AI tarafından üretilen sprint demo raporu ve özeti.

**Path Param:** `sprintId` — integer

**Yanıt `data`:** `SprintReviewDto`
```json
{
  "sprintId": 101,
  "headline": "Ödeme altyapısını ve kullanıcı profil yönetimini production'a taşıdık",
  "summary": "Sprint 12'de ödeme akışını uçtan uca tamamladık...",
  "achievements": [
    "Stripe API entegrasyonu ve 3D-Secure akışı tamamlandı",
    "24 / 26 story point (%92) teslim edildi"
  ],
  "demoScript": [
    "1. Tarayıcıda ürün listesini açın...",
    "2. Checkout sayfasına geçin..."
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `headline` | string | Tek cümle sprint özeti (jüri slaytı için) |
| `summary` | string | 2-4 cümle ayrıntılı özet |
| `achievements` | string[] | Tamamlanan başarılar listesi |
| `demoScript` | string[] | Adım adım demo senaryosu |

Mock: `contract/mocks/sprint-review.json`

---

### GET /api/sprint/{sprintId}/carryover  (BONUS)
Sprint'ten sonraki sprint'e kalan task'ların geçişkenlik metrikleri.

**Path Param:** `sprintId` — integer

**Yanıt `data`:** `CarryoverDto`
```json
{
  "sprintId": 101,
  "carriedOverCount": 1,
  "carriedOverPoints": 2,
  "items": [
    {
      "key": "PROJ-186",
      "summary": "E2E test: ödeme akışı",
      "storyPoints": 2,
      "sprintsSpilled": 1,
      "lastSprintName": "Sprint 12",
      "reason": "Sprint sonunda tamamlanamadı, Sprint 13'e taşındı."
    }
  ]
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `carriedOverCount` | integer | Kalan issue sayısı |
| `carriedOverPoints` | integer | Kalan toplam puan |
| `items[].sprintsSpilled` | integer | Kaç sprint boyunca taşındığı |

Mock: `contract/mocks/sprint-carryover.json`

---

### GET /api/sprint/{sprintId}/health  (BONUS)
1-100 sprint sağlık skoru ve faktör dökümü.

**Path Param:** `sprintId` — integer

**Yanıt `data`:** `SprintHealthDto`
```json
{
  "sprintId": 101,
  "score": 82,
  "grade": "B+",
  "factors": [
    {
      "name": "Tamamlanma Oranı",
      "value": 92,
      "weight": 0.40,
      "impact": "positive",
      "detail": "24/26 puan teslim edildi"
    }
  ],
  "summary": "Güçlü bir sprint. Ödeme altyapısı zamanında teslim edildi..."
}
```
| Alan | Tip | Açıklama |
|------|-----|----------|
| `score` | integer (1-100) | Genel sağlık skoru |
| `grade` | string | Harf notu (A+/A/B+/B/C/D) |
| `factors[].name` | string | Faktör adı |
| `factors[].value` | number | Faktörün alt skoru (0-100) |
| `factors[].weight` | number | Ağırlık katsayısı (toplam 1.0) |
| `factors[].impact` | string | `positive \| neutral \| negative` |
| `factors[].detail` | string | Kısa açıklama |
| `summary` | string | AI açıklaması |

Mock: `contract/mocks/sprint-health.json`

---

## Sözleşme Değişiklik Protokolü

1. Değişikliği **bu dosyada** ve ilgili `contract/mocks/*.json` içinde güncelle.
2. Backend sorumlusuna: hangi DTO/endpoint değişti, neden.
3. Frontend sorumlusuna: mock JSON güncellemesi, hangi bileşen etkileniyor.
4. Değişikliği aşağıdaki tabloya kaydet.

| Saat | Değişiklik | Kim |
|------|-----------|-----|
| 00:00 | İlk sözleşme oluşturuldu | Contract |

---

## Mock Dosyaları Dizini

| Mock Dosyası | Endpoint |
|---|---|
| `mocks/health.json` | `GET /api/health` |
| `mocks/jira-sprints.json` | `GET /api/jira/sprints` |
| `mocks/jira-backlog.json` | `GET /api/jira/backlog` |
| `mocks/jira-sprint-issues.json` | `GET /api/jira/sprints/{sprintId}/issues` |
| `mocks/velocity.json` | `GET /api/velocity` |
| `mocks/planning-predict-size.json` | `POST /api/planning/predict-size` |
| `mocks/planning-blockers.json` | `POST /api/planning/blockers` |
| `mocks/team.json` | `GET /api/team` |
| `mocks/tasks-decompose.json` | `POST /api/tasks/decompose` |
| `mocks/tasks-assign.json` | `POST /api/tasks/assign` |
| `mocks/tasks-decompose-and-assign.json` | `POST /api/tasks/decompose-and-assign` |
| `mocks/sprint-dashboard.json` | `GET /api/sprint/{sprintId}/dashboard` |
| `mocks/sprint-review.json` | `GET /api/sprint/{sprintId}/review` |
| `mocks/sprint-carryover.json` | `GET /api/sprint/{sprintId}/carryover` |
| `mocks/sprint-health.json` | `GET /api/sprint/{sprintId}/health` |
