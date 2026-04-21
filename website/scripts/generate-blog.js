import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

// --- DYNAMIC KEY DISCOVERY ---
const discoverKeys = (prefix) => {
  const keys = [];
  const primary = process.env[prefix] || process.env[`VITE_${prefix}`];
  if (primary) keys.push(primary);
  
  // Look for indexed keys: PREFIX_1, PREFIX_2...
  for (let i = 1; i <= 10; i++) {
    const k = process.env[`${prefix}_${i}`] || process.env[`VITE_${prefix}_${i}`];
    if (k && !keys.includes(k)) keys.push(k);
  }
  return keys;
};

const GEMINI_KEYS = discoverKeys('GEMINI_API_KEY');
const GROQ_KEYS = discoverKeys('GROQ_API_KEY');
const OPENROUTER_KEYS = discoverKeys('OPENROUTER_API_KEY');

// Fallback to hardcoded OpenRouter if none found
if (OPENROUTER_KEYS.length === 0) {
  OPENROUTER_KEYS.push('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');
}


// Parse arguments
const countArg = process.argv.find(a => a.startsWith('--count='));
const slotArg = process.argv.find(a => a.startsWith('--slot='));
const POSTS_TO_GENERATE = countArg ? parseInt(countArg.split('=')[1], 10) : 1;
const SLOT_ID = slotArg ? parseInt(slotArg.split('=')[1], 10) : 0;

console.log('--- Environment Check ---');
console.log(`GEMINI_KEYS: ${GEMINI_KEYS.length} keys found`);
console.log(`GROQ_KEYS: ${GROQ_KEYS.length} keys found`);
console.log(`OPENROUTER_KEYS: ${OPENROUTER_KEYS.length} keys found`);


const KEYWORDS = [
  'AI chatbot', 'AI', 'Midjourney', 'Artificial Intelligence', 'Midjourney AI',
  'Chat GPT 4', 'AI chat', 'OpenAI', 'Open AI', 'Generative AI',
  'AI website', 'AI chatbot online', 'Conversational AI', 'AI assistant',
  'AI generated', 'AI GPT', 'AI robot', 'Chat GPT4'
];


const REAL_IMAGE_KEYWORDS = {
  'chatbot': 'robotics customer service',
  'ai': 'data science technology future',
  'midjourney': 'abstract 3d art professional',
  'chatgpt': 'programming keyboard artificial intelligence',
  'openai': 'silicon valley innovation',
  'exam': 'student studying library india',
  'robot': 'humanoid robot factory future',
  'default': 'minimal technology office'
};

const getRealImageUrl = (topic) => {
  const keyword = REAL_IMAGE_KEYWORDS[topic.toLowerCase()] || REAL_IMAGE_KEYWORDS.default;
  const randomSig = Math.floor(Math.random() * 1000);
  return `https://images.unsplash.com/photo-1?auto=format&fit=crop&q=80&w=1200&sig=${randomSig}&q=${encodeURIComponent(keyword)}`;
};
const NEWS_CACHE_PATH = path.join(__dirname, 'news-cache.json');

function extractJSON(text) {
  const raw = text.replace(/```json/gi, '').replace(/```/g, '').trim();

  // Strategy 1: Direct parse — try cleanly first
  try {
    const s = raw.indexOf('{'), e = raw.lastIndexOf('}');
    if (s !== -1 && e !== -1) return JSON.parse(raw.substring(s, e + 1));
  } catch (_) {}

  // Strategy 2: Fix ONLY real control characters (safe — does NOT touch single quotes or HTML)
  try {
    const s = raw.indexOf('{'), e = raw.lastIndexOf('}');
    if (s !== -1 && e !== -1) {
      const fixed = raw.substring(s, e + 1).replace(/[\u0000-\u001F\u007F]/g, (ch) => {
        if (ch === '\n') return '\\n';
        if (ch === '\r') return '\\r';
        if (ch === '\t') return '\\t';
        return '';
      });
      return JSON.parse(fixed);
    }
  } catch (_) {}

  // Strategy 3: Fix trailing commas + control chars
  try {
    const s = raw.indexOf('{'), e = raw.lastIndexOf('}');
    if (s !== -1 && e !== -1) {
      const fixed = raw.substring(s, e + 1)
        .replace(/,\s*([}\]])/g, '$1')               // remove trailing commas
        .replace(/[\u0000-\u001F\u007F]/g, (ch) => {
          if (ch === '\n') return '\\n';
          if (ch === '\r') return '\\r';
          if (ch === '\t') return '\\t';
          return '';
        });
      return JSON.parse(fixed);
    }
  } catch (_) {}

  // Strategy 4: Regex field extraction as last resort
  try {
    const get = (key) => {
      const m = raw.match(new RegExp(`"${key}"\\s*:\\s*"((?:[^"\\\\]|\\\\.)*)"`));
      return m ? m[1] : '';
    };
    const getArr = (key) => {
      const m = raw.match(new RegExp(`"${key}"\\s*:\\s*(\\[[^\\]]*\\])`));
      try { return m ? JSON.parse(m[1]) : []; } catch (_) { return []; }
    };
    const title = get('title');
    if (!title) throw new Error('No title found');
    return {
      title,
      excerpt: get('excerpt'),
      content: get('content').replace(/\\n/g, '\n'),
      tags: getArr('tags'),
      faqSchema: []
    };
  } catch (_) {}

  console.error('All JSON extraction strategies failed.');
  return null;
}

