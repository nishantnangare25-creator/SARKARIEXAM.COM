import { useState, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useDropzone } from 'react-dropzone';
import { FileText, Search, Calendar, BookOpen, Loader2, UploadCloud } from 'lucide-react';
import { EXAMS } from '../utils/constants';
import { generatePYQSMockQuestions } from '../services/ai';
import { extractTextFromPdfFile } from '../utils/pdfParser';
import { generateQuestionPdf } from '../utils/pdfGenerator';

// Generate comprehensive 10-20 years of PYQs
const MOCK_PDFS = [];
let idCounter = 1;

const examsData = [
  { id: 'upsc', titles: ['UPSC Civil Services Prelims GS Paper 1', 'UPSC Prelims CSAT', 'UPSC Mains GS Paper 1', 'UPSC Mains GS Paper 2', 'UPSC Mains GS Paper 3', 'UPSC Mains GS Paper 4'], startYear: 2004 },
  { id: 'mpsc', titles: ['MPSC Rajyaseva Prelims Paper 1', 'MPSC Rajyaseva Prelims CSAT', 'MPSC Mains GS 1'], startYear: 2010 },
  { id: 'ssc', titles: ['SSC CGL Tier 1 Quantitative Aptitude', 'SSC CGL Tier 1 General Awareness', 'SSC CGL Tier 2 Maths'], startYear: 2014 },
  { id: 'banking', titles: ['IBPS PO Prelims Reasoning', 'IBPS PO Prelims Quantitative Aptitude', 'SBI Clerk Mains General Awareness'], startYear: 2015 },
  { id: 'railway', titles: ['RRB NTPC Stage 1 CBT', 'RRB Group D General Science'], startYear: 2015 },
  { id: 'nda', titles: ['NDA Mathematics Paper 1', 'NDA General Ability Test'], startYear: 2014 },
  { id: 'state_psc', titles: ['BPSC Prelims (Bihar PSC)', 'UPPSC Prelims GS', 'RPSC RAS Prelims'], startYear: 2014 }
];

const currentYear = 2024;
examsData.forEach(exam => {
  for (let year = currentYear; year >= exam.startYear; year--) {
    exam.titles.forEach(title => {
      MOCK_PDFS.push({
        id: idCounter++,
        title: title,
        examId: exam.id,
        year: year,
        size: (Math.random() * 3 + 1.2).toFixed(1) + ' MB',
        type: year % 4 === 0 ? 'Question Paper + Solution' : 'Question Paper'
      });
    });
  }
});

