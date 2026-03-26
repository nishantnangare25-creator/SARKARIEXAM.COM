// Test script for new OpenRouter API key
const API_KEY = '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';
const MODEL = 'google/gemini-2.0-flash-lite-001';

async function testOpenRouter() {
  console.log('🔍 Testing OpenRouter API...');
  console.log(`📦 Model: ${MODEL}`);
  console.log('');

  try {
    const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${API_KEY}`,
        'X-Title': 'Sarkari Exam AI Test',
      },
      body: JSON.stringify({
        model: MODEL,
        messages: [{ role: 'user', content: 'Reply with exactly: "API Working!"' }],
        max_tokens: 20,
      }),
    });

    const data = await response.json();

    if (response.ok && data.choices?.[0]) {
      console.log('✅ SUCCESS! OpenRouter connected!');
      console.log(`💬 Response: ${data.choices[0].message.content}`);
      console.log(`🔑 Model used: ${data.model}`);
    } else {
      console.log('❌ FAILED!');
      console.log('Error:', data.error?.message || JSON.stringify(data));
    }
  } catch (err) {
    console.log('❌ Network Error:', err.message);
  }
}

testOpenRouter();
