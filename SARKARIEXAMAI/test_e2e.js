// End-to-end test simulating exactly what generateMockQuestions does
const GROQ_KEY = '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';

const extractJSON = (text) => {
  try {
    let cleanText = text.replace(/```json/gi, '').replace(/```/g, '').trim();
    const start = cleanText.indexOf('{');
    const end = cleanText.lastIndexOf('}');
    if (start !== -1 && end !== -1 && start < end) {
      return JSON.parse(cleanText.substring(start, end + 1));
    }
  } catch (e) {
    console.error('JSON parse error:', e.message);
  }
  return null;
};

const test = async () => {
  console.log('Testing Groq API for PYQs Mock Test flow...');
  try {
    const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${GROQ_KEY}`,
      },
      body: JSON.stringify({
        model: 'llama-3.3-70b-versatile',
        messages: [
          {
            role: 'system',
            content: `You are an expert Indian competitive exam creator. Generate exactly 3 MCQ questions. Return ONLY valid JSON:
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
            content: 'Generate 3 targeted practice MCQ questions for UPSC Civil Services exam. Subject/topic focus: Science. Difficulty level: medium.'
          }
        ],
        temperature: 0.7,
        max_tokens: 2000,
      }),
    });

    const data = await response.json();

    if (response.ok && data.choices && data.choices[0]) {
      const content = data.choices[0].message.content;
      console.log('\n✅ Groq Response received!');
      console.log('Raw (first 300 chars):', content.substring(0, 300));

      const parsed = extractJSON(content);
      if (parsed && parsed.questions && parsed.questions.length > 0) {
        console.log(`\n✅ JSON parsed successfully! Got ${parsed.questions.length} questions.`);
        console.log('First question:', parsed.questions[0].question);
      } else {
        console.log('❌ JSON parsing failed');
      }
    } else {
      console.log('❌ Groq API error:', data.error?.message);
    }
  } catch (e) {
    console.log('❌ Fetch error:', e.message);
  }
};

test();
