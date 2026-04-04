// Groq AI API Service
// API key is stored in environment variables (VITE_GROQ_API_KEY)

import i18n, { languages } from '../i18n';
import { db } from './firebase';
import { doc, getDoc, setDoc, serverTimestamp } from 'firebase/firestore';

const getGroqKeys = () => {
  const keys = [];
  const primary = import.meta.env.VITE_GROQ_API_KEY;
  if (primary) keys.push(primary);
  for (let i = 1; i <= 10; i++) {
    const k = import.meta.env[`VITE_GROQ_API_KEY_${i}`];
    if (k && !keys.includes(k)) keys.push(k);
  }
  return keys.sort(() => Math.random() - 0.5);
};

const getGeminiKeys = () => {
  const keys = [];
  const primary = import.meta.env.VITE_GEMINI_API_KEY;
  if (primary) keys.push(primary);
  for (let i = 1; i <= 10; i++) {
    const k = import.meta.env[`VITE_GEMINI_API_KEY_${i}`];
    if (k && !keys.includes(k)) keys.push(k);
  }
  return keys.sort(() => Math.random() - 0.5);
};

const getOpenRouterKeys = () => {
  const keys = [];
  const primary = import.meta.env.VITE_OPENROUTER_API_KEY;
  if (primary) keys.push(primary);
  for (let i = 1; i <= 10; i++) {
    const k = import.meta.env[`VITE_OPENROUTER_API_KEY_${i}`];
    if (k && !keys.includes(k)) keys.push(k);
  }
  return keys.sort(() => Math.random() - 0.5);
};

const getLanguageName = (code) => {
  const currentCode = code || i18n.language || 'en';
  const baseCode = currentCode.split('-')[0];
  const l = languages.find(lang => lang.code === baseCode);
  return l ? l.name : 'English';
};

const extractJSON = (text) => {
  try {
    let cleanText = text.replace(/```json/gi, '').replace(/```/g, '').trim();

    // Auto-fix common AI JSON typos before parsing
    cleanText = cleanText
      .replace(/"options"\s*\)\[/g, '"options":[') // Fix `"options")[A, B]` mistake
      .replace(/"options"\s*\[/g, '"options":[') // Fix `"options" [` mistake
      .replace(/options\s*\)\[/g, '"options":[') // Fix `options)["A", "B"]` mistake
      .replace(/options\s*\):/g, '"options":') // Fix `options):` mistake
      .replace(/options\s*\)/g, '"options":') // Fix `options)` mistake
      .replace(/(?<!")options(?!")/g, '"options"') // Quote options if unquoted
      .replace(/([{,]\s*)([a-zA-Z0-9_]+)\s*:/g, '$1"$2":') // Wrap unquoted keys in quotes
      .replace(/,\s*([}\]])/g, '$1'); // Remove trailing commas

    const start = cleanText.indexOf('{');
    const end = cleanText.lastIndexOf('}');
    if (start !== -1 && end !== -1 && start < end) {
      const jsonStr = cleanText.substring(start, end + 1);
      const data = JSON.parse(jsonStr);
      const conversation = cleanText.replace(jsonStr, '').trim();
      return { data, conversation };
    }
    const startArr = cleanText.indexOf('[');
    const endArr = cleanText.lastIndexOf(']');
    if (startArr !== -1 && endArr !== -1 && startArr < endArr) {
      const jsonStr = cleanText.substring(startArr, endArr + 1);
      const data = JSON.parse(jsonStr);
      const conversation = cleanText.replace(jsonStr, '').trim();
      return { data, conversation };
    }
  } catch (e) {
    console.error('Failed to parse AI response:', text, 'Error:', e.message);
  }
  return { data: null, conversation: text };
};

