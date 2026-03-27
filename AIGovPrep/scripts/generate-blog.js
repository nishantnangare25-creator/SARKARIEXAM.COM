import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BLOG_DATA_PATH = path.join(__dirname, '../src/data/blogPosts.json');

const API_KEY = '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';
const MODEL = 'google/gemini-2.0-flash-lite-001';

const KEYWORDS = [
  'ai civil services exam',
  'upsc syllabus comprehensive guide',
  'mpsc online classes free',
  'mpsc exams pyq book analysis',
  'upmpsc complete roadmap'
];

async function generateBlogPost() {
  const keyword = KEYWORDS[Math.floor(Math.random() * KEYWORDS.length)];
  console.log(`🤖 Generating SEO blog post for keyword: "${keyword}"...`);

  const prompt = `
  You are an expert SEO content writer and educator for Sarkari Exam AI, India's #1 AI-powered platform for government exam preparation (UPSC, SSC, Banking, MPSC, etc.). 
  
  Write a comprehensive, engaging, and highly informative blog post targeting the keyword: "${keyword}".
  The content MUST strictly adhere to Google's Helpful Content Guidelines. Make sure it is highly structured and valuable.
  
  CRITICAL REQUIREMENT: Naturally weave in mentions of ALL of Sarkari Exam AI's core tools and features throughout the article as practical solutions for the reader. These features include:
  - Smart AI Mock Tests & PYQs Mock Tests
  - AI Study Planner & Customizable Timetables
  - Instant Notes Generator & PYQ PDFs Downloads
  - Past Paper Analyzer with high-yield topic patterns
  - Interactive AI Tutor & Bot for resolving doubts instantly
  - Deep Performance Analytics & Tracking
  - Community Forum & AI Peer Matching for collaborative study
  - Free multi-lingual support (Hindi, Marathi, Tamil, Telugu, etc.)

  Format the response STRICTLY as a JSON object with the following structure:
  {
    "title": "A catchy, SEO-optimized title",
    "excerpt": "A short 2-sentence meta description style excerpt",
    "content": "The full blog post in well-formatted HTML (use <h2>, <h3>, <p>, <ul>, <li>). Do NOT use markdown code blocks, just raw HTML string.",
    "tags": ["tag1", "tag2", "tag3"]
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
      // Cleanup markdown json blocks if OpenRouter returns them
      content = content.replace(/```json/g, '').replace(/```/g, '').trim();
      
      const newPost = JSON.parse(content);
      newPost.id = Date.now().toString();
      newPost.date = new Date().toISOString();
      newPost.slug = newPost.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)+/g, '');
      newPost.readTime = Math.ceil(newPost.content.split(' ').length / 200) + ' min read';
      
      let posts = [];
      if (fs.existsSync(BLOG_DATA_PATH)) {
        posts = JSON.parse(fs.readFileSync(BLOG_DATA_PATH, 'utf8'));
      }
      
      // Auto-publish to the array
      posts.unshift(newPost);
      
      // Keep only top 50 posts to avoid bloat
      if (posts.length > 50) posts.length = 50;

      fs.writeFileSync(BLOG_DATA_PATH, JSON.stringify(posts, null, 2));
      console.log(`✅ Successfully generated and saved new blog post: ${newPost.title}`);
    } else {
      console.error('❌ Failed to generate post from API', data);
    }
  } catch (err) {
    console.error('❌ Generator Error:', err.message);
  }
}

generateBlogPost();
