// Cloudflare Worker - AI Backend Proxy with Advanced Multi-Key Rotation & Failover
// Engineered for 1,00,000+ students/day with dynamic key discovery and cool-off logic.

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

      // 1. CACHE CHECK (KV)
      if (cacheKey && env.QUESTION_CACHE) {
        const cached = await env.QUESTION_CACHE.get(`cache:${cacheKey}`, 'json');
        if (cached && cached.questionSets && cached.questionSets.length > 0) {
          const randomSet = cached.questionSets[Math.floor(Math.random() * cached.questionSets.length)];
          console.log(`Cache HIT: ${cacheKey}`);
          return corsResponse(JSON.stringify({ content: randomSet, fromCache: true }), 200);
        }
      }

      // 2. AI CALL (Multi-Provider Cascade + Rotation)
      const result = await callAIWithRotation(messages, options, env);

      // 3. STORE IN CACHE (KV)
      if (cacheKey && env.QUESTION_CACHE && result) {
        try {
          const existing = await env.QUESTION_CACHE.get(`cache:${cacheKey}`, 'json') || { questionSets: [] };
          existing.questionSets.push(result);
          if (existing.questionSets.length > 10) existing.questionSets.shift(); // Keep last 10 versions
          await env.QUESTION_CACHE.put(`cache:${cacheKey}`, JSON.stringify(existing), { expirationTtl: 86400 });
        } catch (e) {
          console.warn('Cache store failed:', e.message);
        }
      }

      return corsResponse(JSON.stringify({ content: result }), 200);

    } catch (err) {
      console.error('Worker error:', err.message);
      return corsResponse(JSON.stringify({ error: err.message }), 500);
    }
  }
};

/**
 * Advanced AI Caller with Multi-Key Rotation and Priority Cascading
 */
async function callAIWithRotation(messages, options, env) {
  const errors = [];

  // Helper: Discover all keys for a prefix (e.g., GROQ_KEY_1, GROQ_KEY_2...)
  const getKeys = (prefix) => {
    const keys = [];
    // Manual check for common keys first
    if (env[`${prefix}`]) keys.push(env[`${prefix}`]);
    for (let i = 1; i <= 20; i++) {
      const k = env[`${prefix}_${i}`] || env[`${prefix}${i}`];
      if (k && !keys.includes(k)) keys.push(k);
    }
    return keys;
  };

  // Helper: Check if a key is "cooling off" (marked as 429 in KV)
  const isAvailable = async (keyId) => {
    if (!env.QUESTION_CACHE) return true;
    const status = await env.QUESTION_CACHE.get(`status:${keyId}`);
    return status !== 'cooling';
  };

  // Helper: Mark a key as "cooling off"
  const markCooling = async (keyId) => {
    if (env.QUESTION_CACHE) {
      await env.QUESTION_CACHE.put(`status:${keyId}`, 'cooling', { expirationTtl: 60 });
      console.warn(`Key ${keyId} marked as COOLING for 60s`);
    }
  };

  // --- PROVIDER CONFIG ---
  const providers = [
    { name: 'Groq', prefix: 'GROQ_API_KEY', endpoint: 'https://api.groq.com/openai/v1/chat/completions' },
    { name: 'Gemini', prefix: 'GEMINI_API_KEY', endpoint: 'gemini' }, // Custom handling
    { name: 'OpenRouter', prefix: 'OPENROUTER_API_KEY', endpoint: 'https://openrouter.ai/api/v1/chat/completions' }
  ];

  for (const provider of providers) {
    const keys = getKeys(provider.prefix);
    if (keys.length === 0) continue;

    // Start with a random key to distribute load (Seamless Rotation)
    const startIndex = Math.floor(Math.random() * keys.length);
    console.log(`Starting ${provider.name} rotation at index ${startIndex} (${keys.length} keys)`);

    for (let i = 0; i < keys.length; i++) {
      const idx = (startIndex + i) % keys.length;
      const key = keys[idx];
      const keyId = `${provider.name}_${idx}`;

      if (!(await isAvailable(keyId))) {
        console.log(`Skipping ${keyId} (In Cool-off)`);
        continue;
      }

      try {
        let result;
        if (provider.name === 'Gemini') {
          result = await tryGemini(key, messages, options);
        } else if (provider.name === 'Groq') {
          result = await tryGroq(key, messages, options);
        } else {
          result = await tryOpenRouter(key, messages, options);
        }

        if (result.status === 429) {
          await markCooling(keyId);
          errors.push(`${keyId}: Rate Limited`);
          continue; // Try next key in rotation
        }

        if (result.success) {
          console.log(`✅ Success with ${keyId}`);
          return result.content;
        }

        errors.push(`${keyId}: ${result.error}`);
      } catch (e) {
        errors.push(`${keyId}: Request failed - ${e.message}`);
      }
    }
  }

  throw new Error(`All providers exhausted. Errors: ${errors.join(', ')}`);
}

/** Specific Provider Handlers returning { success, content, status, error } **/

async function tryGroq(key, messages, options) {
  const models = ['llama-3.3-70b-versatile', 'llama-3.1-8b-instant'];
  for (const model of models) {
    const res = await fetch('https://api.groq.com/openai/v1/chat/completions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${key}` },
      body: JSON.stringify({ 
        model, 
        messages, 
        temperature: options.temperature || 0.7, 
        max_tokens: options.max_tokens || 4000 
      })
    });
    if (res.ok) {
      const data = await res.json();
      return { success: true, content: data.choices[0].message.content };
    } else {
      const errTxt = await res.text().catch(()=>'');
      return { success: false, error: `Groq failed: ${res.status} ${errTxt.substring(0, 100)}` };
    }
  }
  return { success: false, error: 'Groq failed completely' };
}

async function tryGemini(key, messages, options) {
  const systemMsg = messages.find(m => m.role === 'system');
  const userMessages = messages.filter(m => m.role !== 'system');
  const body = {
    contents: userMessages.map(m => ({ 
      role: m.role === 'assistant' ? 'model' : 'user', 
      parts: [{ text: m.content }] 
    })),
    generationConfig: { temperature: options.temperature || 0.7, maxOutputTokens: options.max_tokens || 4000 }
  };
  if (systemMsg) body.systemInstruction = { parts: [{ text: systemMsg.content }] };

  const res = await fetch(
    `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${key}`,
    { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) }
  );
  if (res.status === 429) return { status: 429 };
  if (res.ok) {
    const data = await res.json();
    return { success: true, content: data.candidates[0].content.parts[0].text };
  } else {
    const errTxt = await res.text().catch(()=>'');
    return { success: false, error: `Gemini failed: ${res.status} ${errTxt.substring(0, 100)}` };
  }
}

async function tryOpenRouter(key, messages, options) {
  const res = await fetch('https://openrouter.ai/api/v1/chat/completions', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${key}`, 'X-Title': 'Sarkari AI' },
    body: JSON.stringify({ 
      model: 'google/gemini-2.0-flash-lite-001', 
      messages, 
      temperature: options.temperature || 0.7
    })
  });
  if (res.status === 429) return { status: 429 };
  if (res.ok) {
    const data = await res.json();
    return { success: true, content: data.choices[0].message.content };
  } else {
    const errTxt = await res.text().catch(()=>'');
    return { success: false, error: `OpenRouter failed: ${res.status} ${errTxt.substring(0, 100)}` };
  }
}

function corsResponse(body, status) {
  return new Response(body, {
    status,
    headers: {
      'Content-Type': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    }
  });
}
