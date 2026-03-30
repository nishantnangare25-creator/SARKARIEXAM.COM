import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Newspaper, ArrowLeft, Calendar, Share2, Sparkles, BookOpen, ExternalLink, RefreshCcw } from 'lucide-react';
import { Link } from 'react-router-dom';
import { getLatestCurrentAffairs } from '../services/currentAffairs';

export default function CurrentAffairs() {
  const { t, i18n } = useTranslation();
  const [news, setNews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState('All');

  const fetchNews = async (force = false) => {
    if (force) setRefreshing(true);
    else setLoading(true);
    
    try {
      const data = await getLatestCurrentAffairs(force, i18n.language);
      setNews(data);
    } catch (err) {
      console.error("Failed to fetch news:", err);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchNews();
  }, [i18n.language]);

  const filteredNews = selectedCategory === 'All' 
    ? news 
    : news.filter(item => item.category.toLowerCase().includes(selectedCategory.toLowerCase()));

  const handleShare = async (item) => {
    if (navigator.share) {
      try {
        await navigator.share({
          title: item.title,
          text: `${item.title}\n\nRead more on Sarkari Exam AI Current Affairs.`,
          url: window.location.href
        });
      } catch (err) {
        console.log('Share failed', err);
      }
    }
  };

  if (loading) {
    return (
      <div className="page-wrapper" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '80vh' }}>
        <div className="text-center">
          <RefreshCcw size={48} className="animate-spin text-saffron" style={{ marginBottom: 16 }} />
          <h3>Fetching Latest Updates...</h3>
          <p className="text-muted">Analyzing PIB and National news for you.</p>
        </div>
      </div>
    );
  }

  return (
    <main className="page-wrapper current-affairs-page">
      <div className="page-with-sidebar">
        
        {/* Header Section */}
        <header className="page-header animate-fadeInUp">
          <Link to="/dashboard" className="btn btn-ghost btn-sm" style={{ marginBottom: 16, paddingLeft: 0 }}>
            <ArrowLeft size={16} /> Back to Dashboard
          </Link>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 16 }}>
            <div>
              <h1 style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <Newspaper size={32} className="text-saffron" /> Latest Current Affairs
              </h1>
              <p>Top exam-focused updates for UPSC, MPSC, and State Government exams.</p>
            </div>
            <button 
              className={`btn btn-outline btn-sm ${refreshing ? 'disabled' : ''}`} 
              onClick={() => fetchNews(true)}
              style={{ display: 'flex', alignItems: 'center', gap: 8 }}
            >
              <RefreshCcw size={14} className={refreshing ? 'animate-spin' : ''} /> 
              {refreshing ? 'Refreshing...' : 'Refresh Updates'}
            </button>
          </div>
        </header>

        {/* Featured Alert */}
        <section className="card animate-fadeInUp" style={{ background: 'var(--bg-accent-saffron)', border: '1px solid var(--border-saffron)', marginBottom: 24, animationDelay: '0.1s' }}>
          <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
            <Sparkles size={24} className="text-saffron" />
            <div>
              <p style={{ fontWeight: 600, color: 'var(--text-primary)', margin: 0 }}>Daily Fact for Today</p>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Did you know? Agnikul Cosmos is the first private space startup in India to test a 3D-printed rocket engine successfully.</p>
            </div>
          </div>
        </section>

        <div className="grid-news animate-fadeInUp" style={{ animationDelay: '0.2s' }}>
          {filteredNews.length > 0 ? (
            filteredNews.map((item, i) => (
              <article key={i} className="card news-card" style={{ marginBottom: 20, transition: 'transform 0.2s', borderLeft: i === 0 && selectedCategory === 'All' ? '4px solid var(--accent-orange)' : 'none' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                  <span className="badge badge-saffron" style={{ fontSize: '0.65rem' }}>{item.category.toUpperCase()}</span>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 4 }}>
                    <Calendar size={12} /> {item.date}
                  </span>
                </div>
                <h3 style={{ fontSize: '1.2rem', marginBottom: 16, lineHeight: 1.4 }}>{item.title}</h3>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', lineHeight: 1.7, marginBottom: 20 }}>
                  {item.desc}
                </p>
                
                <div style={{ display: 'flex', justifyContent: 'flex-start', alignItems: 'center', paddingTop: 16, borderTop: '1px solid var(--border-color)' }}>
                  <button className="btn btn-ghost btn-sm" style={{ padding: '4px 12px', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: 8, color: 'var(--primary)', fontWeight: 600 }} onClick={() => handleShare(item)}>
                    <Share2 size={16} /> Share News
                  </button>
                </div>
              </article>
            ))
          ) : (
            <div className="card text-center" style={{ padding: '40px' }}>
              <p className="text-muted">No news found in this category for today.</p>
              <button className="btn btn-link" onClick={() => setSelectedCategory('All')}>Show all news</button>
            </div>
          )}
        </div>

        {/* Categories Sidebar/Section */}
        <section className="card animate-fadeInUp" style={{ marginTop: 24, animationDelay: '0.3s' }}>
          <h4>Browse by Category</h4>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12, marginTop: 16 }}>
            {['All', 'National', 'Economy', 'Science & Tech', 'International', 'Sports', 'Policy'].map(cat => (
              <button 
                key={cat} 
                className={`chip ${selectedCategory === cat ? 'active' : ''}`}
                onClick={() => setSelectedCategory(cat)}
                style={{ 
                  cursor: 'pointer',
                  border: selectedCategory === cat ? '1px solid var(--primary)' : '1px solid var(--border-color)',
                  background: selectedCategory === cat ? 'var(--bg-accent-blue)' : 'var(--bg-secondary)',
                  color: selectedCategory === cat ? 'var(--primary)' : 'var(--text-primary)',
                  fontWeight: selectedCategory === cat ? '600' : '400'
                }}
              >
                {cat}
              </button>
            ))}
          </div>
        </section>

      </div>
    </main>
  );
}
