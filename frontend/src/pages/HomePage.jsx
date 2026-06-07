import { useState } from 'react';
import { analyze } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';

export default function HomePage() {
  const [input, setInput] = useState('');
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
      const response = await analyze(input);
      setResult(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-6">
      {/* Input Form */}
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Input
          </label>
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            rows={5}
            className="w-full rounded-lg border border-gray-300 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
            placeholder="Enter your text here..."
          />
        </div>
        <button
          type="submit"
          disabled={loading || !input.trim()}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Analyze
        </button>
      </form>

      {/* Loading */}
      {loading && <LoadingSpinner />}

      {/* Error */}
      {error && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {/* Result */}
      {result && (
        <div className="rounded-lg border border-gray-200 bg-white p-6 space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-lg font-semibold text-gray-900">Result</h2>
            <span className="text-2xl font-bold text-blue-600">{result.score}/100</span>
          </div>

          <p className="text-sm text-gray-600">{result.summary}</p>

          {result.insights?.length > 0 && (
            <div>
              <h3 className="text-sm font-medium text-gray-700 mb-2">Insights</h3>
              <ul className="list-disc list-inside space-y-1">
                {result.insights.map((insight, i) => (
                  <li key={i} className="text-sm text-gray-600">{insight}</li>
                ))}
              </ul>
            </div>
          )}

          {result.recommendation && (
            <div className="rounded-lg bg-blue-50 px-4 py-3">
              <p className="text-sm text-blue-700 font-medium">Recommendation</p>
              <p className="text-sm text-blue-600 mt-1">{result.recommendation}</p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