const parseTextToQuestions = (text) => {
  const questions = [];
  try {
    // Split by variations of "Q:", "Q1.", "Question 1:", etc.
    const blocks = text.split(/(?:^|\n)\s*(?:Q|Question)\s*\d*[:.]?\s*/i).filter(b => b.trim());
    
    blocks.forEach((block, index) => {
      const lines = block.split('\n').filter(l => l.trim() !== '');
      if (lines.length < 3) return; 

      let questionStr = '';
      let options = [];
      let correctAnswerStr = '';
      let explanationStr = '';
      let mode = 'Q';
      
      lines.forEach(line => {
        const trimmed = line.trim();
        // Match options like "A) ", "A. ", "(A) "
        const optionMatch = trimmed.match(/^[\(]?([A-E])[\).:]\s*(.+)/i);
        if (optionMatch) {
          mode = 'O';
          options.push(optionMatch[2].trim());
        } else if (/^(?:Answer|Correct(?: Answer)?)[:.]\s*/i.test(trimmed)) {
          mode = 'A';
          let ansStr = trimmed.replace(/^(?:Answer|Correct(?: Answer)?)[:.]\s*/i, '').trim();
          // Extract just the letter if they wrote "A", "A)", "Option A"
          const letterMatch = ansStr.match(/(?:Option\s*)?([A-E])/i);
          if (letterMatch && options.length > 0) {
             const letterIndex = letterMatch[1].toUpperCase().charCodeAt(0) - 65;
             if (letterIndex >= 0 && letterIndex < options.length) {
               correctAnswerStr = options[letterIndex];
             } else {
               correctAnswerStr = ansStr; // Fallback to raw string
             }
          } else {
             correctAnswerStr = ansStr;
          }
        } else if (/^Explanation[:.]\s*/i.test(trimmed) || mode === 'E') {
          if (mode !== 'E') {
            mode = 'E';
            explanationStr = trimmed.replace(/^Explanation[:.]\s*/i, '').trim();
          } else {
            explanationStr += '\n' + trimmed;
          }
        } else {
          if (mode === 'Q') questionStr += (questionStr ? '\n' : '') + trimmed;
          else if (mode === 'E') explanationStr += '\n' + trimmed;
        }
      });
      
      // Attempt to auto-detect correct answer if it wasn't explicitly formatted but options contain it
      if (!correctAnswerStr && options.length > 0) {
        // Fallback: assume answering failed formatting or first option usually isn't answer unless stated
        // Actually, if Answer: is not found, we might skip the question entirely as it's malformed.
      }

      if (questionStr && options.length >= 2 && correctAnswerStr) {
        questions.push({
          id: index + 1,
          question: questionStr,
          options: options,
          correctAnswer: correctAnswerStr,
          explanation: explanationStr || 'No explanation provided.'
        });
      }
    });
  } catch(e) {
    console.error("Text parsing error:", e);
  }
  
  return { data: { questions }, conversation: text };
};


// ===== AI CALL LOGIC =====
// In production: calls Cloudflare Worker (keys are secret server-side)
// In dev: calls APIs directly using .env keys

const getWorkerUrl = () => import.meta.env.VITE_WORKER_URL || '';

