import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

const API_KEY = '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';
const MODEL = 'google/gemini-2.0-flash-lite-001';

// Parse --count=N argument (default: 1)
const countArg = process.argv.find(a => a.startsWith('--count='));
const POSTS_TO_GENERATE = countArg ? parseInt(countArg.split('=')[1], 10) : 1;

const KEYWORDS = [
  'UPSC syllabus 2025 complete guide',
  'UPSC exam samples and previous year papers',
  'MPSC online classes free resources',
  'best AI tool for UPSC preparation',
  'UPSC mock test free online',
  'government exam study plan for beginners',
  'how to crack MPSC state services exam',
  'IBPS PO preparation strategy AI',
  'Railway NTPC exam preparation tips',
  'SSC CGL syllabus and best books',
  'AI civil services exam coach India',
  'UPSC prelims strategy 2025',
  'MPSC exam pattern and syllabus',
  'how to prepare for government exams at home',
  'best online platform for UPSC preparation India',
];

async function generateBlogPost(keyword) {
  console.log(`🤖 Generating SEO blog post for keyword: "${keyword}"...`);

  const prompt = `
  You are a senior SEO content writer and educator for Sarkari Exam AI — India's #1 AI-powered government exam preparation platform (UPSC, SSC, Banking, MPSC, Railway, etc.).
  
  Write a comprehensive, engaging, and highly informative blog post targeting the search keyword: "${keyword}".
  
  The content MUST strictly adhere to Google's Helpful Content Guidelines:
  - Write for real students, not just for search engines
  - Include actionable advice, study tips, and real strategies
  - Be accurate, trustworthy, and educational
  - Use a warm, encouraging, expert tone
  
  Structure the article with:
  1. A compelling introduction (explain the challenge aspirants face)
  2. 3-4 main sections with H2 headings covering key aspects of the topic
  3. At least one FAQ section (H2: "Frequently Asked Questions") with 3 Q&A pairs
  4. A motivating conclusion with a call-to-action to use Sarkari Exam AI

  IMPORTANT: Naturally weave in mentions of Sarkari Exam AI's features as practical solutions:
  - Smart AI Mock Tests & PYQ Analysis
  - AI Study Planner & Customizable Timetables
  - Instant Notes Generator & PDF Downloads
  - Interactive AI Tutor for instant doubt resolution
  - Deep Performance Analytics & Tracking
  - Free multi-lingual support (Hindi, Marathi, Tamil, Telugu, etc.)
  
  Format the response STRICTLY as a JSON object:
  {
    "title": "A catchy, SEO-optimized H1 title (60-70 chars)",
    "excerpt": "A compelling 150-160 character meta description naturally including the target keyword",
    "content": "The full blog post in well-formatted HTML using <h2>, <h3>, <p>, <ul>, <li>, <strong>. Do NOT use markdown code blocks — only raw HTML string.",
    "tags": ["tag1", "tag2", "tag3", "tag4"]
  }
  
  Only output the validated JSON object. No other text.
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
        response_format: { type: "json_object" }
      }),
    });

    const data = await response.json();
    
    if (data.choices && data.choices[0]) {
      let content = data.choices[0].message.content;
      content = content.replace(/```json/g, '').replace(/```/g, '').trim();
      
      const newPost = JSON.parse(content);
      newPost.id = Date.now().toString() + Math.random().toString(36).slice(2, 7);
      newPost.date = new Date().toISOString();
      newPost.slug = newPost.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)+/g, '');
      newPost.readTime = Math.ceil(newPost.content.split(' ').length / 200) + ' min read';
      newPost.keyword = keyword;
      
      let posts = [];
      if (fs.existsSync(BLOG_DATA_PATH)) {
        posts = JSON.parse(fs.readFileSync(BLOG_DATA_PATH, 'utf8'));
      }
      
      // Avoid duplicate slugs
      if (!posts.find(p => p.slug === newPost.slug)) {
        posts.unshift(newPost);
      }
      
      // Keep only top 50 posts to avoid bloat
      if (posts.length > 50) posts.length = 50;

      fs.writeFileSync(BLOG_DATA_PATH, JSON.stringify(posts, null, 2));
      console.log(`✅ Saved: "${newPost.title}"`);
      return true;
    } else {
      console.error('❌ Failed to generate post from API', JSON.stringify(data));
      return false;
    }
  } catch (err) {
    console.error('❌ Generator Error:', err.message);
    return false;
  }
}

async function main() {
  console.log(`\n🚀 Sarkari Exam AI Blog Generator — generating ${POSTS_TO_GENERATE} post(s)...\n`);
  
  // Shuffle keywords to avoid repeating the same ones
  const shuffled = [...KEYWORDS].sort(() => 0.5 - Math.random());
  
  for (let i = 0; i < POSTS_TO_GENERATE; i++) {
    const keyword = shuffled[i % shuffled.length];
    await generateBlogPost(keyword);
    // Small delay between API calls
    if (i < POSTS_TO_GENERATE - 1) await new Promise(r => setTimeout(r, 1500));
  }
  
  console.log(`\n✅ Done! ${POSTS_TO_GENERATE} post(s) generated.\n`);
}

main();
