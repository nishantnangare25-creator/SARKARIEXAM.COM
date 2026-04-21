const checkKeys = async () => {
  try {
    const orRes = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Authorization': 'Bearer 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',
        'Content-Type': 'application/json',
        'HTTP-Referer': 'http://localhost:5174',
        'X-Title': 'Sarkari Exam AI'
      },
      body: JSON.stringify({
        messages: [{ role: 'user', content: 'respond with \"hello\"' }],
        model: 'google/gemini-2.5-flash',
        max_tokens: 10
      })
    });
    console.log('OpenRouter:', await orRes.json());
  } catch (e) {
    console.log('OpenRouter error:', e.message);
  }
};
checkKeys();
