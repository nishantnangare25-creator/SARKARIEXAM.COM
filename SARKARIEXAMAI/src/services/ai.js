// Groq AI API Service
// API key is stored in environment variables (VITE_GROQ_API_KEY)
export const BUILD_ID = "v2.1." + 1714470123456; // Updated for fresh trigger

import i18n, { languages } from '../i18n';
import { db } from './firebase';
import { doc, getDoc, setDoc, serverTimestamp } from 'firebase/firestore';
import fallbackMocks from '../data/fallback_mocks.json';
import fallbackPlanners from '../data/fallback_planners.json';
import fallbackChat from '../data/fallback_chat.json';

const getGroqKeys = () => {
  const keys = [
    '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',
    import.meta.env.VITE_GROQ_API_KEY,
    import.meta.env.VITE_GROQ_API_KEY_1, import.meta.env.VITE_GROQ_API_KEY_2, import.meta.env.VITE_GROQ_API_KEY_3,
    import.meta.env.VITE_GROQ_API_KEY_4, import.meta.env.VITE_GROQ_API_KEY_5, import.meta.env.VITE_GROQ_API_KEY_6,
    import.meta.env.VITE_GROQ_API_KEY_7, import.meta.env.VITE_GROQ_API_KEY_8, import.meta.env.VITE_GROQ_API_KEY_9,
    import.meta.env.VITE_GROQ_API_KEY_10, import.meta.env.VITE_GROQ_API_KEY_11, import.meta.env.VITE_GROQ_API_KEY_12,
    import.meta.env.VITE_GROQ_API_KEY_13, import.meta.env.VITE_GROQ_API_KEY_14, import.meta.env.VITE_GROQ_API_KEY_15,
    import.meta.env.VITE_GROQ_API_KEY_16, import.meta.env.VITE_GROQ_API_KEY_17, import.meta.env.VITE_GROQ_API_KEY_18,
    import.meta.env.VITE_GROQ_API_KEY_19, import.meta.env.VITE_GROQ_API_KEY_20,
    import.meta.env.VITE_GROQ_API_KEY_21, import.meta.env.VITE_GROQ_API_KEY_22, import.meta.env.VITE_GROQ_API_KEY_23,
    import.meta.env.VITE_GROQ_API_KEY_24, import.meta.env.VITE_GROQ_API_KEY_25, import.meta.env.VITE_GROQ_API_KEY_26,
    import.meta.env.VITE_GROQ_API_KEY_27, import.meta.env.VITE_GROQ_API_KEY_28, import.meta.env.VITE_GROQ_API_KEY_29,
    import.meta.env.VITE_GROQ_API_KEY_30, import.meta.env.VITE_GROQ_API_KEY_31, import.meta.env.VITE_GROQ_API_KEY_32,
    import.meta.env.VITE_GROQ_API_KEY_33, import.meta.env.VITE_GROQ_API_KEY_34, import.meta.env.VITE_GROQ_API_KEY_35,
    import.meta.env.VITE_GROQ_API_KEY_36, import.meta.env.VITE_GROQ_API_KEY_37, import.meta.env.VITE_GROQ_API_KEY_38,
    import.meta.env.VITE_GROQ_API_KEY_39, import.meta.env.VITE_GROQ_API_KEY_40,
    import.meta.env.VITE_GROQ_API_KEY_41, import.meta.env.VITE_GROQ_API_KEY_42, import.meta.env.VITE_GROQ_API_KEY_43,
    import.meta.env.VITE_GROQ_API_KEY_44, import.meta.env.VITE_GROQ_API_KEY_45, import.meta.env.VITE_GROQ_API_KEY_46,
    import.meta.env.VITE_GROQ_API_KEY_47, import.meta.env.VITE_GROQ_API_KEY_48, import.meta.env.VITE_GROQ_API_KEY_49,
    import.meta.env.VITE_GROQ_API_KEY_50
  ].filter(Boolean);
  return [...new Set(keys)].sort(() => Math.random() - 0.5);
};

