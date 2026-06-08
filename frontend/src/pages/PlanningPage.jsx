import { useState, useEffect } from 'react';
import { getBacklog, getVelocity, predictSize, getBlockerSuggestion } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';

const CONFIDENCE_COLOR = { HIGH: 'bg-green-100 text-green-700', MEDIUM: 'bg-yellow-100 text-yellow-700', LOW: 'bg-red-100 text-red-700' };
const PRIORITY_COLOR = { High: 'text-red-500', Highest: 'text-red-700', Medium: 'text-yellow-500', Low: 'text-gray-400', Lowest: 'text-gray-300' };

export default function PlanningPage() {
  const [backlog, setBacklog] = useState([]);
  const [velocity, setVelocity] = useState(null);
  const [selected, setSelected] = useState(new Set());
  const [predictions, setPredictions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingInit, setLoadingInit] = useState(true);
  const [error, setError] = useState('');

  // Blocker paneli
  const [blockerIssue, setBlockerIssue] = useState(null);
  const [blockerReason, setBlockerReason] = useState('');
  const [blocker, setBlocker] = useState(null);
  const [loadingBlocker, setLoadingBlocker] = useState(false);

  useEffect(() => {
    Promise.all([getBacklog(), getVelocity()])
      .then(([bl, v]) => {
        setBacklog(bl.data || []);
        setVelocity(v.data || null);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoadingInit(false));
  }, []);

  function toggleSelect(key) {
    setSelected((prev) => {
      const next = new Set(prev);
      next.has(key) ? next.delete(key) : next.add(key);
      return next;
    });
  }

  async function handlePredict() {
    if (selected.size === 0) return;
    setLoading(true); setError(''); setPredictions([]);
    try {
      const res = await predictSize([...selected]);
      setPredictions(res.data?.predictions || []);
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  }

  async function handleBlocker(issue) {
    setBlockerIssue(issue); setBlocker(null); setBlockerReason('');
  }

  async function submitBlocker() {
    if (!blockerReason.trim() || !blockerIssue) return;
    setLoadingBlocker(true); setBlocker(null);
    try {
      const res = await getBlockerSuggestion(blockerIssue.key, blockerIssue.summary, blockerIssue.description, blockerReason);
      setBlocker(res.data);
    } catch (e) { setError(e.message); }
    finally { setLoadingBlocker(false); }
  }

  if (loadingInit) return <div className="flex justify-center py-20"><LoadingSpinner /></div>;

  return (
    <div className="space-y-6">

      {/* Velocity Özeti */}
      {velocity && (
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <h2 className="text-base font-semibold text-gray-800 mb-3">Takım Velocity</h2>
          <div className="flex items-center gap-6">
            <div className="text-center">
              <div className="text-3xl font-bold text-blue-600">{velocity.averageVelocity}</div>
              <div className="text-xs text-gray-500 mt-1">Ort. Puan/Sprint</div>
            </div>
            <div className={`px-3 py-1 rounded-full text-sm font-medium ${
              velocity.trend === 'improving' ? 'bg-green-100 text-green-700' :
              velocity.trend === 'declining' ? 'bg-red-100 text-red-700' :
              'bg-gray-100 text-gray-600'}`}>
              {velocity.trend === 'improving' ? '↑ Artıyor' : velocity.trend === 'declining' ? '↓ Düşüyor' : '→ Stabil'}
            </div>
            <div className="flex gap-3 overflow-x-auto">
              {velocity.sprints?.map((s) => (
                <div key={s.sprintId} className="text-xs text-center min-w-[70px]">
                  <div className="text-gray-500 truncate">{s.name}</div>
                  <div className="font-semibold text-gray-800">{s.completedPoints}/{s.committedPoints}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Hata */}
      {error && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{error}</div>
      )}

      {/* Backlog Tablosu */}
      <div className="bg-white rounded-xl border border-gray-200">
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-100">
          <h2 className="text-base font-semibold text-gray-800">Backlog ({backlog.length} görev)</h2>
          <button
            onClick={handlePredict}
            disabled={selected.size === 0 || loading}
            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? 'Tahmin yapılıyor…' : `${selected.size} görevi tahmin et`}
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-xs text-gray-500 uppercase">
              <tr>
                <th className="px-4 py-3 w-8"></th>
                <th className="px-4 py-3 text-left">Anahtar</th>
                <th className="px-4 py-3 text-left">Başlık</th>
                <th className="px-4 py-3 text-left">Tip</th>
                <th className="px-4 py-3 text-left">Öncelik</th>
                <th className="px-4 py-3 text-center">SP</th>
                <th className="px-4 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {backlog.map((issue) => (
                <tr key={issue.key} className={`hover:bg-gray-50 transition-colors ${selected.has(issue.key) ? 'bg-blue-50' : ''}`}>
                  <td className="px-4 py-3">
                    <input type="checkbox" checked={selected.has(issue.key)}
                      onChange={() => toggleSelect(issue.key)}
                      className="rounded border-gray-300 text-blue-600 focus:ring-blue-500" />
                  </td>
                  <td className="px-4 py-3 font-mono text-blue-600 font-medium">{issue.key}</td>
                  <td className="px-4 py-3 text-gray-800 max-w-xs truncate">{issue.summary}</td>
                  <td className="px-4 py-3">
                    <span className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">{issue.issueType}</span>
                  </td>
                  <td className={`px-4 py-3 font-medium ${PRIORITY_COLOR[issue.priority] || 'text-gray-500'}`}>
                    {issue.priority}
                  </td>
                  <td className="px-4 py-3 text-center">
                    {issue.storyPoints != null
                      ? <span className="font-semibold text-gray-800">{issue.storyPoints}</span>
                      : <span className="text-gray-400">—</span>}
                  </td>
                  <td className="px-4 py-3">
                    <button onClick={() => handleBlocker(issue)}
                      className="text-xs text-orange-500 hover:text-orange-700 font-medium">
                      Bloker?
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {backlog.length === 0 && (
            <div className="text-center py-12 text-gray-400">Backlog boş veya yüklenemedi.</div>
          )}
        </div>
      </div>

      {/* Tahmin Sonuçları */}
      {loading && <div className="flex justify-center py-6"><LoadingSpinner /></div>}
      {predictions.length > 0 && (
        <div className="bg-white rounded-xl border border-gray-200">
          <div className="px-5 py-4 border-b border-gray-100">
            <h2 className="text-base font-semibold text-gray-800">AI Tahmin Sonuçları</h2>
          </div>
          <div className="divide-y divide-gray-100">
            {predictions.map((p) => (
              <div key={p.key} className="px-5 py-4 flex items-start gap-4">
                <div className="min-w-[90px]">
                  <div className="font-mono text-blue-600 font-medium text-sm">{p.key}</div>
                  <div className="flex items-center gap-1 mt-2">
                    {p.currentStoryPoints != null && (
                      <span className="text-xs text-gray-400 line-through">{p.currentStoryPoints}</span>
                    )}
                    <span className="text-2xl font-bold text-gray-900">{p.predictedStoryPoints}</span>
                    <span className="text-xs text-gray-500">SP</span>
                  </div>
                </div>
                <div className="flex-1">
                  <div className="text-sm font-medium text-gray-800">{p.summary}</div>
                  <div className="text-xs text-gray-500 mt-1">{p.rationale}</div>
                </div>
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${CONFIDENCE_COLOR[p.confidence] || 'bg-gray-100 text-gray-600'}`}>
                  {p.confidence}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Bloker Paneli */}
      {blockerIssue && (
        <div className="bg-white rounded-xl border border-orange-200 shadow-sm">
          <div className="px-5 py-4 border-b border-orange-100 flex items-center justify-between">
            <div>
              <h3 className="text-base font-semibold text-gray-800">Bloker Analizi</h3>
              <div className="text-sm text-gray-500 mt-0.5">{blockerIssue.key} — {blockerIssue.summary}</div>
            </div>
            <button onClick={() => setBlockerIssue(null)} className="text-gray-400 hover:text-gray-600 text-xl leading-none">×</button>
          </div>
          <div className="p-5 space-y-3">
            <textarea
              value={blockerReason}
              onChange={(e) => setBlockerReason(e.target.value)}
              rows={2}
              placeholder="Bloker nedeni (ör. API dökümanı eksik, bağımlı servis hazır değil…)"
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400 resize-none"
            />
            <button onClick={submitBlocker} disabled={!blockerReason.trim() || loadingBlocker}
              className="px-4 py-2 bg-orange-500 text-white text-sm font-medium rounded-lg hover:bg-orange-600 disabled:opacity-40 transition-colors">
              {loadingBlocker ? 'Analiz ediliyor…' : 'AI Çözüm Öner'}
            </button>
          </div>
          {loadingBlocker && <div className="flex justify-center pb-4"><LoadingSpinner /></div>}
          {blocker && (
            <div className="px-5 pb-5 space-y-3">
              <div className="rounded-lg bg-red-50 border border-red-100 px-4 py-3">
                <div className="text-xs font-semibold text-red-600 mb-1">Kök Neden</div>
                <div className="text-sm text-red-800">{blocker.rootCause}</div>
              </div>
              <div className="rounded-lg bg-green-50 border border-green-100 px-4 py-3">
                <div className="text-xs font-semibold text-green-700 mb-2">Öneriler</div>
                <ul className="space-y-1">
                  {blocker.suggestions?.map((s, i) => (
                    <li key={i} className="text-sm text-green-800 flex gap-2"><span className="text-green-500">•</span>{s}</li>
                  ))}
                </ul>
              </div>
              <div className="rounded-lg bg-blue-50 border border-blue-100 px-4 py-3">
                <div className="text-xs font-semibold text-blue-600 mb-1">Önerilen Eylem</div>
                <div className="text-sm text-blue-800 font-medium">{blocker.recommendedAction}</div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
