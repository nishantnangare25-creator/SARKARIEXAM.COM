import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { 
  BarChart3, TrendingUp, Target, Brain, 
  ArrowUp, ArrowDown, Minus, Sparkles, 
  ChevronRight, Award, Zap, Activity, Clock
} from 'lucide-react';
import { analyzePerformance } from '../services/ai';
import { getTestHistory } from '../services/firebase';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import './Analytics.css';

// --- MOCK DATA FOR DEMO ---
const MOCK_STATS = {
  accuracy: 78,
  consistency: 92,
  completion: 45,
  subjects: [
    { name: 'Indian Polity', score: 85, color: '#2563eb' },
    { name: 'History & Culture', score: 62, color: '#f97316' },
    { name: 'Geography', score: 74, color: '#10b981' },
    { name: 'Economy', score: 48, color: '#ef4444' },
    { name: 'Current Affairs', score: 91, color: '#8b5cf6' }
  ]
};

const Gauge = ({ value, label, icon: Icon, color = 'var(--primary)', t }) => {
  const radius = 60;
  const circumference = 2 * Math.PI * radius;
  const offset = circumference - (value / 100) * circumference;

  return (
    <div className="analytics-card" style={{ textAlign: 'center' }}>
      <div className="gauge-container">
        <svg className="gauge-svg" viewBox="0 0 140 140">
          <circle className="gauge-bg" cx="70" cy="70" r={radius} />
          <circle 
            className="gauge-progress" 
            cx="70" cy="70" r={radius} 
            style={{ strokeDashoffset: offset, stroke: color }}
          />
        </svg>
        <div className="gauge-text">
          <div className="gauge-value">{value}%</div>
          <div className="gauge-label">{label}</div>
        </div>
      </div>
      <div style={{ marginTop: 16, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, color: color, fontWeight: 700, fontSize: '0.85rem' }}>
        <Icon size={16} /> 
        {value > 70 ? t('analytics.stats.excellent') : value > 40 ? t('analytics.stats.steady') : t('analytics.stats.needsFocus')}
      </div>
    </div>
  );
};

