import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

const GEMINI_API_KEY = process.env.GEMINI_API_KEY || process.env.VITE_GEMINI_API_KEY;
const GROQ_API_KEY = process.env.GROQ_API_KEY || process.env.VITE_GROQ_API_KEY;
const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY || process.env.VITE_OPENROUTER_API_KEY || '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';


// Parse arguments
const countArg = process.argv.find(a => a.startsWith('--count='));
const slotArg = process.argv.find(a => a.startsWith('--slot='));
const POSTS_TO_GENERATE = countArg ? parseInt(countArg.split('=')[1], 10) : 1;
const SLOT_ID = slotArg ? parseInt(slotArg.split('=')[1], 10) : 0;

console.log('--- Environment Check ---');
console.log(`GEMINI_API_KEY: ${GEMINI_API_KEY ? '✅ Present' : '❌ Missing'}`);
console.log(`GROQ_API_KEY: ${GROQ_API_KEY ? '✅ Present' : '❌ Missing'}`);
console.log(`OPENROUTER_API_KEY: ${OPENROUTER_API_KEY ? '✅ Present' : '❌ Missing'}`);


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
  if (!GEMINI_API_KEY) {
    console.log('Skipping Gemini: GEMINI_API_KEY not found.');
    return null;
  }
  // Only use currently available models (1.5-flash and 1.5-flash-8b are deprecated/404)
  const models = [
    { name: 'gemini-2.0-flash', version: 'v1beta' },
    { name: 'gemini-2.0-flash-lite', version: 'v1beta' },
  ];
  
  for (const { name: model, version } of models) {
    for (let attempt = 0; attempt < 2; attempt++) {
      try {
        console.log(`Trying Gemini ${version}/${model} (attempt ${attempt + 1})...`);
        const url = `https://generativelanguage.googleapis.com/${version}/models/${model}:generateContent?key=${GEMINI_API_KEY}`;
        const response = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [{ parts: [{ text: prompt }] }],
            generationConfig: { maxOutputTokens: 3000, temperature: 0.7 }
          })
        });
        
        if (response.status === 429 && attempt === 0) {
          console.warn('⏳ Rate limited — waiting 60 seconds before retry...');
          await new Promise(r => setTimeout(r, 60000));
          continue;
        }

        if (!response.ok) {
          const errBody = await response.text();
          console.warn(`Gemini API HTTP ${response.status}: ${errBody.substring(0, 100)}...`);
          break; // try next model
        }

        const data = await response.json();
        if (data.candidates && data.candidates[0]?.content?.parts[0]?.text) {
          console.log(`✅ Gemini ${version}/${model} Success!`);
          return extractJSON(data.candidates[0].content.parts[0].text);
        }
        if (data.error) {
          console.warn(`Gemini error:`, data.error.message.substring(0, 100));
        }
        break;
      } catch (e) {
        console.warn(`Gemini connection error: ${e.message}`);
        break;
      }
    }
  }
  return null;
}


async function generateWithGroq(prompt) {
  if (!GROQ_API_KEY) {
    console.log('Skipping Groq: GROQ_API_KEY not found.');
    return null;
  }
  // Try multiple Groq models — llama-3.1-8b-instant has much higher free rate limits
  const groqModels = ['llama-3.3-70b-versatile', 'llama-3.1-8b-instant'];
  
  for (const model of groqModels) {
    try {
      console.log(`Requesting from Groq (${model})...`);
      const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${GROQ_API_KEY}` },
        body: JSON.stringify({
          model,
          messages: [{ role: 'user', content: prompt }],
          temperature: 0.7,
          max_tokens: 3000
        })
      });
      
      if (!response.ok) {
        const errBody = await response.text();
        console.warn(`Groq (${model}) HTTP ${response.status}: ${errBody.substring(0, 100)}...`);
        continue; // try next model
      }

      const data = await response.json();
      if (data.choices && data.choices[0]?.message?.content) {
        console.log(`✅ Groq (${model}) Success!`);
        return extractJSON(data.choices[0].message.content);
      }
    } catch (e) {
      console.error(`Groq (${model}) connection error:`, e.message);
    }
  }
  return null;
}


async function generateWithOpenRouter(prompt) {
  if (!OPENROUTER_API_KEY) {
    console.log('Skipping OpenRouter: OPENROUTER_API_KEY not found.');
    return null;
  }
  try {
    console.log('Requesting from OpenRouter (google/gemini-2.0-flash-lite-001)...');
    const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json', 
        'Authorization': `Bearer ${OPENROUTER_API_KEY}`,
        'X-Title': 'Sarkari Exam AI Blog Bot'
      },
      body: JSON.stringify({
        model: 'google/gemini-2.0-flash-lite-001',
        messages: [{ role: 'user', content: prompt }],
        temperature: 0.7,
        max_tokens: 2000
      })
    });

    if (!response.ok) {
      const errBody = await response.text();
      console.warn(`OpenRouter API HTTP ${response.status}: ${errBody.substring(0, 100)}...`);
      return null;
    }

    const data = await response.json();
    if (data.choices && data.choices[0]?.message?.content) {
      console.log('✅ OpenRouter Success!');
      return extractJSON(data.choices[0].message.content);
    }
    return null;
  } catch (e) {
    console.error('OpenRouter connection error:', e.message);
    return null;
  }
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
