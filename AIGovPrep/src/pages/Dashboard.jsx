import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  Zap, Brain, Target, Sparkles, TrendingUp, 
  Clock, ArrowRight, Play, BookOpen, Newspaper, RefreshCcw 
} from 'lucide-react';
import { getLatestCurrentAffairs } from '../services/currentAffairs';

export default function Dashboard({ onToggleSidebar }) {
  const { t, i18n } = useTranslation();
  const { user, profile } = useAuth();
  const [greeting, setGreeting] = useState('');
  const [currentAffairs, setCurrentAffairs] = useState([]);
  const [caLoading, setCaLoading] = useState(true);

  useEffect(() => {
    const hour = new Date().getHours();
    if (hour < 12) setGreeting('Good Morning');
    else if (hour < 17) setGreeting('Good Afternoon');
    else setGreeting('Good Evening');

    const fetchCA = async () => {
      try {
        const data = await getLatestCurrentAffairs(false, i18n.language);
        setCurrentAffairs(data.slice(0, 3));
      } catch (err) {
        console.error(err);
      } finally {
        setCaLoading(false);
      }
    };
    fetchCA();
  }, [i18n.language]);

  const quickActions = [
    { id: 'mock', title: 'Start Mock Test', desc: 'Full length test', icon: Brain, color: 'blue', path: '/mock-test' },
    { id: 'pyq-lib', title: 'PYQ Library', desc: 'Past papers & Upload', icon: BookOpen, color: 'saffron', path: '/pyq-pdfs' },
    { id: 'pyq-test', title: 'PYQs Practice', desc: '10-min 10-questions', icon: Target, color: 'red', path: '/pyqs-mock-test' },
    { id: 'tutor', title: 'AI Study Tutor', desc: 'Ask Riya anything', icon: Sparkles, color: 'green', path: '/tutor' },
  ];

  const subjects = [
    { name: 'History', progress: 75, color: 'blue' },
    { name: 'Geography', progress: 45, color: 'saffron' },
    { name: 'Polity', progress: 90, color: 'green' },
    { name: 'Current Affairs', progress: 30, color: 'red' },
  ];

  return (
    <main className="page-wrapper">
      <div className="page-with-sidebar">
        
        {/* Header Greeting */}
        <header className="page-header animate-fadeInUp">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end', flexWrap: 'wrap', gap: 16 }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: 8 }}>
                <p className="badge badge-primary" style={{ margin: 0 }}>{user ? 'Dashboard Overview' : 'Guest Mode'}</p>
                <button 
                  className="btn btn-sm btn-outline" 
                  onClick={onToggleSidebar}
                  style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.75rem', padding: '4px 10px', borderRadius: 'var(--radius-full)' }}
                >
                  <Newspaper size={14} /> Open Menu
                </button>
              </div>
              <h1 style={{ marginBottom: 4 }}>
                {user ? `${greeting}, ${user?.displayName?.split(' ')[0] || 'Scholar' }! 👋` : `${greeting}, Aspirant! 👋`}
              </h1>
              <p>{user ? 'Ready to continue your preparation?' : 'Start your prep today as a guest or login to track progress.'}</p>
            </div>
            
            {user ? (
              <div className="card" style={{ padding: '8px 16px', display: 'flex', gap: 24, borderRadius: 'var(--radius-md)' }}>
                <div style={{ textAlign: 'center' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Daily Streak</span>
                  <span style={{ fontWeight: 800, color: 'var(--accent-orange)' }}>🔥 12 Days</span>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Readiness</span>
                  <span style={{ fontWeight: 800, color: 'var(--accent-green)' }}>📈 84%</span>
                </div>
              </div>
            ) : (
              <Link to="/login" className="card" style={{ padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12, borderRadius: 'var(--radius-md)', textDecoration: 'none', background: 'var(--primary-bg)', border: '1px solid var(--border-blue)' }}>
                <Sparkles className="text-blue" size={20} />
                <div>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Personalize Prep</span>
                  <span style={{ fontWeight: 700, color: 'var(--primary)', fontSize: '0.9rem' }}>Login to Sync Progress</span>
                </div>
              </Link>
            )}
          </div>
        </header>

        {/* Quick Actions Grid */}
        <div className="grid-4 animate-fadeInUp" style={{ marginBottom: 32, animationDelay: '0.1s' }}>
          {quickActions.map(action => (
            <Link key={action.id} to={action.path} className="card" style={{ display: 'flex', flexDirection: 'column', gap: 12, textDecoration: 'none' }}>
              <div className={`feature-icon ${action.color}`}>
                <action.icon size={20} />
              </div>
              <div>
                <h4 style={{ color: 'var(--text-primary)', marginBottom: 2 }}>{action.title}</h4>
                <p style={{ fontSize: '0.8rem' }}>{action.desc}</p>
              </div>
              <div style={{ marginTop: 'auto', display: 'flex', alignItems: 'center', gap: 6, fontSize: '0.8rem', fontWeight: 600, color: 'var(--primary)' }}>
                Go now <ArrowRight size={14} />
              </div>
            </Link>
          ))}
        </div>

        {/* Main Content Layout */}
        <div className="dashboard-layout animate-fadeInUp">
          
          {/* Left Column: Progress & Suggestions */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            
            {/* Performance Analytics */}
            <section className="card" style={{ position: 'relative', overflow: 'hidden' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <TrendingUp size={20} className="text-blue" /> Performance Analytics
                </h3>
                {user && <button className="btn btn-sm btn-ghost">View Details</button>}
              </div>

              {!user && (
                <div style={{ position: 'absolute', inset: 0, zIndex: 10, background: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(4px)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 20, textAlign: 'center' }}>
                  <Sparkles size={32} className="text-blue" style={{ marginBottom: 12 }} />
                  <p style={{ fontWeight: 700, fontSize: '0.9rem', marginBottom: 12 }}>Analytics Locked</p>
                  <Link to="/login" className="btn btn-sm btn-primary">Login to Unlock</Link>
                </div>
              )}

              <div style={{ display: 'flex', flexDirection: 'column', gap: 16, filter: user ? 'none' : 'blur(2px)', opacity: user ? 1 : 0.5 }}>
                {subjects.map(sub => (
                  <div key={sub.name}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4, fontSize: '0.85rem' }}>
                      <span style={{ fontWeight: 500 }}>{sub.name}</span>
                      <span className="text-muted">{user ? sub.progress : '??'}%</span>
                    </div>
                    <div className="progress-bar-wrap">
                      <div className={`progress-bar-fill ${sub.color}`} style={{ width: user ? `${sub.progress}%` : '30%' }} />
                    </div>
                  </div>
                ))}
              </div>
            </section>

            {/* AI Insights Chips */}
            <section className="card" style={{ background: 'var(--primary-bg)', border: '1px solid var(--border-blue)' }}>
              <h4 style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 8, color: 'var(--primary)' }}>
                <Zap size={18} fill="var(--primary)" /> Smart Suggestions
              </h4>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                <div className="chip active">Focus on Modern India (Topic)</div>
                <div className="chip">Improve Geography speed</div>
                <div className="chip">Review Mock Test #4 errors</div>
                <div className="chip">New update in Current Affairs</div>
              </div>
            </section>
          </div>

          {/* Right Column: Daily Feed */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            
            {/* Daily Quiz Highlight */}
            <section className="card" style={{ background: 'var(--gradient-primary)', color: 'white', border: 'none', position: 'relative', overflow: 'hidden' }}>
              <div style={{ position: 'relative', zIndex: 2 }}>
                <p style={{ opacity: 0.9, fontSize: '0.8rem', fontWeight: 600, letterSpacing: 0.5 }}>DAILY CHALLENGE</p>
                <h3 style={{ color: 'white', margin: '8px 0 16px' }}>Constitution of India Special Quiz</h3>
                <Link to="/mock-test" className="btn btn-cta" style={{ borderRadius: 'var(--radius-full)' }}>
                  <Play size={16} fill="white" /> Challenge Now
                </Link>
              </div>
              <Sparkles size={80} style={{ position: 'absolute', right: -10, bottom: -10, opacity: 0.1, color: 'white' }} />
            </section>

            {/* Current Affairs Card List */}
            <section className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <Newspaper size={20} className="text-saffron" /> Current Affairs
                </h3>
                <span className="badge badge-orange">Latest</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {caLoading ? (
                  <div style={{ textAlign: 'center', padding: '20px' }}>
                    <RefreshCcw className="animate-spin text-saffron" size={24} />
                  </div>
                ) : (
                  currentAffairs.map((news, i) => (
                    <Link key={i} to="/current-affairs" style={{ textDecoration: 'none', color: 'inherit' }}>
                      <div style={{ display: 'flex', gap: 12, padding: 8, borderRadius: 8, cursor: 'pointer', transition: 'background 0.2s' }} onMouseEnter={e => e.currentTarget.style.background = 'var(--bg-tertiary)'} onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
                        <div style={{ width: 44, height: 44, background: 'var(--bg-accent-saffron)', color: 'var(--accent-orange)', borderRadius: 8, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', fontSize: '0.7rem', fontWeight: 700, flexShrink: 0 }}>
                          {news.date.split(' ')[0]} <br/> {news.date.split(' ')[1]}
                        </div>
                        <div style={{ fontSize: '0.85rem', fontWeight: 500, alignSelf: 'center' }}>
                          {news.title}
                        </div>
                      </div>
                    </Link>
                  ))
                )}
                <Link to="/current-affairs" className="btn btn-ghost btn-sm" style={{ width: '100%', justifyContent: 'center', marginTop: 8, textDecoration: 'none' }}>
                  View All Updates <ArrowRight size={14} />
                </Link>
              </div>
            </section>
          </div>
        </div>
      </div>
    </main>
  );
}
