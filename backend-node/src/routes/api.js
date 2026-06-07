const express = require('express');
const { analyze, analyzeWithContext } = require('../services/aiService');

const router = express.Router();

// POST /api/analyze — main AI endpoint
router.post('/analyze', async (req, res, next) => {
  try {
    const { input, context } = req.body;
    if (!input || typeof input !== 'string') {
      return res.status(400).json({ success: false, error: 'input (string) is required' });
    }

    const result = context
      ? await analyzeWithContext(input, context)
      : await analyze(input);

    res.json({ success: true, data: result });
  } catch (err) {
    next(err);
  }
});

// GET /api/health — quick check
router.get('/health', (req, res) => {
  res.json({ success: true, data: { status: 'ok' } });
});

// ─── ADD NEW ROUTES BELOW ──────────────────────────────────────────────────
// Example pattern:
// router.post('/your-feature', async (req, res, next) => { ... });
// ──────────────────────────────────────────────────────────────────────────

module.exports = router;