export default function PYQPdfs() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const [filterExam, setFilterExam] = useState('');
  const [filterYear, setFilterYear] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [isParsing, setIsParsing] = useState(false);

  const onDrop = async (acceptedFiles) => {
    const file = acceptedFiles[0];
    if (!file) return;
    
    setIsParsing(true);
    try {
      const text = await extractTextFromPdfFile(file);
      navigate('/pyq-practice', { state: { pdfInfo: { title: file.name, type: 'Custom Upload' }, extractedText: text } });
    } catch (e) {
      alert(t('common.error') || "Failed to read the PDF. Please ensure it's a valid text-based PDF document.");
    } finally {
      setIsParsing(false);
    }
  };

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { 'application/pdf': ['.pdf'] },
    maxFiles: 1
  });

  const filteredPdfs = useMemo(() => {
    return MOCK_PDFS.filter(pdf => {
      const matchExam = filterExam === '' || pdf.examId === filterExam;
      const matchYear = filterYear === '' || pdf.year.toString() === filterYear;
      const matchSearch = pdf.title.toLowerCase().includes(searchQuery.toLowerCase());
      return matchExam && matchYear && matchSearch;
    });
  }, [filterExam, filterYear, searchQuery]);

  return (
    <main className="page-wrapper">
      <div className="page-with-sidebar">
        
        <header className="page-header animate-fadeInUp">
          <p className="badge badge-saffron" style={{ marginBottom: 8 }}>{t('pyq.badge')}</p>
          <h1><FileText size={28} className="text-saffron" style={{ verticalAlign: 'middle', marginRight: 12 }} /> {t('pyq.title')}</h1>
          <p>{t('pyq.subtitle')}</p>
        </header>

        <section className="animate-fadeInUp" style={{ marginBottom: 32 }}>
          <div 
            {...getRootProps()} 
            style={{ 
              border: `2px dashed ${isDragActive ? 'var(--primary)' : 'var(--border-light)'}`,
              background: isDragActive ? 'rgba(var(--primary-rgb), 0.05)' : 'var(--bg-secondary)',
              borderRadius: 12,
              padding: '40px 20px',
              textAlign: 'center',
              cursor: 'pointer',
              transition: 'all 0.2s ease',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 12
            }}
          >
            <input {...getInputProps()} />
            {isParsing ? (
              <>
                <Loader2 size={48} className="text-primary animate-spin" />
                <h3 style={{ margin: 0 }}>{t('pyq.upload.reading')}</h3>
                <p style={{ margin: 0, color: 'var(--text-secondary)' }}>{t('pyq.upload.readingDesc')}</p>
              </>
            ) : (
              <>
                <UploadCloud size={48} className={isDragActive ? "text-primary" : "text-muted"} />
                <h3 style={{ margin: 0 }}>{t('pyq.upload.drag')}</h3>
                <p style={{ margin: 0, color: 'var(--text-secondary)' }}>{t('pyq.upload.click')}</p>
              </>
            )}
          </div>
        </section>

        <section className="card animate-fadeInUp" style={{ marginBottom: 24, display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
            <div className="nav-search" style={{ flex: 1, minWidth: '250px', background: 'var(--bg-tertiary)', padding: '10px 16px', borderRadius: 8, display: 'flex', alignItems: 'center', gap: 12 }}>
              <Search size={18} color="var(--text-secondary)" />
              <input 
                type="text" 
                placeholder={t('pyq.searchPlaceholder')} 
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                style={{ border: 'none', background: 'transparent', flex: 1, color: 'var(--text-primary)', outline: 'none' }}
              />
            </div>
            <div className="input-group" style={{ minWidth: '150px' }}>
              <select value={filterYear} onChange={e => setFilterYear(e.target.value)} style={{ height: '100%', border: '1px solid var(--border-light)' }}>
                <option value="">{t('pyq.allYears') || 'All Years'}</option>
                {Array.from({length: 21}, (_, i) => 2024 - i).map(y => <option key={y} value={y}>{y}</option>)}
              </select>
            </div>
            <div className="input-group" style={{ minWidth: '200px' }}>
              <select value={filterExam} onChange={e => setFilterExam(e.target.value)} style={{ height: '100%', border: '1px solid var(--border-light)' }}>
                <option value="">{t('pyq.allExams') || 'All Exams'}</option>
                {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
              </select>
            </div>
          </div>
        </section>

        <section className="animate-fadeInUp" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 20 }}>
          {filteredPdfs.length > 0 ? (
            filteredPdfs.map(pdf => {
              const examName = EXAMS.find(e => e.id === pdf.examId)?.name || 'General';
              return (
                <div key={pdf.id} className="card" style={{ display: 'flex', flexDirection: 'column', padding: 20, borderTop: '4px solid var(--primary)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
                    <span className="badge badge-blue">{examName}</span>
                    <span className="badge" style={{ background: 'var(--bg-tertiary)' }}><Calendar size={12} style={{ marginRight: 4 }}/> {pdf.year}</span>
                  </div>
                  <h3 style={{ fontSize: '1.1rem', marginBottom: 8, lineHeight: 1.4 }}>{pdf.title}</h3>
                  <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', marginBottom: 24, display: 'flex', alignItems: 'center', gap: 6 }}>
                     <FileText size={14} /> {pdf.type} • {pdf.size}
                  </p>
                  <div className="download-actions" style={{ marginTop: 'auto' }}>
                    <button 
                      className="btn btn-primary" 
                      style={{ flex: 1 }}
                      onClick={() => navigate('/pyq-practice', { state: { pdfInfo: pdf } })}
                    >
                      <BookOpen size={16} /> {t('pyq.startPractice')}
                    </button>
                  </div>
                </div>
              );
            })
          ) : (
            <div className="card" style={{ gridColumn: '1 / -1', textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
              <FileText size={48} style={{ opacity: 0.2, marginBottom: 16 }} />
              <h3>{t('pyq.noMaterials')}</h3>
              <p>{t('pyq.noMaterialsDesc')}</p>
            </div>
          )}
        </section>

      </div>
    </main>
  );
}
