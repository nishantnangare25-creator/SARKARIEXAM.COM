import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { generatePYQSMockQuestions } from '../services/ai';
import { EXAMS, SUBJECTS } from '../utils/constants';
import { FileText, Clock, CheckCircle, XCircle, Sparkles, ArrowRight, RotateCcw, Download, AlertCircle } from 'lucide-react';
import { saveTestResult } from '../services/firebase';
import ReactMarkdown from 'react-markdown';
import './Auth.css';
import { generateQuestionPdf } from '../utils/pdfGenerator';
import fallbackData from '../data/fallback_mocks.json';

// Normalize fallback JSON {Q,A,B,C,D,Answer,Explanation} into display format
const normalizeFallback = (questions, count = 10) => {
  const shuffled = [...questions].sort(() => Math.random() - 0.5);
  return shuffled.slice(0, count).map((q, i) => {
    const options = [q.A, q.B, q.C, q.D].filter(Boolean);
    const answerMap = { A: q.A, B: q.B, C: q.C, D: q.D };
    return {
      id: `fb-${i}-${Date.now()}`,
      question: q.Q || q.question || '',
      options,
      correctAnswer: answerMap[q.Answer] || q.correctAnswer || options[0] || '',
      explanation: q.Explanation || q.explanation || ''
    };
  }).filter(q => q.question && q.options.length >= 2);
};

