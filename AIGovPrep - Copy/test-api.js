

const key = "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";

async function testKey() {
  console.log("Testing OpenRouter API Key...");
  try {
    const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${key}`
      },
      body: JSON.stringify({
        model: 'google/gemini-2.5-flash',
        messages: [{ role: 'user', content: 'respond with exactly the word: ok' }],
        max_tokens: 10
      })
    });
    
    if (response.ok) {
      const data = await response.json();
      console.log("SUCCESS! API Key is working.");
      console.log("Response:", data.choices[0].message.content);
    } else {
      const errorText = await response.text();
      console.log("FAILED! Status:", response.status);
      console.log("Error body:", errorText);
    }
  } catch (error) {
    console.log("FETCH ERROR:", error.message);
  }
}

testKey();
