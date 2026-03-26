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
          <p>{t('analytics.subtitle')} - Markdown View</p>
        </div>

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
      </div>
    </div>
  );
}
