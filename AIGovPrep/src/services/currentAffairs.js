import callAI from './ai';

/**
 * Service to fetch TRULY LIVE news from Google News RSS and summarize via AI.
 */
/**
 * Service to fetch TRULY LIVE news from Google News RSS and summarize via AI in the user's language.
 */
export const getLatestCurrentAffairs = async (forceRefresh = false, language = 'en') => {
  const CACHE_KEY = `current_affairs_live_cache_${language}`;
  const CACHE_TIME = 2 * 60 * 60 * 1000; // Refresh every 2 hours

  if (!forceRefresh) {
    const cached = localStorage.getItem(CACHE_KEY);
    if (cached) {
      const { timestamp, data } = JSON.parse(cached);
      if (Date.now() - timestamp < CACHE_TIME) {
        return data;
      }
    }
  }

  try {
    const rssUrl = encodeURIComponent('https://news.google.com/rss/search?q=UPSC+exam+India+policy+economy&hl=en-IN&gl=IN&ceid=IN:en');
    const response = await fetch(`https://api.rss2json.com/v1/api.json?rss_url=${rssUrl}`);
    const rssData = await response.json();

    if (rssData.status !== 'ok' || !rssData.items) {
      throw new Error("RSS Fetch failed");
    }

    const realHeadlines = rssData.items.slice(0, 6).map(item => ({
      title: item.title,
      pubDate: item.pubDate,
      link: item.link
    }));

    // Detect language name for the prompt
    const langName = language === 'hi' ? 'Hindi' : language === 'mr' ? 'Marathi' : 'English';

    const messages = [
      {
        role: 'system',
        content: `You are an expert Current Affairs editor for Indian Government Exams. 
        I will provide you with 6 real headlines from today's news. 
        Your task is to:
        1. Translate the headline accurately into ${langName}.
        2. Categorize it (National, Economy, Science, etc.) in ${langName}.
        3. Provide a 2-sentence summary focused on why this is important for an ASPIRANT (e.g. UPSC/MPSC) in ${langName}.
        4. Use today's date for display.
        
        CRITICAL: Provide the response in ${langName} language.
        RESPOND ONLY IN VALID JSON FORMAT.
        FORMAT:
        [
          { "date": "DD Month", "title": "Headline", "category": "Category", "desc": "Exam-focused summary" }
        ]`
      },
      {
        role: 'user',
        content: `Here are today's real news headlines:\n${JSON.stringify(realHeadlines)}`
      }
    ];

    const aiSummaryRaw = await callAI(messages, { temperature: 0.3 });
    const jsonMatch = aiSummaryRaw.match(/\[\s*{[\s\S]*}\s*\]/);
    
    if (jsonMatch) {
      const formattedNews = JSON.parse(jsonMatch[0]);
      
      localStorage.setItem(CACHE_KEY, JSON.stringify({ 
        timestamp: Date.now(), 
        data: formattedNews 
      }));
      
      return formattedNews;
    }
    throw new Error("AI Formatting failed");

  } catch (err) {
    console.warn("Live news fetch failed, falling back to AI generation:", err);
    return await fetchFallbackAINews(language);
  }
};

/**
 * Fallback to pure AI generation if RSS feed is blocked.
 */
async function fetchFallbackAINews(language = 'en') {
  const currentDate = new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
  const langName = language === 'hi' ? 'Hindi' : language === 'mr' ? 'Marathi' : 'English';
  
  const messages = [
    {
      role: 'system',
      content: `Provide 5 important current affairs for Indian competitive exams for ${currentDate} in ${langName} language. Respond in JSON.`
    }
  ];
  try {
    const result = await callAI(messages);
    const jsonMatch = result.match(/\[\s*{[\s\S]*}\s*\]/);
    return JSON.parse(jsonMatch[0]);
  } catch {
    // Basic fallback if even AI fails
    return [
      { date: currentDate, title: 'National Updates', category: 'General', desc: 'Please refresh to get latest news.' }
    ];
  }
}
