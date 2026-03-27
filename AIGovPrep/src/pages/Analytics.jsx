import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { BarChart3, TrendingUp, Target, Brain, ArrowUp, ArrowDown, Minus, Sparkles } from 'lucide-react';
import { analyzePerformance } from '../services/ai';
import { getTestHistory } from '../services/firebase';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import './Auth.css';

export default function Analytics() {
  const { t } = useTranslation();
  const { user, profile } = useAuth();
  const [analysis, setAnalysis] = useState('');
  const [testHistory, setTestHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;
    if (user?.uid) {
      getTestHistory(user.uid)
        .then(data => {
          if (isMounted) setTestHistory(data);
        })
        .catch(console.error);
    }
    return () => { isMounted = false; };
  }, [user]);

  const handleAnalyze = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await analyzePerformance({ testHistory, exam: profile?.exam || 'UPSC', language: profile?.language });
      setAnalysis(result);
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><BarChart3 size={28} style={{ verticalAlign: 'middle' }} /> {t('analytics.title')}</h1>
          <p>{t('analytics.subtitle')}</p>
        </div>

        {!user ? (
          <div className="card animate-fadeInUp" style={{ padding: '80px 40px', textAlign: 'center', background: 'var(--bg-card)', border: '1px dashed var(--border-color)', borderRadius: '24px' }}>
            <div className="feature-icon purple" style={{ width: 80, height: 80, margin: '0 auto 24px', fontSize: '2rem' }}>
              <Sparkles size={40} />
            </div>
            <h2 style={{ marginBottom: 12 }}>Unlock AI Performance Analytics</h2>
            <p style={{ maxWidth: 500, margin: '0 auto 24px', color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
              Detailed performance reports, weakness identification, and personalized prep strategies require a free account to track your history.
            </p>
            <div style={{ display: 'flex', gap: 16, justifyContent: 'center', flexWrap: 'wrap' }}>
              <Link to="/login" className="btn btn-primary btn-lg">Login / Sign Up</Link>
              <Link to="/mock-test" className="btn btn-secondary btn-lg">Try a Free Test First</Link>
            </div>
          </div>
        ) : (
          <>
            <section className="card animate-fadeInUp" style={{ marginBottom: 24 }}>
               <button className="btn btn-primary" onClick={handleAnalyze} disabled={loading} aria-busy={loading}>
                 {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} aria-hidden="true" /> Generating...</> : <><Sparkles size={18} aria-hidden="true" /> Generate Performance Report</>}
               </button>
               {error && <p role="alert" style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
            </section>

            {analysis && (
              <div className="animate-fadeInUp">
                <section className="card">
                  <div className="text-answer-card">
                     <ReactMarkdown remarkPlugins={[remarkGfm]}>{analysis}</ReactMarkdown>
                  </div>
                </section>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
