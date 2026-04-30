const callAI = async (messages, options = {}) => {
  const orKey = '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';

  try {
    const response = await fetch('https://openrouter.ai/api/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${orKey}`,
        'HTTP-Referer': 'http://localhost:5173',
        'X-Title': 'Sarkari Exam AI',
      },
      body: JSON.stringify({
        model: 'google/gemini-2.5-flash',
        messages,
        temperature: 0.7,
        max_tokens: Math.min(options.max_tokens || 1500, 1500),
      }),
    });

    if (response.ok) {
      const data = await response.json();
      if (data.choices && data.choices[0]) {
        console.log('SUCCESS! First 300 chars of response:\n', data.choices[0].message.content.substring(0, 300));
        return;
      }
    }
    const errData = await response.json().catch(() => ({}));
    console.log('API Error:', response.status, errData?.error?.message || response.statusText);
  } catch (e) {
    console.log('Fetch error:', e.message);
  }
};

const messages = [
  {
    role: 'system',
    content: `You are an expert Indian competitive exam creator. Generate 5 MCQ questions. Return JSON:
{
  "questions": [
    {
      "id": 1,
      "question": "...",
      "options": ["...", "...", "...", "..."],
      "correctAnswer": "...",
      "explanation": "...",
      "topic": "...",
      "difficulty": "medium"
    }
  ]
}`
  },
  {
    role: 'user',
    content: `Generate 5 practice MCQ questions for UPSC exam. Subject: General Knowledge. Difficulty: medium.`
  }
];

callAI(messages, { max_tokens: 1500 });
