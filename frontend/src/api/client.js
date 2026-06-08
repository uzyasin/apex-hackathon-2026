import axios from 'axios';

// ─── MOCK TOGGLE ──────────────────────────────────────────────────────────────
// .env dosyasında VITE_USE_MOCKS=true yaparak backend olmadan geliştir.
// Mock JSON'lar frontend/public/mocks/ klasöründen fetch ile gelir (Vite public dir).
// Kaynak: contract/mocks/ — değişiklik yaparken oradaki dosyaları da güncelle.
const USE_MOCKS = import.meta.env.VITE_USE_MOCKS === 'true';

async function mock(file) {
  const res = await fetch(`/mocks/${file}`);
  if (!res.ok) throw new Error(`Mock yüklenemedi: ${file}`);
  const data = await res.json();
  return { success: true, data };
}
// ─────────────────────────────────────────────────────────────────────────────

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 60000,
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.error || error.message || 'Bilinmeyen hata';
    return Promise.reject(new Error(message));
  }
);

// ─── Genel ───────────────────────────────────────────────────────────────────
export const healthCheck = () =>
  USE_MOCKS ? mock('health.json') : api.get('/health');

// ─── Jira okuma ──────────────────────────────────────────────────────────────
export const getSprints = () =>
  USE_MOCKS ? mock('jira-sprints.json') : api.get('/jira/sprints');

export const getBacklog = () =>
  USE_MOCKS ? mock('jira-backlog.json') : api.get('/jira/backlog');

export const getSprintIssues = (sprintId) =>
  USE_MOCKS ? mock('jira-sprint-issues.json') : api.get(`/jira/sprints/${sprintId}/issues`);

// ─── Epik 1: Akıllı Planlama ─────────────────────────────────────────────────
export const getVelocity = () =>
  USE_MOCKS ? mock('velocity.json') : api.get('/velocity');

export const predictSize = (issueKeys) =>
  USE_MOCKS ? mock('planning-predict-size.json') : api.post('/planning/predict-size', { issueKeys });

export const getBlockerSuggestion = (issueKey, summary, description, blockerReason) =>
  USE_MOCKS ? mock('planning-blockers.json')
    : api.post('/planning/blockers', { issueKey, summary, description, blockerReason });

// ─── Epik 2: Görev Kırılımı ve Akıllı Atama ──────────────────────────────────
export const getTeam = () =>
  USE_MOCKS ? mock('team.json') : api.get('/team');

export const decomposTask = (issueKey, summary, description, storyPoints) =>
  USE_MOCKS ? mock('tasks-decompose.json')
    : api.post('/tasks/decompose', { issueKey, summary, description, storyPoints });

export const assignTasks = (subtasks) =>
  USE_MOCKS ? mock('tasks-assign.json') : api.post('/tasks/assign', { subtasks });

export const decomposeAndAssign = (issueKey, summary, description, storyPoints) =>
  USE_MOCKS ? mock('tasks-decompose-and-assign.json')
    : api.post('/tasks/decompose-and-assign', { issueKey, summary, description, storyPoints });

// ─── Epik 3: Dashboard ve Sprint Review ──────────────────────────────────────
export const getSprintDashboard = (sprintId) =>
  USE_MOCKS ? mock('sprint-dashboard.json') : api.get(`/sprint/${sprintId}/dashboard`);

export const getSprintReview = (sprintId) =>
  USE_MOCKS ? mock('sprint-review.json') : api.get(`/sprint/${sprintId}/review`);

export const getSprintCarryover = (sprintId) =>
  USE_MOCKS ? mock('sprint-carryover.json') : api.get(`/sprint/${sprintId}/carryover`);

export const getSprintHealth = (sprintId) =>
  USE_MOCKS ? mock('sprint-health.json') : api.get(`/sprint/${sprintId}/health`);

// ─── Eski genel endpoint (geriye dönük uyumluluk) ────────────────────────────
export const analyze = (input, context = null) =>
  api.post('/analyze', { input, context });

export default api;