async function generateWithGemini(prompt) {
  if (GEMINI_KEYS.length === 0) return null;

  const models = [
    { name: 'gemini-1.5-flash', version: 'v1beta' },
    { name: 'gemini-2.0-flash', version: 'v1beta' },
  ];
  
  // Rotate through all keys
  for (const key of GEMINI_KEYS) {
    for (const { name: model, version } of models) {
      try {
        console.log(`Trying Gemini ${model} with Key ${key.substring(0, 8)}...`);
        const url = `https://generativelanguage.googleapis.com/${version}/models/${model}:generateContent?key=${key}`;
        const response = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{ parts: [{ text: prompt }] }],
            generationConfig: { maxOutputTokens: 3000, temperature: 0.7 }
          })
        });
        
        if (response.status === 429) {
          console.warn('⏳ Rate limited — switching to next key/model...');
          continue; 
        }

        if (!response.ok) {
          const errBody = await response.text();
          console.warn(`Gemini HTTP ${response.status}: ${errBody.substring(0, 100)}`);
          continue;
        }

        const data = await response.json();
        if (data.candidates?.[0]?.content?.parts?.[0]?.text) {
          return extractJSON(data.candidates[0].content.parts[0].text);
        }
      } catch (e) {
        console.warn(`Gemini Error: ${e.message}`);
      }
    }
  }
  return null;
}


