import process from 'process';

// Mocking the behavior in generate-blog.js
const GEMINI_API_KEY = process.env.GEMINI_API_KEY || process.env.VITE_GEMINI_API_KEY;
const GROQ_API_KEY = process.env.GROQ_API_KEY || process.env.VITE_GROQ_API_KEY;
const OPENROUTER_API_KEY = process.env.OPENROUTER_API_KEY || process.env.VITE_OPENROUTER_API_KEY || 'sk-or-v1-fallback';

console.log('--- Environment Check ---');
console.log(`GEMINI_API_KEY: ${GEMINI_API_KEY ? '✅ Present' : '❌ Missing'}`);
console.log(`GROQ_API_KEY: ${GROQ_API_KEY ? '✅ Present' : '❌ Missing'}`);
console.log(`OPENROUTER_API_KEY: ${OPENROUTER_API_KEY ? '✅ Present' : '❌ Missing'}`);