const getGeminiKeys = () => {
  const keys = [
    import.meta.env.VITE_GEMINI_API_KEY,
    import.meta.env.VITE_GEMINI_API_KEY_1, import.meta.env.VITE_GEMINI_API_KEY_2, import.meta.env.VITE_GEMINI_API_KEY_3,
    import.meta.env.VITE_GEMINI_API_KEY_4, import.meta.env.VITE_GEMINI_API_KEY_5, import.meta.env.VITE_GEMINI_API_KEY_6,
    import.meta.env.VITE_GEMINI_API_KEY_7, import.meta.env.VITE_GEMINI_API_KEY_8, import.meta.env.VITE_GEMINI_API_KEY_9,
    import.meta.env.VITE_GEMINI_API_KEY_10, import.meta.env.VITE_GEMINI_API_KEY_11, import.meta.env.VITE_GEMINI_API_KEY_12,
    import.meta.env.VITE_GEMINI_API_KEY_13, import.meta.env.VITE_GEMINI_API_KEY_14, import.meta.env.VITE_GEMINI_API_KEY_15,
    import.meta.env.VITE_GEMINI_API_KEY_16, import.meta.env.VITE_GEMINI_API_KEY_17, import.meta.env.VITE_GEMINI_API_KEY_18,
    import.meta.env.VITE_GEMINI_API_KEY_19, import.meta.env.VITE_GEMINI_API_KEY_20,
    import.meta.env.VITE_GEMINI_API_KEY_21, import.meta.env.VITE_GEMINI_API_KEY_22, import.meta.env.VITE_GEMINI_API_KEY_23,
    import.meta.env.VITE_GEMINI_API_KEY_24, import.meta.env.VITE_GEMINI_API_KEY_25, import.meta.env.VITE_GEMINI_API_KEY_26,
    import.meta.env.VITE_GEMINI_API_KEY_27, import.meta.env.VITE_GEMINI_API_KEY_28, import.meta.env.VITE_GEMINI_API_KEY_29,
    import.meta.env.VITE_GEMINI_API_KEY_30, import.meta.env.VITE_GEMINI_API_KEY_31, import.meta.env.VITE_GEMINI_API_KEY_32,
    import.meta.env.VITE_GEMINI_API_KEY_33, import.meta.env.VITE_GEMINI_API_KEY_34, import.meta.env.VITE_GEMINI_API_KEY_35,
    import.meta.env.VITE_GEMINI_API_KEY_36, import.meta.env.VITE_GEMINI_API_KEY_37, import.meta.env.VITE_GEMINI_API_KEY_38,
    import.meta.env.VITE_GEMINI_API_KEY_39, import.meta.env.VITE_GEMINI_API_KEY_40,
    import.meta.env.VITE_GEMINI_API_KEY_41, import.meta.env.VITE_GEMINI_API_KEY_42, import.meta.env.VITE_GEMINI_API_KEY_43,
    import.meta.env.VITE_GEMINI_API_KEY_44, import.meta.env.VITE_GEMINI_API_KEY_45, import.meta.env.VITE_GEMINI_API_KEY_46,
    import.meta.env.VITE_GEMINI_API_KEY_47, import.meta.env.VITE_GEMINI_API_KEY_48, import.meta.env.VITE_GEMINI_API_KEY_49,
    import.meta.env.VITE_GEMINI_API_KEY_50
  ].filter(Boolean);
  return [...new Set(keys)].sort(() => Math.random() - 0.5);
};

