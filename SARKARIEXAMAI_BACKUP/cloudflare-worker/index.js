// Cloudflare Worker - AI Backend Proxy with KV Caching
// Smart caching: 1L students/day for FREE
// Cache questions by exam+subject+difficulty → serve from cache → 0 API calls

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
          // Pick a random set from cache so students get different questions
          const randomSet = cached.questionSets[Math.floor(Math.random() * cached.questionSets.length)];
          console.log(`Cache HIT for ${cacheKey}, serving set ${cached.questionSets.indexOf(randomSet) + 1}/${cached.questionSets.length}`);
          return corsResponse(JSON.stringify({ content: randomSet, fromCache: true }), 200);
        }
      }

      // ===== AI CALL (cache miss) =====
      const result = await callAI(messages, options, env);

      // ===== STORE IN CACHE (only valid JSON responses) =====
      if (cacheKey && env.QUESTION_CACHE && result) {
        try {
          // Validate: only cache if response contains valid JSON with questions
          let isValidForCache = true;
          if (cacheKey.startsWith('mock:') || cacheKey.startsWith('pyq:')) {
            try {
              // Try to extract JSON from the response
              const jsonMatch = result.match(/\{[\s\S]*"questions"[\s\S]*\}/);
              if (jsonMatch) {
                JSON.parse(jsonMatch[0]); // validates it's proper JSON
              } else {
                isValidForCache = false;
                console.warn('Cache SKIP: No valid JSON with questions found in response');
              }
            } catch (parseErr) {
              isValidForCache = false;
              console.warn('Cache SKIP: Invalid JSON in response:', parseErr.message);
            }
          }

          if (isValidForCache) {
            const existing = await env.QUESTION_CACHE.get(cacheKey, 'json') || { questionSets: [] };
            existing.questionSets.push(result);
            // Keep max 20 sets per combo (variety for students)
            if (existing.questionSets.length > 20) {
              existing.questionSets.shift();
            }
            // Cache for 24 hours
            await env.QUESTION_CACHE.put(cacheKey, JSON.stringify(existing), { expirationTtl: 86400 });
            console.log(`Cache STORED for ${cacheKey}, total sets: ${existing.questionSets.length}`);
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

// ===== MULTI-PROVIDER AI CALL WITH ROTATION =====
async function callAI(messages, options, env) {
  // Collect all available API keys
  const groqKeys = [];
  if (env.GROQ_API_KEY) groqKeys.push(env.GROQ_API_KEY);
  if (env.GROQ_KEY_2) groqKeys.push(env.GROQ_KEY_2);
  if (env.GROQ_KEY_3) groqKeys.push(env.GROQ_KEY_3);
  if (env.GROQ_KEY_4) groqKeys.push(env.GROQ_KEY_4);
  if (env.GROQ_KEY_5) groqKeys.push(env.GROQ_KEY_5);

  const geminiKey = env.GEMINI_API_KEY;
  const openrouterKey = env.OPENROUTER_API_KEY;

  // Shuffle Groq keys for fair rotation
  const shuffledGroqKeys = groqKeys.sort(() => Math.random() - 0.5);

  // 1. Try all Groq keys
  for (const key of shuffledGroqKeys) {
    try {
      const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${key}`,
        },
        body: JSON.stringify({
          model: 'llama-3.3-70b-versatile',
          messages,
          temperature: options.temperature || 0.7,
          max_tokens: options.max_tokens || 4000,
        }),
      });
      const data = await response.json();
      if (response.ok && data.choices?.[0]) {
        console.log('Groq success');
        return data.choices[0].message.content;
      }
      console.warn('Groq key failed:', data.error?.message);
    } catch (e) {
      console.warn('Groq error:', e.message);
    }
  }

  // 2. Try Gemini
  if (geminiKey) {
    try {
      const systemMsg = messages.find(m => m.role === 'system');
      const userMessages = messages.filter(m => m.role !== 'system');
      const geminiMessages = userMessages.map(m => ({
        role: m.role === 'assistant' ? 'model' : 'user',
        parts: [{ text: m.content }]
      }));

      const body = {
        contents: geminiMessages,
        generationConfig: {
          temperature: options.temperature || 0.7,
          maxOutputTokens: options.max_tokens || 4000,
        }
      };
      if (systemMsg) {
        body.systemInstruction = { parts: [{ text: systemMsg.content }] };
      }

      const response = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${geminiKey}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body),
        }
      );
      const data = await response.json();
      if (response.ok && data.candidates?.[0]) {
        console.log('Gemini success');
        return data.candidates[0].content.parts[0].text;
      }
      console.warn('Gemini failed:', data.error?.message);
    } catch (e) {
      console.warn('Gemini error:', e.message);
    }
  }

  // 3. Try OpenRouter
  if (openrouterKey) {
    try {
      const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${openrouterKey}`,
          'X-Title': 'Sarkari Exam AI',
        },
        body: JSON.stringify({
          model: 'google/gemini-2.0-flash-lite-001',
          messages,
          temperature: options.temperature || 0.7,
          max_tokens: Math.min(options.max_tokens || 2000, 2000),
        }),
      });
      const data = await response.json();
      if (response.ok && data.choices?.[0]) {
        console.log('OpenRouter success');
        return data.choices[0].message.content;
      }
      console.warn('OpenRouter failed:', data.error?.message);
    } catch (e) {
      console.warn('OpenRouter error:', e.message);
    }
  }

  throw new Error('All AI providers are busy. Please try again in a moment.');
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
