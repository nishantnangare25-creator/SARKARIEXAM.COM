import { useState, useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { analyzePastPaper } from '../services/ai';
import { EXAMS } from '../utils/constants';
import { FileText, Upload, Sparkles, TrendingUp, BarChart2, Target, BookOpen, Download } from 'lucide-react';
import './Auth.css';

import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { generateNotesPdf } from '../utils/pdfGenerator';

export default function PastPaperAnalyzer() {
  const { t, i18n } = useTranslation();
  const { profile } = useAuth();
  const [exam, setExam] = useState(profile?.exam || '');
  const [text, setText] = useState('');
  const [analysis, setAnalysis] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fileName, setFileName] = useState('');

  const handleFile = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setFileName(file.name);
    
    // Read text from file
    if (file.type === 'text/plain') {
      const reader = new FileReader();
      reader.onload = (ev) => setText(ev.target.result);
      reader.readAsText(file);
    } else {
      // For PDF, we'll ask user to paste text or try basic extraction
      setText('');
      setError(t('pastPaper.pdfExtractionError'));
    }
  };

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    if (file) {
      setFileName(file.name);
      if (file.type === 'text/plain') {
        const reader = new FileReader();
        reader.onload = (ev) => setText(ev.target.result);
        reader.readAsText(file);
      }
    }
  }, []);

  const analyze = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await analyzePastPaper({ text, exam, language: i18n.language || profile?.language });
      setAnalysis(result);
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const downloadAnalysis = async () => {
    if (!analysis) return;
    const filename = `Past_Paper_Analysis.md`;
    const mimeType = 'text/markdown;charset=utf-8';

    if (navigator.share && navigator.canShare) {
      try {
        const file = new File([analysis], filename, { type: mimeType });
        if (navigator.canShare({ files: [file] })) {
          await navigator.share({ title: 'Past Paper Analysis', files: [file] });
          return;
        }
      } catch (err) {
        console.log('Web Share failed', err);
      }
    }

    const fileBlob = new Blob([analysis], { type: mimeType });
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

  const downloadAnalysisPdf = () => {
    if (!analysis) return;
    generateNotesPdf(
      'Past Paper Analysis',
      analysis,
      'Past_Paper_Analysis.pdf'
    );
  };

  return (
    <main className="page-wrapper" id="past-paper-analyzer">
      <div className="page-with-sidebar">
        <header className="page-header animate-fadeInUp">
          <h1><FileText size={28} style={{ verticalAlign: 'middle' }} aria-hidden="true" /> {t('pastPaper.title')}</h1>
          <p>{t('pastPaper.subtitle')}</p>
        </header>

        <section className="card animate-fadeInUp" style={{ marginBottom: 24 }}>
          <div className="input-group" style={{ marginBottom: 16 }}>
            <label htmlFor="exam-select">{t('studyPlanner.exam')}</label>
            <select id="exam-select" value={exam} onChange={e => setExam(e.target.value)}>
              <option value="">{t('common.select') || 'Select Exam'}</option>
              {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
            </select>
          </div>

          <div onDrop={handleDrop} onDragOver={e => e.preventDefault()}
            style={{ border: '2px dashed var(--border-light)', borderRadius: 16, padding: 40, textAlign: 'center', cursor: 'pointer', transition: 'all 0.2s', background: 'var(--bg-glass)', marginBottom: 16 }}
            onClick={() => document.getElementById('pdf-upload').click()}
            role="button"
            aria-label="Upload exam paper PDF or Text"
            tabIndex={0}>
            <Upload size={32} color="var(--primary)" aria-hidden="true" />
            <p style={{ marginTop: 8, fontWeight: 500 }}>{t('pastPaper.dropzone')}</p>
            {fileName && <p style={{ marginTop: 4, fontSize: '0.85rem', color: 'var(--accent-green)' }}>📄 {fileName}</p>}
            <input id="pdf-upload" type="file" accept=".pdf,.txt" onChange={handleFile} style={{ display: 'none' }} />
          </div>

          <div className="input-group" style={{ marginBottom: 16 }}>
            <label htmlFor="exam-text">{t('pastPaper.pasteLabel')}</label>
            <textarea id="exam-text" value={text} onChange={e => setText(e.target.value)} rows={6} placeholder={t('pastPaper.pastePlaceholder')} />
          </div>

          <button className="btn btn-primary" onClick={analyze} disabled={loading || !text} aria-busy={loading}>
            {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} aria-hidden="true" /> {t('pastPaper.analyzing')}</> : <><Sparkles size={18} aria-hidden="true" /> {t('pastPaper.analyzeBtn')}</>}
          </button>
          {error && <p role="alert" style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
        </section>

        {analysis && (
          <div className="animate-fadeInUp">
            <div className="download-actions" style={{ justifyContent: 'flex-end', marginBottom: 16 }}>
              <button className="btn btn-secondary btn-sm" onClick={downloadAnalysisPdf}>
                <Download size={16} /> {t('pastPaper.downloadPdf')}
              </button>
              <button className="btn btn-outline btn-sm" onClick={downloadAnalysis}>
                <Download size={16} /> {t('pastPaper.downloadMd')}
              </button>
            </div>
            <section className="card" style={{ marginBottom: 24 }}>
              <div id="analysis-content" className="text-answer-card">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>{analysis || ''}</ReactMarkdown>
              </div>
            </section>
          </div>
        )}
      </div>
    </main>
  );
}