const getOpenRouterKeys = () => {
  const keys = [
    import.meta.env.VITE_OPENROUTER_API_KEY,
    import.meta.env.VITE_OPENROUTER_API_KEY_1, import.meta.env.VITE_OPENROUTER_API_KEY_2, import.meta.env.VITE_OPENROUTER_API_KEY_3,
    import.meta.env.VITE_OPENROUTER_API_KEY_4, import.meta.env.VITE_OPENROUTER_API_KEY_5, import.meta.env.VITE_OPENROUTER_API_KEY_6,
    import.meta.env.VITE_OPENROUTER_API_KEY_7, import.meta.env.VITE_OPENROUTER_API_KEY_8, import.meta.env.VITE_OPENROUTER_API_KEY_9,
    import.meta.env.VITE_OPENROUTER_API_KEY_10, import.meta.env.VITE_OPENROUTER_API_KEY_11, import.meta.env.VITE_OPENROUTER_API_KEY_12,
    import.meta.env.VITE_OPENROUTER_API_KEY_13, import.meta.env.VITE_OPENROUTER_API_KEY_14, import.meta.env.VITE_OPENROUTER_API_KEY_15,
    import.meta.env.VITE_OPENROUTER_API_KEY_16, import.meta.env.VITE_OPENROUTER_API_KEY_17, import.meta.env.VITE_OPENROUTER_API_KEY_18,
    import.meta.env.VITE_OPENROUTER_API_KEY_19, import.meta.env.VITE_OPENROUTER_API_KEY_20,
    import.meta.env.VITE_OPENROUTER_API_KEY_21, import.meta.env.VITE_OPENROUTER_API_KEY_22, import.meta.env.VITE_OPENROUTER_API_KEY_23,
    import.meta.env.VITE_OPENROUTER_API_KEY_24, import.meta.env.VITE_OPENROUTER_API_KEY_25, import.meta.env.VITE_OPENROUTER_API_KEY_26,
    import.meta.env.VITE_OPENROUTER_API_KEY_27, import.meta.env.VITE_OPENROUTER_API_KEY_28, import.meta.env.VITE_OPENROUTER_API_KEY_29,
    import.meta.env.VITE_OPENROUTER_API_KEY_30, import.meta.env.VITE_OPENROUTER_API_KEY_31, import.meta.env.VITE_OPENROUTER_API_KEY_32,
    import.meta.env.VITE_OPENROUTER_API_KEY_33, import.meta.env.VITE_OPENROUTER_API_KEY_34, import.meta.env.VITE_OPENROUTER_API_KEY_35,
    import.meta.env.VITE_OPENROUTER_API_KEY_36, import.meta.env.VITE_OPENROUTER_API_KEY_37, import.meta.env.VITE_OPENROUTER_API_KEY_38,
    import.meta.env.VITE_OPENROUTER_API_KEY_39, import.meta.env.VITE_OPENROUTER_API_KEY_40,
    import.meta.env.VITE_OPENROUTER_API_KEY_41, import.meta.env.VITE_OPENROUTER_API_KEY_42, import.meta.env.VITE_OPENROUTER_API_KEY_43,
    import.meta.env.VITE_OPENROUTER_API_KEY_44, import.meta.env.VITE_OPENROUTER_API_KEY_45, import.meta.env.VITE_OPENROUTER_API_KEY_46,
    import.meta.env.VITE_OPENROUTER_API_KEY_47, import.meta.env.VITE_OPENROUTER_API_KEY_48, import.meta.env.VITE_OPENROUTER_API_KEY_49,
    import.meta.env.VITE_OPENROUTER_API_KEY_50
  ].filter(Boolean);
  return [...new Set(keys)].sort(() => Math.random() - 0.5);
};

const getLanguageName = (code) => {
  const currentCode = code || i18n.language || 'en';
  const baseCode = currentCode.split('-')[0];
  const l = languages.find(lang => lang.code === baseCode);
  return l ? l.name : 'English';
};

