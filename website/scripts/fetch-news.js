import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const NEWS_CACHE_PATH = path.join(__dirname, 'news-cache.json');

const FEEDS = [
  { name: 'Exams', url: 'https://www.jagranjosh.com/rss/josh-current-affairs.xml' },
  { name: 'Tech', url: 'https://news.google.com/rss/search?q=Artificial%20Intelligence%20AI%20ChatGPT%20Midjourney%20update&hl=en-IN&gl=IN&ceid=IN:en' }
];

async function fetchRSS(feed) {
  try {
    const response = await fetch(feed.url, {
      headers: { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124 Safari/537.36' }
    });
    const xml = await response.text();
    
    // Simple RSS parsing via regex (Lightweight, no deps)
    const items = xml.split('<item>').slice(1);
    return items.slice(0, 5).map(item => {
      const title = item.match(/<title>(.*?)<\/title>/)?.[1]?.replace('<![CDATA[', '').replace(']]>', '');
      const link = item.match(/<link>(.*?)<\/link>/)?.[1];
      const pubDate = item.match(/<pubDate>(.*?)<\/pubDate>/)?.[1];
      return { 
        source: feed.name,
        title: title || 'No Title',
        link: link || 'https://sarkariexamai.com',
        pubDate: pubDate || new Date().toUTCString()
      };
    });
  } catch (error) {
    console.error(`❌ Error fetching ${feed.name}:`, error);
    return [];
  }
}

async function main() {
  console.log('📡 Fetching latest news trends...\n');
  const allNews = [];
  
  for (const feed of FEEDS) {
    const news = await fetchRSS(feed);
    allNews.push(...news);
    console.log(`✅ ${feed.name}: Fetched ${news.length} items.`);
  }

  if (allNews.length > 0) {
    fs.writeFileSync(NEWS_CACHE_PATH, JSON.stringify(allNews, null, 2));
    console.log(`\n💾 Saved ${allNews.length} news items to news-cache.json`);
  } else {
    console.log('\n⚠️ No news items found.');
  }
}

main();
