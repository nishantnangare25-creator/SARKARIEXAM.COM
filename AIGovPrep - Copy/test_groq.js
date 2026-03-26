const checkGroq = async () => {
  try {
    const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer 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',
      },
      body: JSON.stringify({
        model: 'llama-3.3-70b-versatile',
        messages: [{ role: 'user', content: 'test' }],
        max_tokens: 10,
      }),
    });
    console.log('Groq:', await response.json());
  } catch (e) {
    console.log('Groq error:', e.message);
  }
};
checkGroq();
