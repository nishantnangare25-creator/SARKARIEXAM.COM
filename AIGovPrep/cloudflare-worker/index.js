// Cloudflare Worker - AI Backend Proxy with Multi-Provider Cascade Failover
// Handles 2-3 lakh students/day by cascading across all AI providers
// Cascade Order: Groq (5 keys) → Gemini → OpenRouter (Gemini) → OpenRouter (Llama Free)

export default {
  async fetch(request, env) {
    if (request.method === 'OPTIONS') {
      return corsResponse(null, 204);
    }
    if (request.method !== 'POST') {
      return corsResponse(JSON.stringify({ error: 'Method not allowed' }), 405);
    }

    try {
      const body = await request.json();
      const { messages, options = {}, cacheKey } = body;

      if (!messages || !Array.isArray(messages)) {
        return corsResponse(JSON.stringify({ error: 'messages required' }), 400);
      }

      // ===== CACHE CHECK =====
      if (cacheKey && env.QUESTION_CACHE) {
        const cached = await env.QUESTION_CACHE.get(cacheKey, 'json');
        if (cached && cached.questionSets && cached.questionSets.length > 0) {
          const randomSet = cached.questionSets[Math.floor(Math.random() * cached.questionSets.length)];
          console.log(`Cache HIT for ${cacheKey}`);
          return corsResponse(JSON.stringify({ content: randomSet, fromCache: true }), 200);
        }
      }

      // ===== AI CALL with Full Cascade Failover =====
      const result = await callAIWithCascade(messages, options, env);

      // ===== STORE IN CACHE =====
      if (cacheKey && env.QUESTION_CACHE && result) {
        try {
          let isValidForCache = true;
          if (cacheKey.startsWith('mock:') || cacheKey.startsWith('pyq:')) {
            const jsonMatch = result.match(/\{[\s\S]*"questions"[\s\S]*\}/);
            if (jsonMatch) {
              try { JSON.parse(jsonMatch[0]); } catch { isValidForCache = false; }
            } else {
              isValidForCache = false;
            }
          }
          if (isValidForCache) {
            const existing = await env.QUESTION_CACHE.get(cacheKey, 'json') || { questionSets: [] };
            existing.questionSets.push(result);
            if (existing.questionSets.length > 20) existing.questionSets.shift();
            await env.QUESTION_CACHE.put(cacheKey, JSON.stringify(existing), { expirationTtl: 86400 });
            console.log(`Cache STORED for ${cacheKey}, total: ${existing.questionSets.length}`);
          }
        } catch (e) {
          console.warn('Cache write error:', e.message);
        }
      }

      return corsResponse(JSON.stringify({ content: result }), 200);

    } catch (err) {
      console.error('Worker error:', err.message);
      return corsResponse(JSON.stringify({ error: err.message }), 500);
    }
  }
};

