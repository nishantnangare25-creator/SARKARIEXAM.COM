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
  // AI & Technology Keywords (User Requested)
  'AI', 'Midjourney', 'Artificial Intelligence', 'Midjourney AI', 'ChatGPT 4', 'AI Chat', 'OpenAI', 'Open AI', 'Generative AI', 'AI Website', 'AI Chatbot Online', 'Conversational AI', 'AI Assistant', 'AI Generated', 'AI GPT', 'AI Robot', 'ChatGPT 4 AI Chatbot',
  
  // Government Exam Keywords (Original)
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
  You are a senior SEO content writer and educator for Sarkari Exam AI — India's #1 AI-powered government exam preparation platform and AI Hub.
  
  Write a comprehensive, engaging, and highly informative blog post targeting the search keyword: "${keyword}".
  
  The content MUST strictly adhere to Google's Helpful Content Guidelines (E-E-A-T):
  - Experience: Share real-world examples and practical insights.
  - Expertise: Provide accurate, well-researched information.
  - Authoritativeness: Write with confidence and clarity.
  - Trustworthiness: Be transparent, cite reputable facts, and avoid clickbait.
  
  Structure the article with:
  1. A compelling introduction (hook the reader immediately).
  2. 4-5 detailed sections with H2/H3 headings.
  3. A dedicated section on "How Sarkari Exam AI can help" (even for AI tech topics, link it back to personal growth or study aid).
  4. At least one FAQ section (H2: "Frequently Asked Questions") with 3-4 Q&A pairs.
  5. A motivating conclusion with a solid Call-To-Action (CTA).

  IMPORTANT:
  - Use high-quality HTML markup (<h2>, <h3>, <p>, <ul>, <li>, <strong>).
  - Include a "Featured Image Description" that describes a professional, eye-catching image for this topic.
  
  Format the response STRICTLY as a JSON object:
  {
    "title": "A catchy, SEO-optimized H1 title (60-70 chars)",
    "excerpt": "A compelling 150-160 character meta description naturally including the target keyword",
    "content": "The full blog post in well-formatted HTML. (Do NOT use markdown code blocks).",
    "tags": ["tag1", "tag2", "tag3", "tag4"],
    "featuredImage": "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&q=80&w=1200",
    "imagePrompt": "A highly detailed, professional digital art of [keyword topic], cinematic lighting, 8k resolution."
  }
  
  *Note: For the featuredImage, use a relevant Unsplash URL or a placeholder that matches the topic.*
  
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
          response_format: { type: "json_object" },
          max_tokens: 3000
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
      
      // Select a more specific image if it's an AI keyword
      const aiKeywords = ['ai', 'chatgpt', 'midjourney', 'openai', 'bot', 'gpt'];
      if (aiKeywords.some(k => keyword.toLowerCase().includes(k))) {
        if (!newPost.featuredImage.includes('photo-1677')) { // If it's a default, give it a techy one
           newPost.featuredImage = "https://images.unsplash.com/photo-1620712943543-bccffef48332?auto=format&fit=crop&q=80&w=1200";
        }
      } else {
        // Education/Exam image
        newPost.featuredImage = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&q=80&w=1200";
      }

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
