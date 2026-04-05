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

const FALLBACK_FILE_PATH = path.join(__dirname, '../src/data/fallback_planners.json');

const groqKeys = [];
for (let i = 1; i <= 10; i++) {
  if (process.env[`GROQ_API_KEY_${i}`]) groqKeys.push(process.env[`GROQ_API_KEY_${i}`]);
}

if (groqKeys.length === 0) {
  console.error("No Groq API keys found. Please set GROQ_API_KEY_1 in .env");
  process.exit(1);
}

const exams = ["UPSC Civil Services", "SSC CGL", "RRB NTPC", "Bank PO", "NDA", "State PSC", "CTET", "UGC NET"];
const levels = ["Beginner", "Intermediate", "Advanced"];
const hoursArr = ["4", "6", "8", "10"];

const sleep = ms => new Promise(r => setTimeout(r, ms));

async function fetchPlanner(exam, level, hours) {
  const lang = "English";
  const prompt = `You are an expert Indian competitive exam coach. Generate a detailed weekly study plan. Respond in ${lang}. Use detailed markdown formatting with headings, bullet points, and tables. DO NOT use JSON.
Create a study plan for ${exam} exam preparation.
- Available study hours per day: ${hours}
- Current preparation level: ${level}
- Weak subjects: None specified
- Strong subjects: None specified
Return ONLY the formatted markdown plan.`;

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
      max_tokens: 2000
    })
  });

  if (!res.ok) {
    throw new Error(`API Error: ${res.status}`);
  }

  const data = await res.json();
  return data.choices[0].message.content.trim();
}

async function runAutomation() {
  console.log("🚀 Starting Study Planner Fallback Generator...");
  
  let db = { planners: [] };
  if (fs.existsSync(FALLBACK_FILE_PATH)) {
    try {
      db = JSON.parse(fs.readFileSync(FALLBACK_FILE_PATH, 'utf-8'));
    } catch (e) {
      console.log("Starting a fresh fallback DB.");
    }
  }

  // Iterate over combinations
  for (const exam of exams) {
    for (const level of levels) {
      for (const hours of hoursArr) {
        // Check if exists
        const exists = db.planners.find(p => p.exam === exam && p.level === level && p.hours === hours);
        if (exists) {
          console.log(`Skipping ${exam} - ${level} - ${hours}H (Already exists)`);
          continue;
        }

        console.log(`Generating Planner for: ${exam} | ${level} | ${hours}H...`);
        try {
          const content = await fetchPlanner(exam, level, hours);
          if (content && content.length > 100) {
            db.planners.push({
              exam,
              level,
              hours,
              content: content + "\n\n*(This is an offline cached study plan)*"
            });
            fs.writeFileSync(FALLBACK_FILE_PATH, JSON.stringify(db, null, 2));
            console.log(`✅ Saved.`);
          }
        } catch (err) {
          console.error(`❌ Failed: ${err.message}`);
        }
        
        await sleep(5000); // Wait between requests
      }
    }
  }
  console.log("🎉 All common study plans generated!");
}

runAutomation();
