import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

const API_KEY = process.env.OPENROUTER_API_KEY || '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';
const MODEL = 'google/gemini-2.0-flash-lite-001';

if (!process.env.OPENROUTER_API_KEY && API_KEY.includes('sk-or-v1')) {
  console.log('⚠️ Using hardcoded API key. Consider using OPENROUTER_API_KEY environment variable.');
}

// Parse arguments
const countArg = process.argv.find(a => a.startsWith('--count='));
const slotArg = process.argv.find(a => a.startsWith('--slot='));
const POSTS_TO_GENERATE = countArg ? parseInt(countArg.split('=')[1], 10) : 1;
const SLOT_ID = slotArg ? parseInt(slotArg.split('=')[1], 10) : 0;

const KEYWORDS = [
  // Slot 0: General AI
  ['AI', 'Artificial Intelligence', 'Generative AI', 'AI Website', 'AI Robot'],
  // Slot 1: Chatbots
  ['AI Chatbot', 'ChatGPT 4', 'AI Chat', 'AI Chatbot Online', 'ChatGPT 4 AI Chatbot'],
  // Slot 2: Midjourney & Art
  ['Midjourney', 'Midjourney AI', 'AI Generated', 'AI Art Generation', 'DALL-E 3'],
  // Slot 3: OpenAI & Models
  ['OpenAI', 'Open AI', 'AI GPT', 'Conversational AI', 'AI Assistant'],
  // Slot 4: Education & UPSC (Original)
  ['UPSC syllabus 2025 complete guide', 'best AI tool for UPSC preparation', 'UPSC prelims strategy 2025', 'AI civil services exam coach India'],
  // Slot 5: Exams & Jobs
  ['SSC CGL syllabus and best books', 'IBPS PO preparation strategy AI', 'Railway NTPC exam preparation tips', 'government exam study plan for beginners']
];

const TOPIC_IMAGE_MAP = {
  'ai': 'https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&q=80&w=1200',
  'chatbot': 'https://images.unsplash.com/photo-1531746790731-6c087fecd05a?auto=format&fit=crop&q=80&w=1200',
  'midjourney': 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&q=80&w=1200',
  'chatgpt': 'https://images.unsplash.com/photo-1676299081847-824916de030a?auto=format&fit=crop&q=80&w=1200',
  'exam': 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&q=80&w=1200',
  'default': 'https://images.unsplash.com/photo-1485827404703-89b55fcc595e?auto=format&fit=crop&q=80&w=1200'
};

const NEWS_CACHE_PATH = path.join(__dirname, 'news-cache.json');

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

  try {
    const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${API_KEY}`,
        'X-Title': 'Sarkari Exam AI Blog Generator',
      },
      body: JSON.stringify({
        model: MODEL,
        messages: [{ role: 'user', content: prompt }],
        response_format: { type: "json_object" },
        max_tokens: 3000
      }),
    });

    const data = await response.json();
    
    if (data.choices && data.choices[0]) {
      let result = JSON.parse(data.choices[0].message.content.replace(/```json/g, '').replace(/```/g, '').trim());
      
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

      // Image Logic
      const contextText = (target + (result.tags || []).join(' ')).toLowerCase();
      if (contextText.includes('midjourney')) newPost.featuredImage = TOPIC_IMAGE_MAP.midjourney;
      else if (contextText.includes('chatbot') || contextText.includes('chatgpt')) newPost.featuredImage = TOPIC_IMAGE_MAP.chatbot;
      else if (contextText.includes('ai') || contextText.includes('artificial')) newPost.featuredImage = TOPIC_IMAGE_MAP.ai;
      else if (contextText.includes('exam') || contextText.includes('upsc') || contextText.includes('ssc')) newPost.featuredImage = TOPIC_IMAGE_MAP.exam;
      else newPost.featuredImage = TOPIC_IMAGE_MAP.default;

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
    } else {
      console.error('❌ AI returned no content or invalid response.');
      return false;
    }
  } catch (err) {
    console.error('❌ Generator Error:', err.message);
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
      : await generateBlogPost(null, KEYWORDS[SLOT_ID % KEYWORDS.length][Math.floor(Math.random() * 5)]);
    
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