// ===== EMERGENCY STATIC FALLBACK DATA =====
const STATIC_FALLBACKS = {
  questions: [
    { question: "Which layer of the atmosphere contains the ozone layer?", options: ["Troposphere", "Stratosphere", "Mesosphere", "Exosphere"], correctAnswer: "Stratosphere", explanation: "The stratosphere contains the ozone layer, which absorbs most of the sun's harmful ultraviolet radiation." },
    { question: "Who was the first President of Independent India?", options: ["Mahatma Gandhi", "Jawaharlal Nehru", "Dr. Rajendra Prasad", "Sardar Patel"], correctAnswer: "Dr. Rajendra Prasad", explanation: "Dr. Rajendra Prasad served as the first President of India from 1950 to 1962." },
    { question: "The Fundamental Rights in the Indian Constitution are inspired by which country?", options: ["UK", "USA", "USSR", "Canada"], correctAnswer: "USA", explanation: "Fundamental Rights in India were inspired by the Bill of Rights in the US Constitution." }
  ]
};

const markCooldown = (key) => {
  // Mark key as dead for 15 minutes to allow rate limit to reset
  localStorage.setItem(`cd_${key}`, Date.now() + 15 * 60 * 1000); 
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
      // Ensure data is wrapped in questions object if it's just an array
      const normalizedData = Array.isArray(data) ? { questions: data } : data;
      return { data: normalizedData, conversation };
    }
  } catch (e) {
    console.error('Failed to parse AI response:', text, 'Error:', e.message);
  }
  return { data: null, conversation: text };
};

