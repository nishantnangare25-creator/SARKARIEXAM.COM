import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { 
  Zap, Brain, Target, Sparkles, TrendingUp, 
  Clock, ArrowRight, Play, BookOpen, Newspaper, RefreshCcw, Activity
} from 'lucide-react';
import { getLatestCurrentAffairs } from '../services/currentAffairs';
import { getTestHistory } from '../services/firebase';
import { getAIStatus } from '../services/ai';

export default function Dashboard({ onToggleSidebar }) {
  const { t, i18n } = useTranslation(); // Translation hooks for app-wide labels
  // UI Fix: Hardcoded Current Affairs labels to ensure consistency on dashboard.

  const { user, profile } = useAuth();
  const [greeting, setGreeting] = useState('');
  const [currentAffairs, setCurrentAffairs] = useState([]);
  const [caLoading, setCaLoading] = useState(true);
  const [testHistory, setTestHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(true);
  const aiStatus = getAIStatus();

  useEffect(() => {
    const hour = new Date().getHours();
    if (hour < 12) setGreeting(t('dashboard.greeting.morning'));
    else if (hour < 17) setGreeting(t('dashboard.greeting.afternoon'));
    else setGreeting(t('dashboard.greeting.evening'));

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

    const fetchHistory = async () => {
      if (user?.uid) {
        try {
          const history = await getTestHistory(user.uid);
          setTestHistory(history);
        } catch (err) {
          console.error("Error fetching history:", err);
        } finally {
          setHistoryLoading(false);
        }
      } else {
        setHistoryLoading(false);
      }
    };

    fetchCA();
    fetchHistory();
  }, [i18n.language, user]);

  const calculateAnalytics = () => {
    const defaultStats = [
      { name: t('common.history') || 'History', progress: 0, color: 'blue' },
      { name: t('common.geography') || 'Geography', progress: 0, color: 'saffron' },
      { name: t('common.polity') || 'Polity', progress: 0, color: 'green' },
      { name: 'Current Affairs', progress: 0, color: 'red' },
    ];

    if (!testHistory.length) return defaultStats;

    const subjectStats = {};
    testHistory.forEach(test => {
      const sub = test.subject || 'Other';
      if (!subjectStats[sub]) {
        subjectStats[sub] = { total: 0, count: 0 };
      }
      subjectStats[sub].total += (test.score / test.total) * 100;
      subjectStats[sub].count += 1;
    });

    return Object.keys(subjectStats).map(name => ({
      name,
      progress: Math.round(subjectStats[name].total / subjectStats[name].count),
      color: name.includes('History') ? 'blue' : name.includes('Polity') ? 'green' : name.includes('Geography') ? 'saffron' : 'red'
    })).sort((a, b) => b.progress - a.progress);
  };

  const getSuggestions = (stats) => {
    const suggestions = [];
    
    // Add logic based on lowest scores
    const weakSubjects = stats.filter(s => s.progress < 60 && s.progress > 0);
    if (weakSubjects.length > 0) {
      suggestions.push({ text: t('dashboard.suggestions.focus', { subject: weakSubjects[0].name }), level: 'important', path: '/mock-test' });
    }
    
    if (testHistory.length > 0) {
      suggestions.push({ text: t('dashboard.suggestions.review', { subject: testHistory[0].subject }), level: 'normal', path: '/analytics' });
    }

    if (suggestions.length < 3) {
      suggestions.push({ text: t('dashboard.suggestions.takeMock'), level: 'normal', path: '/mock-test' });
      suggestions.push({ text: t('dashboard.suggestions.newUpdate'), level: 'normal', path: '/current-affairs' });
    }

    return suggestions.slice(0, 4);
  };

  const dynamicSubjects = user ? calculateAnalytics() : [
    { name: t('common.history'), progress: 75, color: 'blue' },
    { name: t('common.geography'), progress: 45, color: 'saffron' },
    { name: t('common.polity'), progress: 90, color: 'green' },
    { name: 'Current Affairs', progress: 30, color: 'red' },
  ];

  const suggestions = getSuggestions(dynamicSubjects);

  const quickActions = [
    { id: 'mock', title: t('dashboard.quickActions.mock.title'), desc: t('dashboard.quickActions.mock.desc'), icon: Brain, color: 'blue', path: '/mock-test' },
    { id: 'pyq-lib', title: t('dashboard.quickActions.pyqLib.title'), desc: t('dashboard.quickActions.pyqLib.desc'), icon: BookOpen, color: 'saffron', path: '/pyq-pdfs' },
    { id: 'pyq-test', title: t('dashboard.quickActions.pyqTest.title'), desc: t('dashboard.quickActions.pyqTest.desc'), icon: Target, color: 'red', path: '/pyqs-mock-test' },
    { id: 'tutor', title: t('dashboard.quickActions.tutor.title'), desc: t('dashboard.quickActions.tutor.desc'), icon: Sparkles, color: 'green', path: '/tutor' },
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
                <p className="badge badge-primary" style={{ margin: 0 }}>{user ? t('dashboard.header.overview') : t('dashboard.header.guest')}</p>
                <button 
                  className="btn btn-sm btn-outline" 
                  onClick={onToggleSidebar}
                  style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.75rem', padding: '4px 10px', borderRadius: 'var(--radius-full)' }}
                >
                  <Newspaper size={14} /> {t('dashboard.header.openMenu')}
                </button>
              </div>
              <h1 style={{ marginBottom: 4 }}>
                {user ? `${greeting}, ${user?.displayName?.split(' ')[0] || t('dashboard.greeting.scholar') }! 👋` : `${greeting}, ${t('dashboard.greeting.aspirant')}! 👋`}
              </h1>
              <p>{user ? t('dashboard.greeting.ready') : t('dashboard.greeting.startGuest')}</p>
            </div>
            
            {user ? (
              <div className="card" style={{ padding: '8px 16px', display: 'flex', gap: 24, borderRadius: 'var(--radius-md)' }}>
                <div style={{ textAlign: 'center' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>{t('dashboard.stats.streak')}</span>
                  <span style={{ fontWeight: 800, color: 'var(--accent-orange)' }}>🔥 12 {t('dashboard.stats.days')}</span>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>{t('dashboard.stats.readiness')}</span>
                  <span style={{ fontWeight: 800, color: 'var(--accent-green)' }}>📈 84%</span>
                </div>
              </div>
            ) : (
              <Link to="/login" className="card" style={{ padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12, borderRadius: 'var(--radius-md)', textDecoration: 'none', background: 'var(--primary-bg)', border: '1px solid var(--border-blue)' }}>
                <Sparkles className="text-blue" size={20} />
                <div>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>{t('dashboard.auth.personalize')}</span>
                  <span style={{ fontWeight: 700, color: 'var(--primary)', fontSize: '0.9rem' }}>{t('dashboard.auth.sync')}</span>
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
                {t('dashboard.quickActions.goNow')} <ArrowRight size={14} />
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
                  <TrendingUp size={20} className="text-blue" /> {t('dashboard.analytics.title')}
                </h3>
                {user && <Link to="/analytics" className="btn btn-sm btn-ghost">{t('dashboard.analytics.viewDetails')}</Link>}
              </div>

              {!user && (
                <div style={{ position: 'absolute', inset: 0, zIndex: 10, background: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(4px)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 20, textAlign: 'center' }}>
                  <Sparkles size={32} className="text-blue" style={{ marginBottom: 12 }} />
                  <p style={{ fontWeight: 700, fontSize: '0.9rem', marginBottom: 12 }}>{t('dashboard.analytics.locked')}</p>
                  <Link to="/login" className="btn btn-sm btn-primary">{t('dashboard.analytics.loginToUnlock')}</Link>
                </div>
              )}

              <div style={{ display: 'flex', flexDirection: 'column', gap: 16, filter: (user && !historyLoading) ? 'none' : 'blur(2px)', opacity: (user && !historyLoading) ? 1 : 0.5 }}>
                {historyLoading ? (
                  <div style={{ textAlign: 'center', padding: '20px' }}>
                    <RefreshCcw className="animate-spin text-blue" size={24} />
                  </div>
                ) : (
                  dynamicSubjects.length > 0 ? (
                    dynamicSubjects.map(sub => (
                      <div key={sub.name}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4, fontSize: '0.85rem' }}>
                          <span style={{ fontWeight: 500 }}>{sub.name}</span>
                          <span className="text-muted">{user ? sub.progress : '??'}%</span>
                        </div>
                        <div className="progress-bar-wrap" style={{ height: 10, background: 'var(--bg-secondary)', borderRadius: 20, overflow: 'hidden' }}>
                          <div 
                            className={`progress-bar-fill ${sub.color}`} 
                            style={{ 
                              width: user ? `${sub.progress}%` : '30%',
                              height: '100%',
                              transition: 'width 1s cubic-bezier(0.4, 0, 0.2, 1)',
                              borderRadius: 20
                            }} 
                          />
                        </div>
                      </div>
                    ))
                  ) : (
                    <div style={{ textAlign: 'center', padding: '20px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                      {t('dashboard.analytics.noData')}
                    </div>
                  )
                )}
              </div>
            </section>

            <section className="card" style={{ background: 'var(--primary-bg)', border: '1px solid var(--border-blue)' }}>
              <h4 style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8, color: 'var(--primary)' }}>
                <Zap size={18} fill="var(--primary)" /> {t('dashboard.suggestions.title')}
              </h4>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10 }}>
                {suggestions.map((s, i) => (
                  <Link 
                    key={i} 
                    to={s.path} 
                    className={`chip ${s.level === 'important' ? 'active' : ''}`}
                    style={{ 
                      textDecoration: 'none', 
                      display: 'inline-flex', 
                      alignItems: 'center', 
                      padding: '8px 16px', 
                      borderRadius: '20px', 
                      fontSize: '0.85rem', 
                      fontWeight: 500,
                      transition: 'all 0.3s ease',
                      border: s.level === 'important' ? '1px solid var(--primary)' : '1px solid var(--border-color)',
                      boxShadow: s.level === 'important' ? '0 4px 12px rgba(37,99,235,0.1)' : 'none'
                    }}
                    onMouseEnter={e => {
                      e.currentTarget.style.transform = 'translateY(-2px)';
                      e.currentTarget.style.boxShadow = '0 6px 15px rgba(0,0,0,0.05)';
                    }}
                    onMouseLeave={e => {
                      e.currentTarget.style.transform = 'translateY(0)';
                      e.currentTarget.style.boxShadow = s.level === 'important' ? '0 4px 12px rgba(37,99,235,0.1)' : 'none';
                    }}
                  >
                    {s.text}
                  </Link>
                ))}
              </div>
            </section>
          </div>

          {/* Right Column: Daily Feed */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            
            {/* AI Engine Status Card - High Capacity Mode */}
            <section className="card" style={{ border: '1px solid var(--accent-green)', background: 'rgba(0,201,167,0.05)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <h3 style={{ display: 'flex', alignItems: 'center', gap: 10, fontSize: '1rem', color: 'var(--accent-green)' }}>
                  <Zap size={18} fill="var(--accent-green)" /> 
                  AI High-Capacity Engine
                </h3>
                <span className="badge badge-green" style={{ fontSize: '0.7rem' }}>
                  SCALABLE MODE
                </span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem' }}>
                  <span className="text-muted">Total Available Slots:</span>
                  <span style={{ fontWeight: 800, color: 'var(--accent-green)' }}>{aiStatus.total} Slots Active</span>
                </div>
                <div style={{ display: 'flex', gap: 3, marginTop: 4, flexWrap: 'wrap' }}>
                  {[...Array(Math.min(aiStatus.total, 40))].map((_, i) => (
                    <div key={i} style={{ width: 6, height: 6, borderRadius: '1px', background: 'var(--accent-green)', opacity: 0.9 }} />
                  ))}
                  {aiStatus.total > 40 && <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>+ more than {aiStatus.total - 40} slots</span>}
                </div>
                <div style={{ padding: '8px', background: 'white', borderRadius: 8, marginTop: 4, border: '1px solid rgba(0,201,167,0.2)' }}>
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', lineHeight: 1.4 }}>
                    <strong>Scale Status:</strong> Engine is ready for 2,00,000+ daily requests. Random token cascade and health blacklisting is active.
                  </p>
                </div>
              </div>
            </section>

            {/* Daily Quiz Highlight */}
            <section className="card" style={{ background: 'var(--gradient-primary)', color: 'white', border: 'none', position: 'relative', overflow: 'hidden' }}>
              <div style={{ position: 'relative', zIndex: 2 }}>
                <p style={{ opacity: 0.9, fontSize: '0.8rem', fontWeight: 600, letterSpacing: 0.5 }}>{t('dashboard.challenge.badge')}</p>
                <h3 style={{ color: 'white', margin: '8px 0 16px' }}>{t('dashboard.challenge.title')}</h3>
                <Link to="/mock-test" className="btn btn-cta" style={{ borderRadius: 'var(--radius-full)' }}>
                  <Play size={16} fill="white" /> {t('dashboard.challenge.button')}
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
                <span className="badge badge-orange">Latest Updates</span>
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
                  View All Current Affairs <ArrowRight size={14} />
                </Link>
              </div>
            </section>
          </div>
        </div>
      </div>
    </main>
  );
}