export default function Analytics() {
  const { t } = useTranslation();
  const { user, profile } = useAuth();
  const [analysis, setAnalysis] = useState('');
  const [testHistory, setTestHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showMock, setShowMock] = useState(false);

  useEffect(() => {
    let isMounted = true;
    if (user?.uid) {
      getTestHistory(user.uid)
        .then(data => {
          if (isMounted) {
            setTestHistory(data);
            if (data.length === 0) setShowMock(true); // Show mock if history is empty
          }
        })
        .catch(console.error);
    }
    return () => { isMounted = false; };
  }, [user]);

  const handleAnalyze = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await analyzePerformance({ 
        testHistory: showMock ? [] : testHistory, 
        exam: profile?.exam || 'UPSC', 
        language: profile?.language 
      });
      setAnalysis(result);
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const displayStats = showMock ? MOCK_STATS : {
    accuracy: testHistory.length > 0 
      ? Math.round(testHistory.reduce((acc, curr) => acc + (curr.score/curr.total)*100, 0) / testHistory.length) 
      : 0,
    consistency: Math.min(100, (testHistory?.length || 0) * 10),
    completion: Math.min(100, Math.round(((testHistory?.length || 0) / 50) * 100)),
    subjects: MOCK_STATS.subjects // Fallback subjects for clean UI
  };

  return (
    <div className="page-wrapper" style={{ background: '#F8FAFC' }}>
      <div className="page-with-sidebar">
        
        {/* Header */}
        <header className="page-header animate-fadeInUp" style={{ marginBottom: 32 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
            <div className="feature-icon blue" style={{ width: 44, height: 44, borderRadius: '12px' }}>
              <BarChart3 size={24} />
            </div>
            <h1 style={{ margin: 0, fontSize: '1.75rem' }}>{t('analytics.title')}</h1>
          </div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem' }}>
            {t('analytics.subtitle')}
          </p>
        </header>

        {!user ? (
          <div className="card animate-fadeInUp" style={{ padding: '80px 40px', textAlign: 'center', background: '#FFFFFF', border: '1px solid var(--border-color)', borderRadius: '32px', boxShadow: 'var(--shadow-lg)' }}>
            <div className="feature-icon purple" style={{ width: 80, height: 80, margin: '0 auto 24px', borderRadius: '24px' }}>
              <Sparkles size={40} />
            </div>
            <h2 style={{ marginBottom: 12, fontSize: '2rem' }}>{t('analytics.locked.title')}</h2>
            <p style={{ maxWidth: 550, margin: '0 auto 32px', color: 'var(--text-secondary)', fontSize: '1.15rem', lineHeight: 1.6 }}>
              {t('analytics.locked.desc')}
            </p>
            <div style={{ display: 'flex', gap: 16, justifyContent: 'center', flexWrap: 'wrap' }}>
              <Link to="/login" className="btn btn-primary btn-lg" style={{ padding: '16px 40px', borderRadius: '16px' }}>
                {t('analytics.locked.join')}
              </Link>
              <Link to="/mock-test" className="btn btn-outline btn-lg" style={{ padding: '16px 40px', borderRadius: '16px' }}>
                {t('analytics.locked.tryMock')}
              </Link>
            </div>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 32 }}>
            
            {/* Stats Overview Grid */}
            <div className="analytics-grid animate-fadeInUp">
              <Gauge value={displayStats.accuracy} label={t('analytics.stats.accuracy')} icon={Target} color="#2563eb" t={t} />
              <Gauge value={displayStats.consistency} label={t('analytics.stats.consistency')} icon={Zap} color="#f97316" t={t} />
              <Gauge value={displayStats.completion} label={t('analytics.stats.completion')} icon={Activity} color="#10b981" t={t} />
            </div>

            {/* Main Dashboard Layout */}
            <div className="grid-2 animate-fadeInUp" style={{ animationDelay: '0.2s' }}>
              
              {/* Subject Performance */}
              <div className="analytics-card">
                <div className="analytics-card-header">
                  <Brain size={20} className="text-purple" />
                  {t('analytics.breakdown.title')}
                </div>
                <div className="subject-performance-list">
                  {displayStats.subjects.map((s, i) => (
                    <div key={i} className="subject-item">
                      <div className="subject-info">
                        <span className="subject-name">{s.name}</span>
                        <span className="subject-score">{s.score}%</span>
                      </div>
                      <div className="bar-container">
                        <div 
                          className="bar-fill" 
                          style={{ width: `${s.score}%`, background: s.color }} 
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Tips & Guidance */}
              <div className="analytics-card" style={{ background: '#0F172A', color: '#FFFFFF', borderColor: '#1E293B' }}>
                <div className="analytics-card-header" style={{ color: '#FFFFFF' }}>
                  <Award size={20} className="text-saffron" />
                  {t('analytics.milestones.title')}
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
                  <div style={{ display: 'flex', gap: 12 }}>
                    <div style={{ background: 'rgba(255,255,255,0.1)', padding: 10, borderRadius: 12 }}>
                      <TrendingUp size={20} className="text-green" />
                    </div>
                    <div>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>{t('analytics.milestones.builder.title')}</div>
                      <p style={{ fontSize: '0.8rem', color: 'rgba(255,255,255,0.6)', marginTop: 2 }}>{t('analytics.milestones.builder.desc')}</p>
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: 12 }}>
                    <div style={{ background: 'rgba(255,255,255,0.1)', padding: 10, borderRadius: 12 }}>
                      <Clock size={20} className="text-blue" />
                    </div>
                    <div>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>{t('analytics.milestones.time.title')}</div>
                      <p style={{ fontSize: '0.8rem', color: 'rgba(255,255,255,0.6)', marginTop: 2 }}>{t('analytics.milestones.time.desc')}</p>
                    </div>
                  </div>
                  <div style={{ marginTop: 12, padding: 16, background: 'rgba(255,255,255,0.05)', borderRadius: 16, border: '1px solid rgba(255,255,255,0.1)' }}>
                    <div style={{ fontSize: '0.8rem', fontWeight: 700, color: 'var(--accent-saffron)', marginBottom: 4 }}>{t('analytics.proTip.title')}</div>
                    <p style={{ fontSize: '0.85rem', lineHeight: 1.5 }}>{t('analytics.proTip.desc')}</p>
                  </div>
                </div>
              </div>

            </div>

            {/* AI Deep Dive Section */}
            <div className="ai-report-banner animate-fadeInUp" style={{ animationDelay: '0.4s' }}>
              <div className="ai-banner-content">
                <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 8 }}>
                  <Sparkles size={24} className="text-blue" />
                  <h3 style={{ margin: 0 }}>{t('analytics.deepDive.title')}</h3>
                </div>
                <p>
                  {t('analytics.deepDive.desc')}
                </p>
              </div>
              <button 
                className="btn btn-primary btn-lg" 
                onClick={handleAnalyze} 
                disabled={loading}
                style={{ borderRadius: '16px', boxShadow: '0 10px 25px rgba(37,99,235,0.2)' }}
              >
                {loading ? <Loader2 size={20} className="animate-spin" /> : t('analytics.deepDive.button')}
              </button>
            </div>

            {analysis && (
              <div className="animate-fadeInUp">
                <section className="card" style={{ border: '2px solid var(--primary-light)' }}>
                  <div className="text-answer-card">
                     <ReactMarkdown remarkPlugins={[remarkGfm]}>{analysis || ''}</ReactMarkdown>
                  </div>
                </section>
              </div>
            )}
            
            {showMock && (
              <div className="alert alert-info" style={{ borderRadius: '12px' }}>
                <Zap size={16} /> <strong>Note:</strong> {t('analytics.demoMode')}
              </div>
            )}

          </div>
        )}
      </div>
    </div>
  );
}

// Simple Loader icon since we used it in JSX
const Loader2 = ({ size, className }) => <Activity size={size} className={className} />;