async function generateWithGroq(prompt) {
  if (GROQ_KEYS.length === 0) return null;

  const groqModels = ['llama-3.3-70b-versatile', 'llama-3.1-8b-instant'];
  
  for (const key of GROQ_KEYS) {
    for (const model of groqModels) {
      try {
        console.log(`Trying Groq ${model} with Key ${key.substring(0, 8)}...`);
        const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${key}` },
          body: JSON.stringify({
            model,
            messages: [{ role: 'user', content: prompt }],
            temperature: 0.7,
            max_tokens: 3000
          })
        });
        
        if (response.status === 429) {
          console.warn('⏳ Groq Rate Limited — trying next key...');
          break; // Try next key
        }

        if (response.ok) {
          const data = await response.json();
          if (data.choices?.[0]?.message?.content) {
            return extractJSON(data.choices[0].message.content);
          }
        }
      } catch (e) {
        console.error(`Groq Error:`, e.message);
      }
    }
  }
  return null;
}


async function generateWithOpenRouter(prompt) {
  if (OPENROUTER_KEYS.length === 0) return null;

  for (const key of OPENROUTER_KEYS) {
    try {
      console.log(`Trying OpenRouter with Key ${key.substring(0, 8)}...`);
      const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json', 
          'Authorization': `Bearer ${key}`,
          'X-Title': 'Sarkari Exam AI Blog Bot'
        },
        body: JSON.stringify({
          model: 'google/gemini-2.0-flash-lite-001',
          messages: [{ role: 'user', content: prompt }],
          temperature: 0.7,
          max_tokens: 2000
        })
      });

      if (response.status === 429) continue;
      if (response.ok) {
        const data = await response.json();
        if (data.choices?.[0]?.message?.content) {
          return extractJSON(data.choices[0].message.content);
        }
      }
    } catch (e) {
      console.error('OpenRouter Error:', e.message);
    }
  }
  return null;
}


async function generateBlogPost(newsItem = null, fallbackKeyword = null) {
  const isNews = !!newsItem;
  const target = isNews ? newsItem.title : fallbackKeyword;
  
  console.log(`🤖 Generating ${isNews ? 'NEWS' : 'KEYWORD'} post for: "${target}"...`);

  const prompt = `
  You are a senior SEO content writer for Sarkari Exam AI — India's leading AI-powered educational hub.
  
  ${isNews ? 
    `Write a helpful news analysis blog post based on this trending headline: "${newsItem.title}".
     Context: This news is under the category "${newsItem.source}".` 
    : 
    `Write a comprehensive blog post targeting the search keyword: "${fallbackKeyword}".`}

  Strictly follow Google's E-E-A-T guidelines:
  - Provide expert-level insights and practical value for Indian students and aspirants.
  - If it's a news item, explain "Why this matters for your exam preparation" or "Future implications for AI".
  - Structure with 4-5 detailed H2/H3 sections.
  - Include a "How Sarkari Exam AI Helps" section.
  - Include an FAQ section with 3-4 schema-ready questions.
  
  Format the response as a JSON object:
  {
    "title": "SEO Title (60-70 chars)",
    "excerpt": "Meta description (150 chars)",
    "content": "Full HTML content (no markdown code blocks)",
    "tags": ["tag1", "tag2", "tag3"],
    "faqSchema": [
      {"question": "...", "answer": "..."},
      {"question": "...", "answer": "..." }
    ]
  }
  
  Only output valid JSON.
  `;

  let result = null;

  // Cascade: Gemini (Direct) -> Groq -> OpenRouter
  console.log('--- Provider 1: Gemini Direct ---');
  result = await generateWithGemini(prompt);
  
  if (!result) {
    console.log('--- Provider 2: Groq ---');
    result = await generateWithGroq(prompt);
  }
  
  if (!result) {
    console.log('--- Provider 3: OpenRouter ---');
    result = await generateWithOpenRouter(prompt);
  }

  if (result) {
    try {
      const newPost = {
        ...result,
        id: Date.now().toString() + Math.random().toString(36).slice(2, 7),
        date: new Date().toISOString(),
        slug: result.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)+/g, ''),
        readTime: Math.ceil((result.content || '').split(' ').length / 200) + ' min read',
        sourceUrl: newsItem?.link || null,
        sourceName: newsItem?.source || null,
        isNews: isNews
      };

      // Image Logic - Enhanced with real photography
      const contextText = (target + (result.tags || []).join(' ')).toLowerCase();
      if (contextText.includes('midjourney')) newPost.featuredImage = getRealImageUrl('midjourney');
      else if (contextText.includes('chatbot') || contextText.includes('chatgpt')) newPost.featuredImage = getRealImageUrl('chatbot');
      else if (contextText.includes('robot')) newPost.featuredImage = getRealImageUrl('robot');
      else if (contextText.includes('ai') || contextText.includes('artificial')) newPost.featuredImage = getRealImageUrl('ai');
      else if (contextText.includes('exam') || contextText.includes('upsc') || contextText.includes('ssc')) newPost.featuredImage = getRealImageUrl('exam');
      else newPost.featuredImage = getRealImageUrl('default');

      let posts = [];
      if (fs.existsSync(BLOG_DATA_PATH)) {
        posts = JSON.parse(fs.readFileSync(BLOG_DATA_PATH, 'utf8'));
      }
      
      if (!posts.find(p => p.slug === newPost.slug)) {
        posts.unshift(newPost);
      }
      
      if (posts.length > 150) posts.length = 150; 
      fs.writeFileSync(BLOG_DATA_PATH, JSON.stringify(posts, null, 2));
      console.log(`✅ Saved: "${newPost.title}"`);
      return true;
    } catch (err) {
      console.error('❌ Formatting Error:', err.message);
      return false;
    }
  } else {
    console.error('❌ All providers failed.');
    return false;
  }
}

async function main() {
  let news = [];
  if (fs.existsSync(NEWS_CACHE_PATH)) {
    try {
      news = JSON.parse(fs.readFileSync(NEWS_CACHE_PATH, 'utf8'));
    } catch (e) {
      console.warn('⚠️ Could not read news cache.');
    }
  }

  console.log(`\n🚀 Slot ${SLOT_ID}: Generating ${POSTS_TO_GENERATE} post(s)...\n`);
  let successCount = 0;
  
  for (let i = 0; i < POSTS_TO_GENERATE; i++) {
    const newsIdx = (SLOT_ID + i) % (news.length || 1);
    const currentNews = news.length > 0 ? news[newsIdx] : null;
    
    const success = currentNews 
      ? await generateBlogPost(currentNews) 
      : await generateBlogPost(null, KEYWORDS[SLOT_ID % KEYWORDS.length]);

    
    if (success) successCount++;
    if (i < POSTS_TO_GENERATE - 1) await new Promise(r => setTimeout(r, 2000));
  }

  if (successCount === 0) {
    console.error('❌ All blog generations failed.');
    process.exit(1);
  }
  console.log(`\n✨ Finished: ${successCount}/${POSTS_TO_GENERATE} posts generated successfully.\n`);
}

main().catch(err => {
  console.error('🔥 Global failure:', err);
  process.exit(1);
});