const callAI = async (messages, options = {}, cacheKey = null) => {
  // --- LOCAL CACHING LAYER ---
  if (cacheKey) {
    try {
      const cached = localStorage.getItem(cacheKey);
      if (cached) {
        const { timestamp, content } = JSON.parse(cached);
        // Cache valid for 24 hours
        if (Date.now() - timestamp < 24 * 60 * 60 * 1000) {
          console.log(`[AI Cache Hit] ${cacheKey}`);
          return content;
        }
      }
    } catch(e) { console.warn("Cache read error:", e); }

    // --- GLOBAL FIRESTORE CACHE ---
    try {
      // Create a URL-safe document ID from cacheKey
      const safeDocId = cacheKey.replace(/[^a-zA-Z0-9_\-]/g, '_').substring(0, 500); 
      const docRef = doc(db, 'ai_global_cache', safeDocId);
      const docSnap = await getDoc(docRef);
      if (docSnap.exists()) {
        const data = docSnap.data();
        if (data.content) {
          console.log(`[Global Firestore Cache Hit] ${safeDocId}`);
          // Save to local cache so next time it's instant
          try { localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), content: data.content })); } catch(e) {}
          return data.content;
        }
      }
    } catch(e) { 
      console.warn("Global cache read error:", e); 
    }
  }

  const saveCache = (data) => {
    if (!cacheKey || !data) return data;
    try {
      localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), content: data }));
    } catch (e) {
      localStorage.clear(); // Clear space if quota exceeded
      try { localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), content: data })); } catch(err) {}
    }

    // Asynchronously save to Firestore without blocking response
    try {
      const safeDocId = cacheKey.replace(/[^a-zA-Z0-9_\-]/g, '_').substring(0, 500);
      setDoc(doc(db, 'ai_global_cache', safeDocId), {
        content: data,
        timestamp: serverTimestamp()
      }, { merge: true }).catch(err => console.warn("Global cache write error:", err));
    } catch (e) { 
      console.warn("Global cache write error:", e); 
    }

    return data;
  };

  const workerUrl = getWorkerUrl();

  // PRODUCTION: Use Cloudflare Worker (secure + cached) with retry
  if (workerUrl) {
    for (let attempt = 1; attempt <= 3; attempt++) {
      try {
        const response = await fetch(workerUrl, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ messages, options, cacheKey }),
        });

        const data = await response.json();
        if (response.ok && data.content) {
          return saveCache(data.content);
        }
        console.warn(`Worker attempt ${attempt} failed:`, data.error);
        // Wait before retry (2s, 4s, 8s)
        if (attempt < 3) {
          await new Promise(r => setTimeout(r, attempt * 2000));
        }
      } catch (e) {
        console.warn(`Worker attempt ${attempt} error:`, e.message);
        if (attempt < 3) {
          await new Promise(r => setTimeout(r, attempt * 2000));
        }
      }
    }
    // Worker failed 3 times — fall through to direct API as backup
    console.warn('Worker failed after 3 attempts, trying direct API...');
  }

  // LOCAL DEV / DIRECT FALLBACK: Full Cascade with 429 Rate-Limit Detection
  // Cascade Order: Groq (5 keys) → Gemini → OpenRouter Gemini → OpenRouter Llama Free

  const COOLDOWN_MS = 5 * 60 * 1000; // 5 minutes cooldown per rate-limited key

  const isOnCooldown = (key) => {
    try {
      const ts = sessionStorage.getItem(`rl_cooldown_${key.slice(-6)}`);
      return ts && (Date.now() - parseInt(ts)) < COOLDOWN_MS;
    } catch { return false; }
  };

  const markCooldown = (key) => {
    try { sessionStorage.setItem(`rl_cooldown_${key.slice(-6)}`, Date.now().toString()); } catch { }
  };

  const groqKeys = getGroqKeys(); // Already shuffled
  const geminiKeys = getGeminiKeys(); // Already shuffled
  const orKeys = getOpenRouterKeys(); // Already shuffled

  // === PRIORITY 1-5: GROQ KEYS ===
  for (let i = 0; i < groqKeys.length; i++) {
    const groqKey = groqKeys[i];
    const label = `Groq Key ${i + 1}`;

    if (isOnCooldown(groqKey)) {
      console.warn(`[Cascade] ${label} is on 429 cooldown, skipping.`);
      continue;
    }

    try {
      const groqResponse = await fetch('https://api.groq.com/openai/v1/chat/completions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${groqKey}` },
        body: JSON.stringify({
          model: 'llama-3.3-70b-versatile',
          messages,
          temperature: options.temperature || 0.7,
          max_tokens: options.max_tokens || 4000,
        }),
      });

      const groqData = await groqResponse.json();

      if (groqResponse.status === 429) {
        console.warn(`[Cascade] ${label} RATE LIMITED (429). Cooling down & cascading...`);
        markCooldown(groqKey);
        continue;
      }

      if (groqResponse.ok && groqData.choices?.[0]) {
        console.log(`[Cascade] ✅ ${label} SUCCESS`);
        return saveCache(groqData.choices[0].message.content);
      }

      console.warn(`[Cascade] ${label} failed:`, groqData.error?.message);
    } catch (e) {
      console.warn(`[Cascade] ${label} network error:`, e.message);
    }
  }

  // === PRIORITY 6: GOOGLE GEMINI (DIRECT ROTATION) ===
  for (const key of geminiKeys) {
    if (isOnCooldown(key)) continue;
    try {
      console.log(`[Cascade] Trying Google Gemini Direct (Key ${key.substring(0, 8)})...`);
      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${key}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: typeof messages === 'string' ? messages : messages.map(m => `${m.role}: ${m.content}`).join('\n') }] }],
          generationConfig: {
            maxOutputTokens: options.max_tokens || 4000,
            temperature: options.temperature || 0.7,
          }
        })
      });

      const data = await response.json();
      if (response.status === 429) {
        console.warn('[Cascade] Gemini RATE LIMITED (429). Switching key...');
        markCooldown(key);
        continue;
      }
      if (response.ok && data.candidates?.[0]?.content?.parts?.[0]?.text) {
        return saveCache(data.candidates[0].content.parts[0].text);
      }
    } catch (e) {
      console.warn(`[Cascade] Gemini key ${key.substring(0, 8)} error:`, e.message);
    }
  }

  // === PRIORITY 7: OPENROUTER ROTATION ===
  for (const key of orKeys) {
    const openrouterModels = [
      { model: 'google/gemini-2.0-flash-lite-001:free', label: 'OpenRouter Gemini Flash Lite (Free)' },
      { model: 'meta-llama/llama-3.2-3b-instruct:free', label: 'OpenRouter Llama 3.2 Free' },
    ];

    for (const { model, label } of openrouterModels) {
      const cooldownKey = `or_${model}_${key.substring(0, 8)}`;
      if (isOnCooldown(cooldownKey)) continue;

      try {
        console.log(`[Cascade] Trying ${label} with Key ${key.substring(0, 8)}...`);
        const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${key}`,
            'HTTP-Referer': typeof window !== 'undefined' ? window.location.origin : '',
            'X-Title': 'Sarkari Exam AI',
          },
          body: JSON.stringify({
            model,
            messages,
            temperature: options.temperature || 0.7,
            max_tokens: Math.min(options.max_tokens || 1500, 2000),
          }),
        });

        const data = await response.json();
        if (response.status === 429) {
          console.warn(`[Cascade] ${label} RATE LIMITED. Trying next...`);
          markCooldown(cooldownKey);
          continue;
        }
        if (response.ok && data.choices?.[0]) {
          return saveCache(data.choices[0].message.content);
        }
      } catch (e) {
        console.error(`[Cascade] OpenRouter key error:`, e.message);
      }
    }
  }

  throw new Error('All AI providers are temporarily at capacity. Please try again in a moment.');
};



// ===== STUDY PLANNER =====
export const generateStudyPlan = async ({ exam, hours, level, weakSubjects, strongSubjects, language }) => {
  const lang = getLanguageName(language);
  const messages = [
    {
      role: 'system',
      content: `You are an expert Indian competitive exam coach. Generate a detailed weekly study plan. Respond in ${lang}. Use detailed markdown formatting with headings, bullet points, and tables. DO NOT use JSON.`
    },
    {
      role: 'user',
      content: `Create a study plan for ${exam} exam preparation.
- Available study hours per day: ${hours}
- Current preparation level: ${level}
- Weak subjects: ${weakSubjects?.join(', ') || 'None specified'}
- Strong subjects: ${strongSubjects?.join(', ') || 'None specified'}
- Respond in: ${lang}`
    }
  ];
  return await callAI(messages, { max_tokens: 1500 });
};

// ===== MOCK TEST QUESTIONS =====
export const generateMockQuestions = async ({ exam, subject, difficulty, count, language }) => {
  const lang = getLanguageName(language);
  const randomSeed = Math.floor(Math.random() * 100000); // Prevent AI determinism
  const messages = [
    {
      role: 'system',
      content: `You are an expert Indian competitive exam creator. Generate exactly ${count || 5} MCQ questions. Respond in ${lang}.
CRITICAL RULES:
1. DO NOT USE JSON. Respond STRICTLY in plain text/markdown format.
2. Format EACH question exactly like this:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [1-2 sentences of explanation]`
    },
    {
      role: 'user',
      content: `Generate ${count || 5} practice MCQ questions for ${exam} exam.
- Subject: ${subject || 'General'}
- Difficulty: ${difficulty || 'medium'}
- Randomization Seed: ${randomSeed} (Ensure these questions are highly diverse and different from previous sets)
- Keep explanations SHORT (1-2 sentences).
- CRITICAL: Return ONLY the structured text, no extra conversational filler.
- Respond in: ${lang}`
    }
  ];
  try {
    // Intentionally removed cacheKey to prevent students from getting repeating questions
    const result = await callAI(messages, { max_tokens: 2000 });
    const parsed = parseTextToQuestions(result);
    if (!parsed.data || !parsed.data.questions || parsed.data.questions.length === 0) {
      // Fallback for strict JSON parser if text parsing didn't catch anything due to model ignoring formatting
      const fallbackParsed = extractJSON(result);
      if (fallbackParsed.data?.questions?.length > 0) return fallbackParsed;
      throw new Error("Unable to parse generated mock questions.");
    }
    return parsed;
  } catch (err) {
    console.error("AI call failed:", err);
    throw new Error("AI Server is too busy or API limits exhausted. Please try again later.");
  }
};

// ===== PYQS MOCK TEST QUESTIONS =====
export const generatePYQSMockQuestions = async ({ topic, year, count, language }) => {
  const lang = getLanguageName(language);
  const yearContext = year ? ` from the year ${year}` : '';
  const randomSeed = Math.floor(Math.random() * 100000); // Prevent AI determinism
  const messages = [
    {
      role: 'system',
      content: `You are an expert Indian competitive exam creator. Generate exactly ${count || 5} Past Year Questions (PYQs). Respond in ${lang}.
CRITICAL RULES:
1. DO NOT USE JSON. Respond STRICTLY in plain text/markdown format.
2. Format EACH question exactly like this:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [1-2 sentences of explanation]`
    },
    {
      role: 'user',
      content: `Generate ${count || 5} Past Year Questions (PYQs) for: ${topic}${yearContext}.
- Randomization Seed: ${randomSeed} (Randomly select a unique batch of questions from that paper, DO NOT select the exact same first 10 questions)
- Use real historical questions if available.
- Keep explanations SHORT (1-2 sentences).
- CRITICAL: Return ONLY the structured text, no extra conversational filler.
- Respond in: ${lang}`
    }
  ];
  try {
    // Intentionally removed cacheKey so multiple attempts of the same PYQ give different question batches
    const result = await callAI(messages, { max_tokens: 4000 });
    const parsed = parseTextToQuestions(result);
    if (!parsed.data || !parsed.data.questions || parsed.data.questions.length === 0) {
      const fallbackParsed = extractJSON(result);
      if (fallbackParsed.data?.questions?.length > 0) return fallbackParsed;
      throw new Error("Unable to parse generated past year questions.");
    }
    return parsed;
  } catch (err) {
    console.error("AI call failed:", err);
    throw new Error("AI Server is too busy or API limits exhausted. Please try again later.");
  }
};

// ===== PDF TO QUIZ CONVERTER =====
export const convertPdfToQuiz = async ({ text, exam, language }) => {
  const lang = getLanguageName(language);
  const messages = [
    {
      role: 'system',
      content: `You are an expert Indian competitive exam creator. I will provide raw extracted text from a PDF of a Previous Year Question (PYQ) paper. 
Extract 10-15 key multiple-choice questions from the text. Respond in ${lang}. 
CRITICAL RULES:
1. DO NOT USE JSON. Respond STRICTLY in plain text/markdown format.
2. Format EACH question exactly like this:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [Provide highly detailed background information on why the answer is correct to provide extreme value]`
    },
    {
      role: 'user',
      content: `Extract the best MCQs from this uploaded document text (ignore headers, footers, unstructured noise):\n\n${text.substring(0, 10000)}\n\nRespond in: ${lang}`
    }
  ];
  try {
    const result = await callAI(messages, { max_tokens: 3000 });
    const parsed = parseTextToQuestions(result);
    if (!parsed.data || !parsed.data.questions || parsed.data.questions.length === 0) {
      const fallbackParsed = extractJSON(result);
      if (fallbackParsed.data?.questions?.length > 0) return fallbackParsed;
      throw new Error("Unable to extract valid questions from PDF.");
    }
    return parsed;
  } catch (err) {
    console.error("AI call failed:", err);
    throw new Error("Failed to process PDF. Please try again later.");
  }
};

// ===== PAST PAPER ANALYSIS =====
export const analyzePastPaper = async ({ text, exam, language }) => {
  const lang = getLanguageName(language);
  const messages = [
    {
      role: 'system',
      content: `You are an expert exam paper analyst. Analyze the given exam paper content and identify patterns. Respond in ${lang}. Provide an EXTREMELY LONG AND DETAILED analysis using markdown formatting. You must provide extensive depth, exhaustive breakdowns of frequent topics, difficulty levels, subject-wise statistics, and long-form recommendations. Do not summarize briefly; students need maximum detail. DO NOT use JSON.`
    },
    {
      role: 'user',
      content: `Analyze this ${exam || 'competitive'} exam paper and identify patterns:\n\n${text?.substring(0, 4000) || 'No text provided'}\n\nPlease generate a very detailed, long-form analysis report. Respond in: ${lang}`
    }
  ];
  return await callAI(messages, { max_tokens: 1500 });
};

// ===== NOTES GENERATOR =====
export const generateNotes = async ({ exam, subject, topics, language }) => {
  const lang = getLanguageName(language);
  const messages = [
    {
      role: 'system',
      content: `You are an expert study material creator for Indian competitive exams. Generate EXTREMELY COMPREHENSIVE, LONG, and DETAILED study notes. Use bullet points, important facts, and key dates. Respond in ${lang}. Use markdown formatting. IMPORTANT: Do not provide short summaries. The notes MUST be incredibly detailed and exhaustive, covering every possible nuance of the topics to give students maximum value. DO NOT include any conversational filler, logs, or meta-comments.`
    },
    {
      role: 'user',
      content: `Generate very long, detailed, and comprehensive study notes for ${exam} exam preparation.
- Subject: ${subject}
- Topics to cover: ${topics?.join(', ') || 'All important topics'}
- Make notes highly detailed and exam-focused with key facts, dates, theories, and concepts.
- Include memory tricks and mnemonics where possible.
- Provide an extensive deep dive into each sub-topic. Write as much relevant content as possible.
- DO NOT include any introductory text, concluding remarks, or logs. Just the notes.
- Respond in: ${lang}`
    }
  ];
  return callAI(messages, { max_tokens: 1500 });
};

// ===== PERFORMANCE ANALYSIS =====
export const analyzePerformance = async ({ testHistory, exam, language }) => {
  const lang = getLanguageName(language);
  const summary = testHistory?.map(t => `${t.subject}: ${t.score}/${t.total} (${t.date})`).join('\n') || 'No test data';
  const messages = [
    {
      role: 'system',
      content: `You are an AI exam performance analyst. Analyze the student's test history and provide insights. Respond in ${lang}. Provide a detailed analysis using markdown formatting, indicating strengths, weaknesses, readiness score, and improvement tips. DO NOT use JSON.`
    },
    {
      role: 'user',
      content: `Analyze my ${exam} exam preparation performance:\n\n${summary}\n\nProvide detailed analysis and a readiness score out of 100. Respond in: ${lang}`
    }
  ];
  return await callAI(messages, { max_tokens: 1500 });
};