export default function PYQSMockTest() {
  const { t, i18n } = useTranslation();
  const { user, profile } = useAuth();
  const [exam, setExam] = useState(profile?.exam || '');
  const [subject, setSubject] = useState('');
  const [questions, setQuestions] = useState([]);
  const [current, setCurrent] = useState(0);
  const [answers, setAnswers] = useState({});
  const [showResult, setShowResult] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [timer, setTimer] = useState(600);
  const [started, setStarted] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const intervalRef = useRef(null);

  useEffect(() => {
    if (started && timer > 0) {
      intervalRef.current = setInterval(() => setTimer(t => t - 1), 1000);
      return () => clearInterval(intervalRef.current);
    }
    if (timer === 0 && started) handleSubmit();
  }, [started, timer]);

  const startQuiz = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await generatePYQSMockQuestions({ topic: exam + (subject ? ` - ${subject}` : ''), count: 10, language: i18n.language });
      if (result.data && result.data.questions && result.data.questions.length > 0) {
        // Validate questions have real content
        const valid = result.data.questions.filter(q => q.question && q.question.trim().length > 5 && q.options && q.options.length >= 2);
        if (valid.length >= 3) {
          setQuestions(valid.map((q, i) => ({ ...q, id: q.id || `ai-${i}` })));
          if (result.isOffline) setError('⚡ AI limit reached — showing offline questions. Full AI resumes soon!');
          setStarted(true); setTimer(600); setCurrent(0); setAnswers({}); setShowResult(false);
          setLoading(false); return;
        }
      }
      // AI returned bad/empty data — use local fallback
      const fb = normalizeFallback(fallbackData.questions, 10);
      setQuestions(fb);
      setError('⚡ Showing practice questions from our database. AI will load next time!');
      setStarted(true); setTimer(600); setCurrent(0); setAnswers({}); setShowResult(false);
    } catch (err) {
      // Total failure — still show local questions
      const fb = normalizeFallback(fallbackData.questions, 10);
      setQuestions(fb);
      setError('⚡ Showing offline questions. AI will resume shortly!');
      setStarted(true); setTimer(600); setCurrent(0); setAnswers({}); setShowResult(false);
    }
    setLoading(false);
  };

  const fetchMoreQuestions = async () => {
    setLoadingMore(true);
    try {
      const result = await generatePYQSMockQuestions({ topic: exam + (subject ? ` - ${subject}` : ''), count: 10, language: i18n.language });
      if (result.data && result.data.questions) {
        const batchId = Date.now();
        const newQs = result.data.questions.map((q, i) => ({ ...q, id: `batch${batchId}-${i}` }));
        setQuestions(prev => [...prev, ...newQs]);
        setCurrent(current + 1);
      }
    } catch (err) {
      const fb = normalizeFallback(fallbackData.questions, 10);
      setQuestions(prev => [...prev, ...fb]);
      setCurrent(current + 1);
    }
    setLoadingMore(false);
  };

  const selectAnswer = (qId, option) => {
    if (!showResult) setAnswers({ ...answers, [qId]: option });
  };

  const handleSubmit = async () => {
    clearInterval(intervalRef.current);
    try {
      if (user) {
        await saveTestResult(user.uid, {
          exam,
          subject,
          score: getScore(),
          total: questions.length,
          timestamp: new Date().toISOString()
        });
      }
    } catch (err) {
      console.error("Error saving test result:", err);
    }
    setStarted(false);
    setShowResult(true);
  };

  const getScore = () => {
    let correct = 0;
    if (!questions || !Array.isArray(questions)) return 0;
    questions.forEach(q => { if (q && q.id && answers && answers[q.id] === q.correctAnswer) correct++; });
    return correct;
  };

  const subjects = exam ? SUBJECTS[exam] || [] : [];

  if (!started && !showResult) {
    return (
      <main className="page-wrapper" id="pyq-mock-test">
        <div className="page-with-sidebar">
          <header className="page-header animate-fadeInUp">
            <h1><FileText size={28} style={{ verticalAlign: 'middle' }} aria-hidden="true" /> PYQs Mock Test</h1>
            <p>Practice with Previous Year Questions powered by AI.</p>
          </header>
          <div className="content-area">
            <section className="card animate-fadeInUp" style={{ maxWidth: 500, margin: '0 auto' }}>
              <div className="input-group" style={{ marginBottom: 16 }}>
                <label htmlFor="pyq-exam-select">Select Exam</label>
                <select id="pyq-exam-select" value={exam} onChange={e => { setExam(e.target.value); setSubject(''); }}>
                  <option value="">Choose your exam...</option>
                  {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
                </select>
              </div>
              <div className="input-group" style={{ marginBottom: 16 }}>
                <label htmlFor="pyq-subject-select">Select Subject (Optional)</label>
                <select id="pyq-subject-select" value={subject} onChange={e => setSubject(e.target.value)}>
                  <option value="">All Subjects</option>
                  {subjects.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>
              <button className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center' }} onClick={startQuiz} disabled={loading || !exam} aria-busy={loading}>
                {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} aria-hidden="true" /> Generating...</> : <><Sparkles size={18} aria-hidden="true" /> Start 10-Min PYQ Session</>}
              </button>
              {error && <p role="alert" style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
            </section>
          </div>
        </div>
      </main>
    );
  }

  if (showResult) {
    const score = getScore() || 0;
    const total = (questions && Array.isArray(questions)) ? questions.length : 0;
    const percent = total > 0 ? Math.round((score / total) * 100) : 0;

    if (total === 0 && !loading) {
      return (
        <main className="page-wrapper">
          <div className="page-with-sidebar" style={{ textAlign: 'center', padding: '100px 20px' }}>
            <div className="feature-icon red" style={{ margin: '0 auto 24px' }}>
              <AlertCircle size={32} />
            </div>
            <h2>No Questions Available</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Difficulty generating result. Please try taking the test again.</p>
            <button className="btn btn-primary" onClick={() => { setShowResult(false); setStarted(false); setQuestions([]); }}>
              Go Back
            </button>
          </div>
        </main>
      );
    }

    return (
      <main className="page-wrapper" id="pyq-result">
        <div className="page-with-sidebar">
          <header className="animate-fadeInUp" style={{ textAlign: 'center', marginBottom: 40 }}>
            <p className="badge badge-primary">PYQ Session Completed</p>
            <h1 style={{ marginTop: 12 }}>Test Result</h1>
          </header>

          <div className="content-area">
            <div className="grid-2 animate-fadeInUp" style={{ marginBottom: 32 }}>
              <section className="card" style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
                <div style={{ width: 100, height: 100, borderRadius: '50%', border: '8px solid var(--primary-bg)', borderTopColor: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 800, color: 'var(--primary)', flexShrink: 0 }}>
                  {percent}%
                </div>
                <div>
                  <h3 style={{ marginBottom: 4 }}>Score: {score}/{total}</h3>
                  <p style={{ fontSize: '0.9rem' }}>Accuracy: {percent}% - {percent >= 70 ? 'Excellent!' : percent >= 40 ? 'Good progress!' : 'Keep practicing!'}</p>
                </div>
              </section>

              <div className="card" style={{ background: 'var(--bg-accent-green)', border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 16 }}>
                <button className="btn btn-primary" onClick={() => { setShowResult(false); setQuestions([]); setAnswers({}); }}>
                  <RotateCcw size={18} style={{ marginRight: 8 }} /> Retake Test
                </button>
                <button className="btn btn-outline" onClick={() => generateQuestionPdf(`PYQ Test - ${exam}`, subject || 'All Subjects', questions, 'PYQ_Test_Result.pdf')}>
                  <Download size={18} style={{ marginRight: 8 }} /> Download PDF
                </button>
              </div>
            </div>

            {!user && (
              <div className="card animate-fadeInUp" style={{ marginBottom: 32, background: 'var(--primary-bg)', border: '1px solid var(--border-blue)', textAlign: 'center' }}>
                <h3 style={{ color: 'var(--primary)', marginBottom: 8 }}>Track your PYQ progress!</h3>
                <p style={{ marginBottom: 20 }}>Logged-in users get detailed history, performance charts, and AI-powered insights.</p>
                <Link to="/login" className="btn btn-primary" style={{ margin: '0 auto' }}>Login / Create Account</Link>
              </div>
            )}

            <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              <h3 style={{ marginBottom: 4 }}>Question Review</h3>
              {questions?.map((q, i) => {
                const isCorrect = answers[q.id] === q.correctAnswer;
                return (
                  <article key={q.id} className="card animate-fadeInUp" style={{ borderLeft: `4px solid ${isCorrect ? 'var(--accent-green)' : 'var(--accent-red)'}` }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                      <span className={`badge ${isCorrect ? 'badge-green' : 'badge-red'}`}>
                        {isCorrect ? '✅ Correct' : '❌ Incorrect'}
                      </span>
                      <span className="text-muted" style={{ fontSize: '0.8rem' }}>Question {i + 1}</span>
                    </div>
                    <h4 style={{ marginBottom: 16 }}>{q.question}</h4>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginBottom: 16 }}>
                      {q.options?.map((opt, oi) => {
                        const isSelected = answers[q.id] === opt;
                        const isAnswer = q.correctAnswer === opt;
                        return (
                          <div key={oi} style={{ padding: '10px 14px', borderRadius: 8, fontSize: '0.9rem', background: isAnswer ? 'rgba(0,201,167,0.1)' : isSelected && !isAnswer ? 'rgba(239,68,68,0.1)' : 'var(--bg-tertiary)', color: isAnswer ? 'var(--accent-green)' : isSelected && !isAnswer ? 'var(--accent-red)' : 'var(--text-secondary)', fontWeight: isAnswer || isSelected ? 600 : 400, display: 'flex', justifyContent: 'space-between' }}>
                            <span>{opt}</span>
                            {isAnswer && <CheckCircle size={16} />}
                            {isSelected && !isAnswer && <XCircle size={16} />}
                          </div>
                        );
                      })}
                    </div>
                    {q.explanation && (
                      <div style={{ padding: '16px', background: 'var(--primary-bg)', borderRadius: 12, borderTop: '1px solid var(--border-blue)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--primary)', fontWeight: 600, fontSize: '0.85rem' }}>
                          <Sparkles size={14} /> AI EXPLANATION
                        </div>
                        <div style={{ fontSize: '0.9rem', lineHeight: 1.6 }}>
                          <ReactMarkdown>{q.explanation || ''}</ReactMarkdown>
                        </div>
                      </div>
                    )}
                  </article>
                );
              })}
            </div>
          </div>
        </div>
      </main>
    );
  }

  const q = questions[current];

  if (!q && !loadingMore) {
    return (
      <main className="page-wrapper">
        <div className="page-with-sidebar" style={{ textAlign: 'center', padding: '100px 20px' }}>
          <div className="feature-icon red" style={{ margin: '0 auto 24px' }}>
            <AlertCircle size={32} />
          </div>
          <h2>Data Not Available</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>The question could not be loaded. Please restart.</p>
          <button className="btn btn-primary" onClick={() => { setShowResult(false); setStarted(false); setQuestions([]); }}>
            Go Back
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="page-wrapper" id="pyq-mock-test-active">
      <div className="page-with-sidebar">
        <div className="content-area">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Question {current + 1} of ∞ ({questions.length} loaded)</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: timer < 60 ? '#ff6b6b' : 'var(--accent-orange)', fontWeight: 700, fontSize: '1.2rem' }}>
                <Clock size={20} aria-hidden="true" /> {Math.floor(timer / 60)}:{(timer % 60).toString().padStart(2, '0')}
              </div>
              <button className="btn btn-secondary" onClick={handleSubmit}>
                <RotateCcw size={16} aria-hidden="true" /> Finish Early
              </button>
            </div>
          </div>

          <article className="card animate-fadeIn">
            <h2 style={{ marginBottom: 20, fontSize: '1.5rem', lineHeight: '1.4' }}>{q?.question}</h2>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
              {q?.options?.map((opt, i) => {
                const isSelected = answers[q.id] === opt;
                return (
                  <button
                    key={i}
                    onClick={() => selectAnswer(q.id, opt)}
                    className="btn btn-secondary"
                    style={{
                      padding: '16px 20px',
                      background: isSelected ? 'var(--primary-glow)' : 'var(--bg-secondary)',
                      borderColor: isSelected ? 'var(--primary)' : 'var(--border-color)',
                      borderRadius: 12,
                      color: isSelected ? 'var(--primary)' : 'var(--text-secondary)',
                      fontSize: '1rem',
                      textAlign: 'left',
                      whiteSpace: 'normal',
                      height: 'auto',
                      fontWeight: isSelected ? 600 : 400
                    }}
                    aria-pressed={isSelected}>
                    <div style={{ display: 'flex', gap: 12 }}>
                      <span style={{ opacity: 0.5, fontWeight: 700 }}>{String.fromCharCode(65 + i)}.</span>
                      <span>{opt}</span>
                    </div>
                  </button>
                );
              })}
            </div>
          </article>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 32 }}>
            <button className="btn btn-secondary btn-lg" disabled={current === 0} onClick={() => setCurrent(current - 1)}>Previous</button>
            {current < questions.length - 1 ? (
              <button className="btn btn-primary btn-lg" onClick={() => setCurrent(current + 1)}>Next <ArrowRight size={18} aria-hidden="true" /></button>
            ) : (
              <button className="btn btn-primary btn-lg" onClick={fetchMoreQuestions} disabled={loadingMore}>
                {loadingMore ? 'Loading...' : 'Load More PYQs'} <ArrowRight size={18} aria-hidden="true" />
              </button>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
