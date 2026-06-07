const Anthropic = require('@anthropic-ai/sdk');

const client = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });
const MODEL = 'claude-sonnet-4-6';

// ─── SYSTEM PROMPT ─────────────────────────────────────────────────────────
// Edit this in AI_CONTEXT/3_ai_prompts.md, then paste the final version here.
const SYSTEM_PROMPT = `You are a helpful AI assistant in a hackathon demo application.

Analyze the user's input and respond with valid JSON in this exact structure:
{
  "summary": "string — concise summary of your analysis",
  "insights": ["string", "string"],
  "score": number between 0 and 100,
  "recommendation": "string — clear next step"
}

Rules:
- Respond ONLY with the JSON object above. No markdown, no code fences, no extra text.
- If input is unclear, set score to 0 and explain in summary.`;
// ───────────────────────────────────────────────────────────────────────────

const FALLBACK_RESPONSE = {
  summary: 'Analysis temporarily unavailable. Please try again.',
  insights: [],
  score: 0,
  recommendation: 'Retry in a few seconds.',
};

async function analyze(userInput) {
  try {
    const message = await client.messages.create({
      model: MODEL,
      max_tokens: 1024,
      system: SYSTEM_PROMPT,
      messages: [{ role: 'user', content: String(userInput) }],
    });

    const text = message.content[0].text.trim();
    return JSON.parse(text);
  } catch (err) {
    console.error('[aiService] analyze error:', err.message);
    return FALLBACK_RESPONSE;
  }
}

async function analyzeWithContext(userInput, context) {
  try {
    const userMessage = `CONTEXT:\n---\n${context}\n---\n\nINPUT: ${userInput}`;
    const message = await client.messages.create({
      model: MODEL,
      max_tokens: 1024,
      system: SYSTEM_PROMPT,
      messages: [{ role: 'user', content: userMessage }],
    });

    const text = message.content[0].text.trim();
    return JSON.parse(text);
  } catch (err) {
    console.error('[aiService] analyzeWithContext error:', err.message);
    return FALLBACK_RESPONSE;
  }
}

module.exports = { analyze, analyzeWithContext };