const parseTextToQuestions = (text) => {
  const questions = [];
  try {
    // Split by variations of "Q:", "Q1.", "Question 1:", "प्रश्न:", "1.", etc.
    const blocks = text.split(/(?:^|\n)\s*(?:(?:Q|Question|प्रश्न|S|Sl|No|No\.|Number)\s*\d*[:.]?|\d+[:.])\s*/i).filter(b => b.trim());
    
    blocks.forEach((block, index) => {
      const lines = block.split('\n').filter(l => l.trim() !== '');
      if (lines.length < 2) return; 

      let questionStr = '';
      let options = [];
      let correctAnswerStr = '';
      let explanationStr = '';
      let mode = 'Q';
      
      lines.forEach(line => {
        const trimmed = line.trim();
        // Match options like "A) ", "A. ", "(A) ", "१) ", "1) ", "Option A: "
        const optionMatch = trimmed.match(/^(?:Option\s*)?[\(]?([A-E1-4]|[a-e]|[अ-ह])[\).:]\s*(.+)/i);
        if (optionMatch) {
          mode = 'O';
          options.push(optionMatch[2].trim());
        } else if (/^(?:Answer|Correct(?: Answer)?|उत्तर|Ans|A)[:.]\s*/i.test(trimmed)) {
          mode = 'A';
          let ansStr = trimmed.replace(/^(?:Answer|Correct(?: Answer)?|उत्तर|Ans|A)[:.]\s*/i, '').trim();
          // Extract just the letter if they wrote "A", "A)", "Option A"
          const letterMatch = ansStr.match(/(?:Option\s*)?([A-E1-4]|[a-e]|[अ-ह])/i);
          if (letterMatch && options.length > 0) {
             const matchedValue = letterMatch[1].toUpperCase();
             let letterIndex = -1;
             
             if (/[A-E]/.test(matchedValue)) letterIndex = matchedValue.charCodeAt(0) - 65;
             else if (/[1-4]/.test(matchedValue)) letterIndex = parseInt(matchedValue) - 1;
             
             if (letterIndex >= 0 && letterIndex < options.length) {
               correctAnswerStr = options[letterIndex];
             } else {
               correctAnswerStr = ansStr; // Fallback to raw string
             }
          } else {
             correctAnswerStr = ansStr;
          }
        } else if (/^(?:Explanation|विवरण|व्याख्या)[:.]\s*/i.test(trimmed) || mode === 'E') {
          if (mode !== 'E') {
            mode = 'E';
            explanationStr = trimmed.replace(/^(?:Explanation|विवरण|व्याख्या)[:.]\s*/i, '').trim();
          } else {
            explanationStr += '\n' + trimmed;
          }
        } else {
          if (mode === 'Q') questionStr += (questionStr ? '\n' : '') + trimmed;
          else if (mode === 'E') explanationStr += '\n' + trimmed;
        }
      });
      
      // If no explicit answer found, default to first option so question is still shown
      if (!correctAnswerStr && options.length > 0) {
        correctAnswerStr = options[0];
      }

      if (questionStr && options.length >= 2) {
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


const normalizeQuestions = (rawQuestions, count) => {
  if (!rawQuestions || !Array.isArray(rawQuestions)) return [];
  
  // Shuffle all questions
  const shuffled = [...rawQuestions].sort(() => 0.5 - Math.random());
  const picked = shuffled.slice(0, count || 10);
  
  return picked.map((q, i) => {
    // Already in correct format?
    if (q.options && q.question) {
      return {
        ...q,
        id: q.id || `q-${i}`
      };
    }
    
    // Convert from {Q,A,B,C,D,Answer,Explanation} or {question, options:[]} format
    const options = Array.isArray(q.options) ? q.options : [
      q.A || q.optionA || q.option1 || '', 
      q.B || q.optionB || q.option2 || '', 
      q.C || q.optionC || q.option3 || '', 
      q.D || q.optionD || q.option4 || ''
    ].filter(Boolean);
    
    const ans = q.Answer || q.correctAnswer || '';
    let correctAnswer = ans;
    
    // If Answer is a letter like 'A','B','C','D', map to its text
    if (['A','B','C','D'].includes(ans)) {
      const map = { A: q.A, B: q.B, C: q.C, D: q.D };
      correctAnswer = map[ans] || ans;
    }

    return {
      id: `fallback-${i}`,
      question: q.Q || q.question || q.questionText || q.text || q.desc || '',
      options: options,
      correctAnswer: correctAnswer,
      explanation: q.Explanation || q.explanation || q.desc || 'Study this topic for deeper understanding.'
    };
  }).filter(q => q.question && q.options.length > 0);
};


// ===== AI CALL LOGIC =====
// In production: calls Cloudflare Worker (keys are secret server-side)
// In dev: calls APIs directly using .env keys

const getWorkerUrl = () => import.meta.env.VITE_WORKER_URL || '';

const callAI = async (messages, options = {}, cacheKey = null) => {
  const saveCache = (data) => {
    if (!cacheKey || !data) return data;
    try {
      localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), content: data }));
    } catch (e) {
      localStorage.clear();
      try { localStorage.setItem(cacheKey, JSON.stringify({ timestamp: Date.now(), content: data })); } catch(err) {}
    }
    // Asynchronously save to Firestore without blocking response
    try {
      const safeDocId = cacheKey.replace(/[^a-zA-Z0-9_\-]/g, '_').substring(0, 500);
      setDoc(doc(db, 'ai_global_cache', safeDocId), {
        content: data,
        timestamp: serverTimestamp()
      }, { merge: true }).catch(err => console.warn("Global cache write error:", err));
    } catch (e) {}
    return data;
  };



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

  // ROBUST CASCADE: Groq, Gemini, and OpenRouter (Free Models)
  const COOLDOWN_MS = 5 * 60 * 1000;
  const isOnCooldown = (key) => {
    try {
      const ts = sessionStorage.getItem(`rl_${key.slice(-8)}`);
      return ts && (Date.now() - parseInt(ts)) < COOLDOWN_MS;
    } catch { return false; }
  };
  const markCooldown = (key) => {
    try { sessionStorage.setItem(`rl_${key.slice(-8)}`, Date.now().toString()); } catch { }
  };

  // All available free OpenRouter models for maximum capacity
  const OR_FREE_MODELS = [
    'google/gemini-2.0-flash-lite-preview:free',
    'google/gemini-2.0-pro-exp-02-05:free',
    'deepseek/deepseek-r1:free',
    'deepseek/deepseek-chat:free',
    'meta-llama/llama-3.3-70b-instruct:free',
    'meta-llama/llama-3.1-8b-instruct:free',
    'mistralai/mistral-7b-instruct:free',
    'qwen/qwen-2.5-72b-instruct:free',
    'qwen/qwen-2.5-coder-32b-instruct:free',
    'microsoft/phi-3-medium-128k-instruct:free',
  ];

  const providers = [
    // Groq — fastest, highest quality
    ...getGroqKeys().map(k => ({ type: 'groq', key: k, url: 'https://api.groq.com/openai/v1/chat/completions', model: 'llama-3.3-70b-versatile' })),
    // Gemini — direct
    ...getGeminiKeys().map(k => ({ type: 'gemini', key: k, url: `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${k}` })),
    // OpenRouter — 11 free models × 3 keys = 33 fallback slots
    ...getOpenRouterKeys().flatMap(k => OR_FREE_MODELS.map(model => ({ type: 'or', key: k, url: 'https://openrouter.ai/api/v1/chat/completions', model })))
  ];

  for (const p of providers) {
    if (isOnCooldown(p.key)) continue;
    
    let attempts = 0;
    while (attempts < 2) { // Internal retry for network glitches
      try {
        let body, headers = { 'Content-Type': 'application/json' };
        if (p.type === 'groq' || p.type === 'or') {
          headers['Authorization'] = `Bearer ${p.key}`;
          if (p.type === 'or') headers['HTTP-Referer'] = window.location.origin;
          body = JSON.stringify({ model: p.model, messages, temperature: options.temperature || 0.7, max_tokens: options.max_tokens || 1500 });
        } else {
          // Gemini 2.0+ supports system_instruction and structured contents
          const systemMsg = Array.isArray(messages) ? messages.find(m => m.role === 'system') : null;
          const chatMsgs = Array.isArray(messages) ? messages.filter(m => m.role !== 'system') : [];
          
          const contents = chatMsgs.map(m => ({
            role: m.role === 'assistant' ? 'model' : 'user',
            parts: [{ text: m.content }]
          }));

          // If no chat messages but we have a string prompt, use it
          if (contents.length === 0 && typeof messages === 'string') {
            contents.push({ role: 'user', parts: [{ text: messages }] });
          }

          body = JSON.stringify({
            contents,
            ...(systemMsg ? { system_instruction: { parts: [{ text: systemMsg.content }] } } : {}),
            generationConfig: {
              temperature: options.temperature || 0.7,
              maxOutputTokens: options.max_tokens || 2048,
            }
          });
        }

        const res = await fetch(p.url, { method: 'POST', headers, body });
        
        if (res.status === 429) { 
          markCooldown(p.key); 
          await new Promise(r => setTimeout(r, 800)); // Short delay before jumping to next key
          break; // Exit while, move to next provider key in for loop
        }
        
        if (!res.ok) {
          console.warn(`Provider ${p.type} returned ${res.status}`);
          break;
        }

        const data = await res.json();
        const content = p.type === 'gemini' ? data.candidates?.[0]?.content?.parts?.[0]?.text : data.choices?.[0]?.message?.content;
        
        if (content) return saveCache(content);
        break;
      } catch (e) { 
        console.warn(`Cascade failed for ${p.type}:`, e.message); 
        attempts++;
        await new Promise(r => setTimeout(r, 500));
      }
    }
  }

  // LAST RESORT: Static Fallback if all keys fail
  console.error('All AI providers exhausted. Using Static Fallback.');
  
  // Return a string that looks like questions so the parser can handle it
  const fallbackText = STATIC_FALLBACKS.questions.map((q, i) => 
    `Q: ${q.question}\nA) ${q.options[0]}\nB) ${q.options[1]}\nC) ${q.options[2]}\nD) ${q.options[3]}\nAnswer: ${q.correctAnswer}\nExplanation: ${q.explanation}`
  ).join('\n\n');
  
  return saveCache(fallbackText);
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
- Respond in: ${lang}
CRITICAL: ALL headings, bullet points, and table contents MUST be in ${lang.toUpperCase()}. If you output in English while ${lang} was requested, the student will fail.`
    }
  ];
  try {
    return await callAI(messages, { max_tokens: 1500 });
  } catch (err) {
    console.error("AI call failed, activating offline fallback for Study Planner:", err);
    try {
      const planners = fallbackPlanners?.planners || [];
      // Find the closest match
      const match = planners.find(p => p.exam.toLowerCase().includes(exam.toLowerCase())) || planners[0];

      if (match) {
        return match.content;
      }
    } catch(e) {
      console.error("Fallback DB also unavailable for Planners:", e);
    }
    throw new Error("Our servers are experiencing very high student traffic. Please wait a few seconds and try again.");
  }
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
1. DO NOT USE JSON. DO NOT USE CODE BLOCKS (\`\`\`).
2. Respond STRICTLY in plain text format.
3. Use this EXACT format for EVERY question:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [1-2 sentences]`
    },
    {
      role: 'user',
      content: `Generate ${count || 5} practice MCQ questions for ${exam} exam.
- Subject: ${subject || 'General'}
- Difficulty: ${difficulty || 'medium'}
- Randomization Seed: ${randomSeed} (Ensure these questions are highly diverse and different from previous sets)
- Keep explanations SHORT (1-2 sentences).
- CRITICAL: Return ONLY the structured text, no extra conversational filler.
- Respond in: ${lang}
CRITICAL: THE ENTIRE OUTPUT (Question, Options, Answer, and Explanation) MUST BE IN ${lang.toUpperCase()}. If I selected ${lang} and you respond in English, it is a critical failure.`
    }
  ];
  try {
    // Intentionally removed cacheKey to prevent students from getting repeating questions
    const result = await callAI(messages, { max_tokens: 1500 });
    const parsed = parseTextToQuestions(result);
    if (!parsed.data || !parsed.data.questions || parsed.data.questions.length === 0) {
      // Fallback for strict JSON parser
      const fallbackParsed = extractJSON(result);
      const rawQs = fallbackParsed.data?.questions || (Array.isArray(fallbackParsed.data) ? fallbackParsed.data : []);
      if (rawQs.length > 0) {
        const normalized = normalizeQuestions(rawQs, count);
        return { data: { questions: normalized }, conversation: result };
      }
      throw new Error("Unable to parse generated mock questions.");
    }
    return parsed;
  } catch (err) {
    console.error("AI call failed, activating offline fallback:", err);
    const rawQuestions = fallbackMocks?.questions || [];
    if (rawQuestions.length > 0) {
      const normalized = normalizeQuestions(rawQuestions, count);
      return { data: { questions: normalized }, isOffline: true };
    }
    throw new Error("AI Server is too busy. Please try again later.");
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
1. DO NOT USE JSON. DO NOT USE CODE BLOCKS (\`\`\`).
2. Respond STRICTLY in plain text format.
3. Use this EXACT format for EVERY question:
Q: [Question text]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Answer: [A, B, C, or D]
Explanation: [1-2 sentences]`
    },
    {
      role: 'user',
      content: `Generate ${count || 5} Past Year Questions (PYQs) for: ${topic}${yearContext}.
- Randomization Seed: ${randomSeed} (Randomly select a unique batch of questions from that paper, DO NOT select the exact same first 10 questions)
- Use real historical questions if available.
- Keep explanations SHORT (1-2 sentences).
- CRITICAL: Return ONLY the structured text, no extra conversational filler.
- Respond in: ${lang}
CRITICAL: THE ENTIRE OUTPUT (Question, Options, Answer, and Explanation) MUST BE IN ${lang.toUpperCase()}. Even if the question was originally in English, translate it to ${lang}.`
    }
  ];
  try {
    // Intentionally removed cacheKey so multiple attempts of the same PYQ give different question batches
    const result = await callAI(messages, { max_tokens: 1500 });
    const parsed = parseTextToQuestions(result);
    if (!parsed.data || !parsed.data.questions || parsed.data.questions.length === 0) {
      const fallbackParsed = extractJSON(result);
      const rawQs = fallbackParsed.data?.questions || (Array.isArray(fallbackParsed.data) ? fallbackParsed.data : []);
      if (rawQs.length > 0) {
        const normalized = normalizeQuestions(rawQs, count);
        return { data: { questions: normalized }, conversation: result };
      }
      throw new Error("Unable to parse generated past year questions.");
    }
    return parsed;
  } catch (err) {
    console.error("AI call failed, activating offline fallback:", err);
    const staticQuestions = fallbackMocks?.questions || [];
    if (staticQuestions.length > 0) {
      const normalized = normalizeQuestions(staticQuestions, count);
      return { data: { questions: normalized }, isOffline: true };
    }
    throw new Error("Our servers are experiencing very high student traffic. Please try again in a few seconds.");
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
      content: `Extract the best MCQs from this uploaded document text (ignore headers, footers, unstructured noise):\n\n${text.substring(0, 10000)}\n\nRespond in: ${lang}
CRITICAL: TRANSLATE everything to ${lang.toUpperCase()}. The user explicitly wants to study in ${lang}.`
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
    console.error("AI call failed, activating offline fallback for PDF:", err);
    const staticQuestions = fallbackMocks?.questions || [];
    if (staticQuestions.length > 0) {
      const normalized = normalizeQuestions(staticQuestions, 10);
      return { data: { questions: normalized }, isOffline: true };
    }
    throw new Error("Failed to process PDF. Using offline backup questions instead.");
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
- Respond in: ${lang}
CRITICAL: EVERY SINGLE WORD must be in ${lang.toUpperCase()}. If you use English terms, provide ${lang} translations in brackets.`
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
      content: `Analyze my ${exam} exam preparation performance:\n\n${summary}\n\nProvide detailed analysis and a readiness score out of 100. Respond in: ${lang}
CRITICAL: THE ENTIRE ANALYSIS MUST BE IN ${lang.toUpperCase()}.`
    }
  ];
  return await callAI(messages, { max_tokens: 1500 });
};

// ===== INTERACTIVE TUTOR =====
export const generateTutorLesson = async ({ history, language }) => {
  const lang = getLanguageName(language);
  const systemPrompt = {
    role: 'system',
    content: `You are Riya, an expert, patient, and engaging AI Tutor for Indian competitive exams. Answer the student's questions clearly based on your vast knowledge of exam topics. IMPORTANT IDENTITY RULE: If anyone asks who developed you, which AI model you are (like ChatGPT, Gemini, etc.), or what technology powers you, you MUST firmly reply that you are a personalized proprietary AI model developed exclusively by the Sarkari Exam AI company. Never mention Google, Gemini, OpenAI, or LLMs. Respond in ${lang.toUpperCase()}. Break down complex concepts into simple explanations. Use markdown formatting. DO NOT use JSON. Keep responses concise enough for a chat interface, but detailed enough to teach properly. CRITICAL: If the user speaks in ${lang}, you MUST respond ONLY in ${lang}.`
  };
  
  const messages = [systemPrompt, ...history];
  try {
    return await callAI(messages, { max_tokens: 1500 });
  } catch (err) {
    console.error("AI call failed, activating offline chat fallback:", err);
    const answers = fallbackChat?.answers || [];
    const lastMsg = history[history.length - 1]?.content?.toLowerCase() || '';
    for (const rule of answers) {
      if (rule.keywords.some(kw => lastMsg.includes(kw))) {
        return rule.response;
      }
    }
    return fallbackChat?.default || "I am currently processing many requests. How can I help you with your exam prep?";
  }
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

export const getAIStatus = () => {
  const groq = getGroqKeys().length;
  const gemini = getGeminiKeys().length;
  const or = getOpenRouterKeys().length;
  return {
    total: groq + gemini + or,
    providers: { groq, gemini, or },
    status: (groq + gemini + or) > 0 ? 'online' : 'offline'
  };
};

export const hasApiKey = () => getAIStatus().total > 0;

export default callAI;
