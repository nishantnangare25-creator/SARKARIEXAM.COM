import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { generateStudyPlan } from '../services/ai';
import { EXAMS, SUBJECTS, PREP_LEVELS } from '../utils/constants';
import { Link } from 'react-router-dom';
import { BookOpen, Sparkles, Clock, CheckCircle, Download, Zap } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import './Auth.css';
import { generateNotesPdf } from '../utils/pdfGenerator';

export default function StudyPlanner() {
  const { t } = useTranslation();
  const { user, profile } = useAuth();
  const [exam, setExam] = useState(profile?.exam || '');
  const [hours, setHours] = useState(profile?.hours || 4);
  const [level, setLevel] = useState(profile?.level || 'beginner');
  const [plan, setPlan] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const subjects = exam ? SUBJECTS[exam] || [] : [];

  const handleGenerate = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await generateStudyPlan({ exam, hours, level, weakSubjects: profile?.weakSubjects, strongSubjects: profile?.strongSubjects, language: profile?.language || 'en' });
      setPlan(result);
      if (!user) {
        localStorage.setItem('sarkari_trial_used', 'true');
      }
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const downloadPlan = async () => {
    if (!plan) return;
    const filename = `${exam || 'Study'}_Plan.md`;
    const mimeType = 'text/markdown;charset=utf-8';

    if (navigator.share && navigator.canShare) {
      try {
        const file = new File([plan], filename, { type: mimeType });
        if (navigator.canShare({ files: [file] })) {
          await navigator.share({ title: 'Study Plan', files: [file] });
          return;
        }
      } catch (err) {
        console.log('Web Share failed', err);
      }
    }

    const fileBlob = new Blob([plan], { type: mimeType });
    const url = URL.createObjectURL(fileBlob);
    const textElement = document.createElement("a");
    textElement.style.display = 'none';
    textElement.href = url;
    textElement.download = filename;
    document.body.appendChild(textElement);
    
    textElement.click();
    
    setTimeout(() => {
      document.body.removeChild(textElement);
      URL.revokeObjectURL(url);
    }, 1000);
  };

  const downloadPlanPdf = () => {
    if (!plan) return;
    const filename = `${exam || 'Study'}_Plan.pdf`;
    generateNotesPdf(`${exam.toUpperCase()} Study Plan`, plan, filename);
  };

  const dayColors = ['#4338CA', '#059669', '#EA6C10', '#0284C7', '#DB2777', '#F59E0B', '#6366F1'];

  return (
    <main className="page-wrapper" id="study-planner">
      <div className="page-with-sidebar">
        <header className="page-header animate-fadeInUp">
          <h1><BookOpen size={28} style={{ verticalAlign: 'middle' }} aria-hidden="true" /> {t('studyPlanner.title')}</h1>
          <p>{t('studyPlanner.subtitle')}</p>
        </header>

        {(() => {
          const trialUsed = localStorage.getItem('sarkari_trial_used') === 'true';
          if (trialUsed && !user) {
            return (
              <section className="card-trial-barrier animate-fadeInUp">
                <div className="feature-icon blue" style={{ margin: '0 auto', width: 64, height: 64 }}>
                  <Zap size={32} fill="currentColor" />
                </div>
                <h2 style={{ marginBottom: 12 }}>Trial Completed</h2>
                <p style={{ color: 'var(--text-secondary)', marginBottom: 24, lineHeight: 1.6 }}>
                  You have successfully created your first AI Study Plan. To generate more plans and stay organized, please login.
                </p>
                <Link to="/login" className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center', textDecoration: 'none' }}>
                  Login to Continue
                </Link>
              </section>
            );
          }

          return (
            <section className="card animate-fadeInUp" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16 }}>
                <div className="input-group">
                  <label htmlFor="exam-select">{t('studyPlanner.exam')}</label>
                  <select id="exam-select" value={exam} onChange={e => setExam(e.target.value)}>
                    <option value="">{t('onboarding.step1')}</option>
                    {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
                  </select>
                </div>
                <div className="input-group">
                  <label htmlFor="hours-select">{t('studyPlanner.hours')}</label>
                  <select id="hours-select" value={hours} onChange={e => setHours(Number(e.target.value))}>
                    {[1,2,3,4,5,6,7,8,9,10,11,12].map(h => <option key={h} value={h}>{h} {t('dashboard.stats.days').includes('दिन') ? 'घंटे' : 'hrs'}</option>)}
                  </select>
                </div>
                <div className="input-group">
                  <label htmlFor="level-select">{t('studyPlanner.level')}</label>
                  <select id="level-select" value={level} onChange={e => setLevel(e.target.value)}>
                    {PREP_LEVELS.map(l => <option key={l} value={l}>{t(`studyPlanner.${l}`)}</option>)}
                  </select>
                </div>
              </div>
              <button className="btn btn-primary" style={{ marginTop: 16 }} onClick={handleGenerate} disabled={loading || !exam} aria-busy={loading}>
                {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} aria-hidden="true" /> {t('studyPlanner.generating')}</> : <><Sparkles size={18} aria-hidden="true" /> {t('studyPlanner.generate')}</>}
              </button>
              {error && <p role="alert" style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
            </section>
          );
        })()}

        {plan && (
          <div className="animate-fadeInUp">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3>{t('studyPlanner.yourPlan')}</h3>
              <div className="download-actions">
                <div className="badge badge-primary">{t('studyPlanner.exam')}: {exam.toUpperCase()}</div>
                <button className="btn btn-secondary btn-sm" onClick={downloadPlanPdf}>
                  <Download size={14} style={{ marginRight: 4 }} /> PDF
                </button>
                <button className="btn btn-outline btn-sm" onClick={downloadPlan}>
                  <Download size={14} style={{ marginRight: 4 }} /> Text/MD
                </button>
              </div>
            </div>
            <section className="card" style={{ marginBottom: 24 }}>
              <div id="plan-content" className="text-answer-card">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{plan || ''}</ReactMarkdown>
              </div>
            </section>
          </div>
        )}
      </div>
    </main>
  );
}
