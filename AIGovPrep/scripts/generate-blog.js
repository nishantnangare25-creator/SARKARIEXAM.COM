import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

// Security: Use environment variable for API Key
const API_KEY = process.env.OPENROUTER_API_KEY || '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';
const MODEL = 'google/gemini-2.0-flash-lite-001';

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

async function generateBlogPost(keyword) {
  console.log(`🤖 Generating SEO blog post for keyword: "${keyword}"...`);

  const prompt = `
  You are a senior SEO content writer for Sarkari Exam AI — India's leading AI-powered educational hub.
  
  Write a comprehensive blog post targeting the search keyword: "${keyword}".
  
  Strictly follow Google's E-E-A-T guidelines:
  - Provide expert-level insights and practical value.
  - Structure with 4-5 detailed H2/H3 sections.
  - Include a "How Sarkari Exam AI Helps" section linking the topic to our platform.
  - Include an FAQ section with 3-4 schema-ready questions.
  - Use formal yet engaging tone.
  
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
        readTime: Math.ceil(result.content.split(' ').length / 200) + ' min read',
        keyword: keyword
      };

      // Enhanced Image Logic
      const lowerK = keyword.toLowerCase();
      if (lowerK.includes('midjourney')) newPost.featuredImage = TOPIC_IMAGE_MAP.midjourney;
      else if (lowerK.includes('chatbot') || lowerK.includes('chat gpt')) newPost.featuredImage = TOPIC_IMAGE_MAP.chatbot;
      else if (lowerK.includes('ai') || lowerK.includes('artificial')) newPost.featuredImage = TOPIC_IMAGE_MAP.ai;
      else if (lowerK.includes('exam') || lowerK.includes('upsc') || lowerK.includes('ssc')) newPost.featuredImage = TOPIC_IMAGE_MAP.exam;
      else newPost.featuredImage = TOPIC_IMAGE_MAP.default;

      let posts = [];
      if (fs.existsSync(BLOG_DATA_PATH)) {
        posts = JSON.parse(fs.readFileSync(BLOG_DATA_PATH, 'utf8'));
      }
      
      if (!posts.find(p => p.slug === newPost.slug)) {
        posts.unshift(newPost);
      }
      
      if (posts.length > 100) posts.length = 100; // Increased limit for more daily posts

      fs.writeFileSync(BLOG_DATA_PATH, JSON.stringify(posts, null, 2));
      console.log(`✅ Saved: "${newPost.title}"`);
      return true;
    }
  } catch (err) {
    console.error('❌ Generator Error:', err.message);
    return false;
  }
}

async function main() {
  const currentKeywords = KEYWORDS[SLOT_ID % KEYWORDS.length];
  console.log(`\n🚀 Slot ${SLOT_ID}: Generating ${POSTS_TO_GENERATE} post(s)...\n`);
  
  for (let i = 0; i < POSTS_TO_GENERATE; i++) {
    const keyword = currentKeywords[Math.floor(Math.random() * currentKeywords.length)];
    await generateBlogPost(keyword);
    if (i < POSTS_TO_GENERATE - 1) await new Promise(r => setTimeout(r, 2000));
  }
}

main();
