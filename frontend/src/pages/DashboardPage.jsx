import { useState, useEffect } from 'react';
import { getSprints, getSprintDashboard, getSprintReview, getSprintCarryover, getSprintHealth } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';

const GRADE_COLOR = { 'A+': 'text-green-600', A: 'text-green-500', 'B+': 'text-blue-600', B: 'text-blue-500', C: 'text-yellow-500', D: 'text-red-500' };
const IMPACT_COLOR = { positive: 'bg-green-50 border-green-200', neutral: 'bg-gray-50 border-gray-200', negative: 'bg-red-50 border-red-200' };

export default function DashboardPage() {
  const [sprints, setSprints] = useState([]);
  const [sprintId, setSprintId] = useState('');
  const [dashboard, setDashboard] = useState(null);
  const [health, setHealth] = useState(null);
  const [carryover, setCarryover] = useState(null);
  const [review, setReview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingReview, setLoadingReview] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    getSprints().then((res) => {
      const closed = (res.data || []).filter((s) => s.state !== 'future');
      setSprints(closed);
      if (closed.length > 0) setSprintId(String(closed[0].id));
    }).catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    if (!sprintId) return;
    setLoading(true); setError('');
    setDashboard(null); setHealth(null); setCarryover(null); setReview(null);
    const id = Number(sprintId);
    Promise.all([
      getSprintDashboard(id),
      getSprintHealth(id),
      getSprintCarryover(id),
    ]).then(([d, h, c]) => {
      setDashboard(d.data);
      setHealth(h.data);
      setCarryover(c.data);
    }).catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [sprintId]);

  async function handleReview() {
    if (!sprintId) return;
    setLoadingReview(true); setReview(null);
    try {
      const res = await getSprintReview(Number(sprintId));
      setReview(res.data);
    } catch (e) { setError(e.message); }
    finally { setLoadingReview(false); }
  }

  const deviation = dashboard?.deviationPercent;
  const burnMax = dashboard?.burndown?.[0]?.remaining || 1;

  return (
    <div className="space-y-6">
      {/* Sprint seçici */}
      <div className="flex items-center gap-4 flex-wrap">
        <div className="flex-1 min-w-[220px]">
          <label className="block text-xs font-medium text-gray-600 mb-1.5">Sprint Seç</label>
          <select
            value={sprintId}
            onChange={(e) => setSprintId(e.target.value)}
            className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {sprints.map((s) => (
              <option key={s.id} value={String(s.id)}>
                {s.name} ({s.state === 'active' ? 'Aktif' : 'Kapalı'})
              </option>
            ))}
          </select>
        </div>
        <button
          onClick={handleReview}
          disabled={!sprintId || loadingReview}
          className="mt-5 px-4 py-2 bg-purple-600 text-white text-sm font-medium rounded-lg hover:bg-purple-700 disabled:opacity-40 transition-colors"
        >
          {loadingReview ? 'Rapor üretiliyor…' : 'AI Sprint Review Üret'}
        </button>
      </div>

      {error && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{error}</div>
      )}

      {loading && <div className="flex justify-center py-16"><LoadingSpinner /></div>}

      {dashboard && !loading && (
        <>
          {/* Metrik Kartları */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Planlanan', value: dashboard.plannedPoints, unit: 'SP', color: 'text-gray-800' },
              { label: 'Tamamlanan', value: dashboard.completedPoints, unit: 'SP', color: 'text-green-600' },
              { label: 'Tamamlanan', value: dashboard.completedCount + '/' + dashboard.plannedCount, unit: 'Görev', color: 'text-blue-600' },
              {
                label: 'Sapma',
                value: (deviation >= 0 ? '+' : '') + deviation + '%',
                unit: '',
                color: deviation >= 0 ? 'text-green-600' : Math.abs(deviation) < 15 ? 'text-yellow-600' : 'text-red-500',
              },
            ].map((m) => (
              <div key={m.label + m.unit} className="bg-white rounded-xl border border-gray-200 p-4 text-center">
                <div className={`text-2xl font-bold ${m.color}`}>{m.value}</div>
                <div className="text-xs text-gray-500 mt-1">{m.unit ? `${m.unit} — ` : ''}{m.label}</div>
              </div>
            ))}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Burndown */}
            <div className="bg-white rounded-xl border border-gray-200 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Burndown</h3>
              <div className="space-y-1.5">
                {dashboard.burndown?.map((pt, i) => {
                  const pct = Math.max(0, Math.round((pt.remaining / burnMax) * 100));
                  return (
                    <div key={i} className="flex items-center gap-2">
                      <div className="text-xs text-gray-400 w-20 shrink-0">{pt.date}</div>
                      <div className="flex-1 bg-gray-100 rounded-full h-3">
                        <div
                          className="bg-blue-500 h-3 rounded-full transition-all"
                          style={{ width: pct + '%' }}
                        />
                      </div>
                      <div className="text-xs font-medium text-gray-700 w-8 text-right">{pt.remaining}</div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Durum Dağılımı */}
            <div className="bg-white rounded-xl border border-gray-200 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Durum Dağılımı</h3>
              <div className="space-y-3">
                {[
                  { key: 'DONE', label: 'Tamamlandı', color: 'bg-green-500' },
                  { key: 'IN_PROGRESS', label: 'Devam Ediyor', color: 'bg-yellow-400' },
                  { key: 'TODO', label: 'Yapılacak', color: 'bg-gray-300' },
                ].map(({ key, label, color }) => {
                  const count = dashboard.statusBreakdown?.[key] || 0;
                  const total = dashboard.plannedCount || 1;
                  const pct = Math.round((count / total) * 100);
                  return (
                    <div key={key}>
                      <div className="flex justify-between text-xs text-gray-600 mb-1">
                        <span>{label}</span>
                        <span>{count} görev ({pct}%)</span>
                      </div>
                      <div className="w-full bg-gray-100 rounded-full h-3">
                        <div className={`${color} h-3 rounded-full`} style={{ width: pct + '%' }} />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Sağlık Skoru */}
          {health && (
            <div className="bg-white rounded-xl border border-gray-200 p-5">
              <div className="flex items-center gap-4 mb-4">
                <div className="text-center">
                  <div className={`text-5xl font-black ${GRADE_COLOR[health.grade] || 'text-gray-800'}`}>
                    {health.score}
                  </div>
                  <div className={`text-lg font-bold ${GRADE_COLOR[health.grade] || 'text-gray-600'}`}>{health.grade}</div>
                  <div className="text-xs text-gray-400">Sprint Sağlığı</div>
                </div>
                <div className="flex-1">
                  <p className="text-sm text-gray-600">{health.summary}</p>
                  <div className="grid grid-cols-2 gap-2 mt-3">
                    {health.factors?.map((f) => (
                      <div key={f.name} className={`rounded-lg border px-3 py-2 text-xs ${IMPACT_COLOR[f.impact] || 'bg-gray-50 border-gray-200'}`}>
                        <div className="font-medium text-gray-700">{f.name}</div>
                        <div className="text-gray-500 mt-0.5">{f.detail}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Carryover */}
          {carryover && carryover.carriedOverCount > 0 && (
            <div className="bg-white rounded-xl border border-gray-200 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-3">
                Sonraki Sprint'e Taşınan
                <span className="ml-2 px-2 py-0.5 bg-red-100 text-red-600 rounded-full text-xs">
                  {carryover.carriedOverCount} görev · {carryover.carriedOverPoints} SP
                </span>
              </h3>
              <div className="space-y-2">
                {carryover.items?.map((item) => (
                  <div key={item.key} className="flex items-center gap-3 py-2 border-b border-gray-100 last:border-0">
                    <span className="font-mono text-xs text-blue-600 font-medium">{item.key}</span>
                    <span className="text-sm text-gray-700 flex-1">{item.summary}</span>
                    {item.storyPoints && (
                      <span className="text-xs bg-gray-100 text-gray-600 px-2 py-0.5 rounded">{item.storyPoints} SP</span>
                    )}
                    <span className="text-xs text-gray-400">{item.sprintsSpilled}. taşma</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </>
      )}

      {/* AI Review */}
      {loadingReview && <div className="flex justify-center py-6"><LoadingSpinner /></div>}
      {review && (
        <div className="bg-white rounded-xl border border-purple-200 p-5">
          <div className="flex items-start gap-3 mb-4">
            <div className="w-8 h-8 rounded-full bg-purple-600 flex items-center justify-center text-white text-sm font-bold shrink-0">AI</div>
            <div>
              <div className="text-base font-bold text-gray-900">{review.headline}</div>
              <div className="text-sm text-gray-600 mt-1">{review.summary}</div>
            </div>
          </div>
          {review.achievements?.length > 0 && (
            <div className="mb-4">
              <div className="text-xs font-semibold text-gray-500 uppercase mb-2">Başarılar</div>
              <ul className="space-y-1">
                {review.achievements.map((a, i) => (
                  <li key={i} className="text-sm text-gray-700 flex gap-2">
                    <span className="text-green-500 font-bold">✓</span>{a}
                  </li>
                ))}
              </ul>
            </div>
          )}
          {review.demoScript?.length > 0 && (
            <div>
              <div className="text-xs font-semibold text-gray-500 uppercase mb-2">Demo Senaryosu</div>
              <ol className="space-y-1">
                {review.demoScript.map((step, i) => (
                  <li key={i} className="text-sm text-gray-700 flex gap-2">
                    <span className="text-purple-500 font-medium shrink-0">{i + 1}.</span>{step.replace(/^\d+\.\s*/, '')}
                  </li>
                ))}
              </ol>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
