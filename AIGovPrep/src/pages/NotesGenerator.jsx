import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { generateNotes } from '../services/ai';
import { EXAMS, SUBJECTS } from '../utils/constants';
import { GraduationCap, Sparkles, Download } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import './Auth.css';
import { generateNotesPdf } from '../utils/pdfGenerator';

export default function NotesGenerator() {
  const { t, i18n } = useTranslation();
  const { profile } = useAuth();
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
      const result = await generateNotes({ exam, subject, topics: topics ? topics.split(',').map(t => t.trim()) : null, language: i18n.language || profile?.language });
      setNotes(result);
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

        <div className="card animate-fadeInUp" style={{ marginBottom: 24 }}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16, marginBottom: 16 }}>
            <div className="input-group">
              <label>{t('notes.selectExam')}</label>
              <select value={exam} onChange={e => { setExam(e.target.value); setSubject(''); }}>
                <option value="">Select Exam</option>
                {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
              </select>
            </div>
            <div className="input-group">
              <label>{t('notes.selectSubject')}</label>
              <select value={subject} onChange={e => setSubject(e.target.value)}>
                <option value="">Select Subject</option>
                {subjects.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
          </div>
          <div className="input-group" style={{ marginBottom: 16 }}>
            <label>{t('notes.topics')}</label>
            <input type="text" value={topics} onChange={e => setTopics(e.target.value)} placeholder="e.g. Indian Independence, Fundamental Rights" />
          </div>
          <button className="btn btn-primary" onClick={handleGenerate} disabled={loading || !exam || !subject}>
            {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} /> {t('notes.generating')}</> : <><Sparkles size={18} /> {t('notes.generate')}</>}
          </button>
          {error && <p style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
        </div>

        {notes && (
          <div className="animate-fadeInUp">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3>{t('notes.yourNotes')}</h3>
              <div style={{ display: 'flex', gap: 12 }}>
                <button className="btn btn-secondary btn-sm" onClick={downloadNotes}>
                  <Download size={16} /> PDF
                </button>
              </div>
            </div>
            <div id="notes-content" className="card" style={{ lineHeight: 1.8 }}>
              <ReactMarkdown>{notes}</ReactMarkdown>
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
