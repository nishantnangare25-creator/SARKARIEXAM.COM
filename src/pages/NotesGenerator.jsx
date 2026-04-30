import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { generateNotes } from '../services/ai';
import { EXAMS, SUBJECTS } from '../utils/constants';
import { GraduationCap, Sparkles, Download, Zap } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import './Auth.css';
import { generateNotesPdf } from '../utils/pdfGenerator';

export default function NotesGenerator() {
  const { t, i18n } = useTranslation();
  const { user, profile } = useAuth();
  const [exam, setExam] = useState(profile?.exam || '');
  const [subject, setSubject] = useState('');
  const [topics, setTopics] = useState('');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const subjects = exam ? SUBJECTS[exam] || [] : [];

  const handleGenerate = async () => {
    setLoading(true);
    setError('');
    try {
      const language = i18n.language || profile?.language || 'en';
      const result = await generateNotes({ exam, subject, topics: topics ? topics.split(',').map(t => t.trim()) : null, language });
      setNotes(result);
      if (!user) {
        localStorage.setItem('sarkari_trial_used', 'true');
      }
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const downloadNotes = async () => {
    const filename = `${subject || exam || 'study'}_notes.pdf`;
    generateNotesPdf(
      `${subject || 'Study'} Notes`, 
      notes, 
      filename
    );
  };

  const downloadNotesText = () => {
    const filename = `${subject || exam || 'study'}_notes.md`;
    const blob = new Blob([notes], { type: 'text/markdown;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><GraduationCap size={28} style={{ verticalAlign: 'middle' }} /> {t('notes.title')}</h1>
          <p>{t('notes.subtitle')}</p>
        </div>

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
                  You have experienced our AI Notes Generator. To generate more notes and download PDFs, please login.
                </p>
                <Link to="/login" className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center' }}>
                  Login to Continue
                </Link>
              </section>
            );
          }

          return (
            <div className="card animate-fadeInUp" style={{ marginBottom: 24 }}>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16, marginBottom: 16 }}>
                <div className="input-group">
                  <label>{t('notes.selectExam')}</label>
                  <select value={exam} onChange={e => { setExam(e.target.value); setSubject(''); }}>
                    <option value="">{t('common.select') || 'Select Exam'}</option>
                    {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
                  </select>
                </div>
                <div className="input-group">
                  <label>{t('notes.selectSubject')}</label>
                  <select value={subject} onChange={e => setSubject(e.target.value)}>
                    <option value="">{t('common.select') || 'Select Subject'}</option>
                    {subjects.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </div>
              </div>
              <div className="input-group" style={{ marginBottom: 16 }}>
                <label>{t('notes.topics')}</label>
                <input type="text" value={topics} onChange={e => setTopics(e.target.value)} placeholder={t('notes.topics') + ' (e.g. Indian Independence, Fundamental Rights)'} />
              </div>
              <button className="btn btn-primary" onClick={handleGenerate} disabled={loading || !exam || !subject}>
                {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} /> {t('notes.generating')}</> : <><Sparkles size={18} /> {t('notes.generate')}</>}
              </button>
              {error && <p style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
            </div>
          );
        })()}

        {notes && (
          <div className="animate-fadeInUp">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3>{t('notes.yourNotes')}</h3>
              <div className="download-actions">
                <button className="btn btn-secondary btn-sm" onClick={downloadNotes}>
                  <Download size={16} /> {t('notes.downloadPdf') || 'PDF'}
                </button>
                <button className="btn btn-outline btn-sm" onClick={downloadNotesText}>
                  <Download size={16} /> {t('notes.downloadText') || 'MD/Text'}
                </button>
              </div>
            </div>
            <div id="notes-content" className="card" style={{ lineHeight: 1.8 }}>
              <ReactMarkdown>{notes || ''}</ReactMarkdown>
            </div>
          </div>
        )}
      </div>

      <style>{`
        .page-with-sidebar .card h1, .page-with-sidebar .card h2, .page-with-sidebar .card h3, .page-with-sidebar .card h4 { margin: 16px 0 8px; }
        .page-with-sidebar .card ul, .page-with-sidebar .card ol { padding-left: 24px; }
        .page-with-sidebar .card li { margin-bottom: 4px; color: var(--text-secondary); }
        .page-with-sidebar .card p { margin-bottom: 8px; }
        .page-with-sidebar .card strong { color: var(--text-primary); }
        .page-with-sidebar .card code { background: var(--bg-glass); padding: 2px 6px; border-radius: 4px; font-size: 0.85em; }
      `}</style>
    </div>
  );
}