// ===== INTERACTIVE TUTOR =====
export const generateTutorLesson = async ({ history, language }) => {
  const lang = getLanguageName(language);
  const systemPrompt = {
    role: 'system',
    content: `You are Riya, an expert, patient, and engaging AI Tutor for Indian competitive exams. Answer the student's questions clearly based on your vast knowledge of exam topics. IMPORTANT IDENTITY RULE: If anyone asks who developed you, which AI model you are (like ChatGPT, Gemini, etc.), or what technology powers you, you MUST firmly reply that you are a personalized proprietary AI model developed exclusively by the Sarkari Exam AI company. Never mention Google, Gemini, OpenAI, or LLMs. Respond in ${lang}. Break down complex concepts into simple explanations. Use markdown formatting. DO NOT use JSON. Keep responses concise enough for a chat interface, but detailed enough to teach properly.`
  };
  
  const messages = [systemPrompt, ...history];
  return await callAI(messages, { max_tokens: 1500 });
};

// ===== DIAGNOSTICS =====
export const testAIConnections = async () => {
  const results = {
    openrouter: { status: 'testing', message: '' },
    groq: { status: 'testing', message: '' },
    gemini: { status: 'testing', message: '' }
  };
 
  const orKeys = getOpenRouterKeys();
  const orKey = orKeys[0];
  const groqKeys = getGroqKeys();
  const geminiKeys = getGeminiKeys();
  const geminiKey = geminiKeys[0];
 
   if (geminiKey) {
    try {
      const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${geminiKey}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: 'respond with "ok"' }] }],
          generationConfig: { maxOutputTokens: 10 }
        })
      });
      if (response.ok) {
        results.gemini = { status: 'success', message: 'Connected to Gemini Direct' };
      } else {
        const err = await response.json().catch(() => ({}));
        results.gemini = { status: 'error', message: err.error?.message || `Error ${response.status}` };
      }
    } catch (e) {
      results.gemini = { status: 'error', message: e.message };
    }
  } else {
    results.gemini = { status: 'none', message: 'Not configured' };
  }

  if (orKey) {
    try {
      const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${orKey}`,
        },
        body: JSON.stringify({
          model: 'google/gemini-2.0-flash-lite-001',
          messages: [{ role: 'user', content: 'respond with "ok"' }],
          max_tokens: 10
        }),
      });
      if (response.ok) {
        results.openrouter = { status: 'success', message: 'Connected to Gemini Flash Lite via OpenRouter' };
      } else {
        const err = await response.json().catch(() => ({}));
        results.openrouter = { status: 'error', message: err.error?.message || `Error ${response.status}: ${response.statusText}` };
      }
    } catch (e) {
      results.openrouter = { status: 'error', message: e.message };
    }
  } else {
    results.openrouter = { status: 'none', message: 'Not configured' };
  }

  if (groqKeys.length > 0) {
    const groqKey = groqKeys[0];
    try {
      const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${groqKey}`,
        },
        body: JSON.stringify({
          model: 'llama-3.3-70b-versatile',
          messages: [{ role: 'user', content: 'respond with "ok"' }],
          max_tokens: 10
        }),
      });
      if (response.ok) {
        results.groq = { status: 'success', message: 'Connected to Llama via Groq' };
      } else {
        const err = await response.json().catch(() => ({}));
        results.groq = { status: 'error', message: err.error?.message || `Error ${response.status}: ${response.statusText}` };
      }
    } catch (e) {
      results.groq = { status: 'error', message: e.message };
    }
  } else {
    results.groq = { status: 'none', message: 'Not configured' };
  }

  return results;
};

export const hasApiKey = () => !!(getOpenRouterKeys().length > 0 || getGroqKeys().length > 0);

export default callAI;
