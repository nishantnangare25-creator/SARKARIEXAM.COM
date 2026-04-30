import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Load .env manually
const envPath = path.join(__dirname, '../.env');
if (fs.existsSync(envPath)) {
  const envContent = fs.readFileSync(envPath, 'utf8');
  envContent.split('\n').forEach(line => {
    const [key, ...val] = line.split('=');
    if (key && val.length > 0) process.env[key.trim()] = val.join('=').trim();
  });
}

const FALLBACK_FILE_PATH = path.join(__dirname, '../src/data/fallback_mocks.json');

// Get keys
const groqKeys = [];
for (let i = 1; i <= 10; i++) {
  if (process.env[`GROQ_API_KEY_${i}`]) groqKeys.push(process.env[`GROQ_API_KEY_${i}`]);
}

if (groqKeys.length === 0) {
  console.error("No Groq API keys found. Please set GROQ_API_KEY_1 in .env");
  process.exit(1);
}

const subjects = [
  "Indian History", "Indian Polity", "Geography of India", 
  "General Science", "Indian Economy", "Current Affairs", 
  "Reasoning", "Quantitative Aptitude"
];

// Helper to delay
const sleep = ms => new Promise(r => setTimeout(r, ms));

async function fetchQuestions(subject) {
  const prompt = `Generate exactly 10 high-quality MCQ questions for Indian competitive exams on the subject of: ${subject}. 
Return ONLY a strictly valid JSON object inside this structure:
{
  "questions": [
    {
      "Q": "[Question text]",
      "A": "[Option A]",
      "B": "[Option B]",
      "C": "[Option C]",
      "D": "[Option D]",
      "Answer": "[A, B, C, or D]",
      "Explanation": "[Short 1-2 sentence explanation]"
    }
  ]
}
DO NOT include markdown block markers like \`\`\`json. Return pure JSON.`;

  const key = groqKeys[Math.floor(Math.random() * groqKeys.length)];

  const res = await fetch('https://api.groq.com/openai/v1/chat/completions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${key}`
    },
    body: JSON.stringify({
      model: 'llama-3.3-70b-versatile',
      messages: [{ role: 'user', content: prompt }],
      temperature: 0.7,
      max_tokens: 3000
    })
  });

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(`API Error: ${res.status} ${txt}`);
  }

  const data = await res.json();
  const text = data.choices[0].message.content.trim();
  
  // Clean potential markdown blocks
  const jsonStr = text.replace(/^```json\n?/, '').replace(/\n?```$/, '');
  return JSON.parse(jsonStr).questions;
}

async function runAutomation() {
  console.log("🚀 Starting Offline Mock Automation Engine...");
  
  // Create or load existing db
  let db = { questions: [] };
  if (fs.existsSync(FALLBACK_FILE_PATH)) {
    try {
      db = JSON.parse(fs.readFileSync(FALLBACK_FILE_PATH, 'utf-8'));
    } catch (e) {
       console.log("Starting a fresh fallback DB.");
    }
  }

  while (true) {
    for (const subject of subjects) {
      console.log(`Generating 10 questions for ${subject}...`);
      try {
        const questions = await fetchQuestions(subject);
        if (questions && questions.length > 0) {
          db.questions.push(...questions);
          // Save incrementally
          fs.writeFileSync(FALLBACK_FILE_PATH, JSON.stringify(db, null, 2));
          console.log(`✅ Saved. Total questions in offline bank: ${db.questions.length}`);
        }
      } catch (err) {
        console.error(`❌ Failed to generate for ${subject}: ${err.message}`);
      }
      
      // Delay to respect API limits (15 seconds between calls)
      console.log("Cooling down for 15 seconds...");
      await sleep(15000);
    }
  }
}

runAutomation();