// ===== CASCADING AI CALL WITH INTELLIGENT RATE-LIMIT DETECTION =====
async function callAIWithCascade(messages, options, env) {

  // --- BUILD PROVIDER LIST IN PRIORITY ORDER ---
  // Priority 1-5: Groq keys (fastest free tier, highest accuracy)
  const groqKeys = [
    env.GROQ_API_KEY, env.GROQ_KEY_2, env.GROQ_KEY_3,
    env.GROQ_KEY_4, env.GROQ_KEY_5
  ].filter(Boolean);

  // Priority 6: Google Gemini (high accuracy)
  const geminiKey = env.GEMINI_API_KEY;

  // Priority 7-8: OpenRouter (Gemini Flash Lite → Llama 3.1 Free)
  const openrouterKey = env.OPENROUTER_API_KEY;

  const errors = [];

  // === PRIORITY 1-5: GROQ KEYS (try each individually) ===
  for (let i = 0; i < groqKeys.length; i++) {
    const key = groqKeys[i];
    const keyLabel = `Groq Key ${i + 1}`;
    try {
      console.log(`Trying ${keyLabel}...`);
      const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${key}` },
        body: JSON.stringify({
          model: 'llama-3.3-70b-versatile',
          messages,
          temperature: options.temperature || 0.7,
          max_tokens: options.max_tokens || 4000,
        }),
      });

      const data = await response.json();

      if (response.status === 429) {
        // Rate limited — cascade to next key immediately
        console.warn(`${keyLabel} RATE LIMITED (429). Cascading to next...`);
        errors.push(`${keyLabel}: 429 Rate Limited`);
        continue;
      }

      if (response.ok && data.choices?.[0]) {
        console.log(`✅ ${keyLabel} SUCCESS`);
        return data.choices[0].message.content;
      }

      // Other error (not rate limit) — still try next key
      console.warn(`${keyLabel} failed: ${data.error?.message}`);
      errors.push(`${keyLabel}: ${data.error?.message}`);

    } catch (e) {
      console.warn(`${keyLabel} network error:`, e.message);
      errors.push(`${keyLabel}: ${e.message}`);
    }
  }

  // === PRIORITY 6: GOOGLE GEMINI FLASH ===
  if (geminiKey) {
    try {
      console.log('Trying Google Gemini Flash...');
      const systemMsg = messages.find(m => m.role === 'system');
      const userMessages = messages.filter(m => m.role !== 'system');
      const geminiMessages = userMessages.map(m => ({
        role: m.role === 'assistant' ? 'model' : 'user',
        parts: [{ text: m.content }]
      }));

      const body = {
        contents: geminiMessages,
        generationConfig: { temperature: options.temperature || 0.7, maxOutputTokens: options.max_tokens || 4000 }
      };
      if (systemMsg) body.systemInstruction = { parts: [{ text: systemMsg.content }] };

      const response = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${geminiKey}`,
        { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) }
      );

      const data = await response.json();

      if (response.status === 429) {
        console.warn('Gemini RATE LIMITED (429). Cascading to OpenRouter...');
        errors.push('Gemini: 429 Rate Limited');
      } else if (response.ok && data.candidates?.[0]) {
        console.log('✅ Gemini Flash SUCCESS');
        return data.candidates[0].content.parts[0].text;
      } else {
        console.warn('Gemini failed:', data.error?.message);
        errors.push(`Gemini: ${data.error?.message}`);
      }
    } catch (e) {
      console.warn('Gemini network error:', e.message);
      errors.push(`Gemini: ${e.message}`);
    }
  }

  // === PRIORITY 7-8: OPENROUTER CASCADE (2 models) ===
  const openrouterModels = [
    { model: 'google/gemini-2.0-flash-lite-001', label: 'OpenRouter Gemini Flash Lite' },
    { model: 'meta-llama/llama-3.1-8b-instruct:free', label: 'OpenRouter Llama 3.1 Free (Emergency)' },
  ];

  if (openrouterKey) {
    for (const { model, label } of openrouterModels) {
      try {
        console.log(`Trying ${label}...`);
        const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${openrouterKey}`,
            'X-Title': 'Sarkari Exam AI',
          },
          body: JSON.stringify({
            model,
            messages,
            temperature: options.temperature || 0.7,
            max_tokens: Math.min(options.max_tokens || 2000, 2000),
          }),
        });

        const data = await response.json();

        if (response.status === 429) {
          console.warn(`${label} RATE LIMITED (429). Cascading...`);
          errors.push(`${label}: 429 Rate Limited`);
          continue;
        }

        if (response.ok && data.choices?.[0]) {
          console.log(`✅ ${label} SUCCESS`);
          return data.choices[0].message.content;
        }

        console.warn(`${label} failed:`, data.error?.message);
        errors.push(`${label}: ${data.error?.message}`);

      } catch (e) {
        console.warn(`${label} error:`, e.message);
        errors.push(`${label}: ${e.message}`);
      }
    }
  }

  // === ALL PROVIDERS EXHAUSTED ===
  console.error('❌ ALL AI providers exhausted. Errors:', errors);
  throw new Error('All AI providers are temporarily at capacity. Please try again in a minute.');
}

// ===== CORS =====
function corsResponse(body, status) {
  return new Response(body, {
    status,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    },
  });
}


