// Test live Cloudflare Worker
const WORKER_URL = 'https://sarkari-exam-ai.nishantnangare34.workers.dev';

const test = async () => {
  console.log('Testing Cloudflare Worker at:', WORKER_URL);
  try {
    const response = await fetch(WORKER_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        messages: [
          { role: 'system', content: 'You are a helpful assistant. Reply briefly.' },
          { role: 'user', content: 'Say "Worker is working!" in exactly 3 words.' }
        ],
        options: { max_tokens: 50 }
      }),
    });
    const data = await response.json();
    if (response.ok && data.content) {
      console.log('✅ Worker SUCCESS! Response:', data.content);
    } else {
      console.log('❌ Worker Error:', data.error);
    }
  } catch (e) {
    console.log('❌ Fetch error:', e.message);
  }
};
test();
