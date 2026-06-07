import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
});

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.error || error.message || 'Unknown error';
    return Promise.reject(new Error(message));
  }
);

export const analyze = (input, context = null) =>
  api.post('/analyze', { input, context });

// ─── ADD NEW API CALLS BELOW ───────────────────────────────────────────────
// Pattern: export const yourFeature = (data) => api.post('/your-endpoint', data);
// ──────────────────────────────────────────────────────────────────────────

export default api;
