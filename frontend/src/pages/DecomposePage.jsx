import { useState, useEffect } from 'react';
import { getBacklog, decomposeAndAssign } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';

const DISCIPLINE_COLOR = {
  FRONTEND: 'bg-purple-100 text-purple-700',
  BACKEND:  'bg-blue-100 text-blue-700',
  DB:       'bg-yellow-100 text-yellow-700',
  TEST:     'bg-green-100 text-green-700',
  DEVOPS:   'bg-gray-100 text-gray-700',
};

const SCORE_COLOR = (s) => s >= 80 ? 'text-green-600' : s >= 60 ? 'text-yellow-600' : 'text-red-500';

export default function DecomposePage() {
  const [backlog, setBacklog] = useState([]);
  const [selectedKey, setSelectedKey] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingInit, setLoadingInit] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getBacklog()
      .then((res) => setBacklog(res.data || []))
      .catch((e) => setError(e.message))
      .finally(() => setLoadingInit(false));
  }, []);

  const selectedIssue = backlog.find((i) => i.key === selectedKey);

  async function handleDecompose() {
    if (!selectedIssue) return;
    setLoading(true); setError(''); setResult(null);
    try {
      const res = await decomposeAndAssign(
        selectedIssue.key,
        selectedIssue.summary,
        selectedIssue.description,
        selectedIssue.storyPoints
      );
      setResult(res.data);
    } catch (e) { setError(e.message); }
    finally { setLoading(false); }
  }

  // Atama haritası: tempId → assignment
  const assignMap = result?.assignments?.reduce((acc, a) => {
    acc[a.tempId] = a; return acc;
  }, {}) || {};

  if (loadingInit) return <div className="flex justify-center py-20"><LoadingSpinner /></div>;

  return (
    <div className="space-y-6">
      {error && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">{error}</div>
      )}

      {/* Görev Seç */}
      <div className="bg-white rounded-xl border border-gray-200 p-5">
        <h2 className="text-base font-semibold text-gray-800 mb-4">Görev Seç ve Kır</h2>
        <div className="flex gap-3 items-end flex-wrap">
          <div className="flex-1 min-w-[260px]">
            <label className="block text-xs font-medium text-gray-600 mb-1.5">Backlog'dan Görev</label>
            <select
              value={selectedKey}
              onChange={(e) => setSelectedKey(e.target.value)}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
            >
              <option value="">— Görev seçin —</option>
              {backlog.map((i) => (
                <option key={i.key} value={i.key}>
                  [{i.key}] {i.summary} {i.storyPoints ? `(${i.storyPoints} SP)` : ''}
                </option>
              ))}
            </select>
          </div>
          <button
            onClick={handleDecompose}
            disabled={!selectedKey || loading}
            className="px-5 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors whitespace-nowrap"
          >
            {loading ? 'Kırılıyor…' : 'Alt Görevlere Böl + Ata'}
          </button>
        </div>

        {/* Seçili görev önizleme */}
        {selectedIssue && (
          <div className="mt-4 p-3 bg-gray-50 rounded-lg border border-gray-200 text-sm">
            <div className="flex items-center gap-2">
              <span className="font-mono font-medium text-blue-600">{selectedIssue.key}</span>
              <span className="text-gray-800 font-medium">{selectedIssue.summary}</span>
              {selectedIssue.storyPoints && (
                <span className="ml-auto px-2 py-0.5 bg-blue-100 text-blue-700 rounded text-xs font-medium">{selectedIssue.storyPoints} SP</span>
              )}
            </div>
            {selectedIssue.description && (
              <p className="text-gray-500 mt-2 line-clamp-2">{selectedIssue.description}</p>
            )}
          </div>
        )}
      </div>

      {loading && <div className="flex justify-center py-8"><LoadingSpinner /></div>}

      {/* Sonuç */}
      {result && (
        <div className="space-y-4">
          <div className="flex items-center gap-2">
            <h2 className="text-base font-semibold text-gray-800">
              {result.parentKey} — Alt Görevler ve Atamalar
            </h2>
            <span className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs">
              {result.subtasks?.length} alt görev
            </span>
          </div>

          <div className="grid grid-cols-1 gap-3">
            {result.subtasks?.map((st) => {
              const assign = assignMap[st.tempId];
              return (
                <div key={st.tempId} className="bg-white rounded-xl border border-gray-200 p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${DISCIPLINE_COLOR[st.discipline] || 'bg-gray-100 text-gray-600'}`}>
                          {st.discipline}
                        </span>
                        <span className="text-sm font-medium text-gray-800">{st.title}</span>
                        <span className="ml-auto text-xs text-gray-400">{st.estimateHours}h</span>
                      </div>
                      {st.description && (
                        <p className="text-xs text-gray-500 mt-2">{st.description}</p>
                      )}
                    </div>
                  </div>

                  {/* Atama Önerisi */}
                  {assign && (
                    <div className="mt-3 pt-3 border-t border-gray-100 flex items-center justify-between flex-wrap gap-2">
                      <div className="flex items-center gap-2">
                        <div className="w-7 h-7 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs font-bold">
                          {assign.suggestedAssignee?.name?.[0]}
                        </div>
                        <div>
                          <div className="text-sm font-medium text-gray-800">{assign.suggestedAssignee?.name}</div>
                          <div className="text-xs text-gray-400">{assign.reason}</div>
                        </div>
                      </div>
                      <div className="flex items-center gap-3">
                        <div className="text-right">
                          <div className={`text-lg font-bold ${SCORE_COLOR(assign.matchScore)}`}>
                            {assign.matchScore}
                          </div>
                          <div className="text-xs text-gray-400">uyum skoru</div>
                        </div>
                        <div className="text-right text-xs text-gray-500">
                          <div>{assign.memberLoadAfterHours}/{assign.memberCapacityHours}h</div>
                          <div>yük</div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
}
