import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { generateMockQuestions } from '../services/ai';
import { EXAMS, SUBJECTS } from '../utils/constants';
import { Brain, Clock, CheckCircle, XCircle, Sparkles, ArrowRight, RotateCcw, Download } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import './Auth.css';
import { generateQuestionPdf } from '../utils/pdfGenerator';

export default function MockTest() {
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
  const [conversation, setConversation] = useState('');
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
      const result = await generateMockQuestions({ exam, subject, difficulty: 'medium', count: 10, language: i18n.language });
      if (result.data && result.data.questions) {
        const newQs = result.data.questions.map((q, i) => ({ ...q, id: `batch1-${i}` }));
        setQuestions(newQs);
        setConversation(result.conversation || '');
        setStarted(true);
        setTimer(600);
        setCurrent(0);
        setAnswers({});
        setShowResult(false);
      } else {
        setError('Could not generate questions. Check your API key or response format.');
      }
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const fetchMoreQuestions = async () => {
    setLoadingMore(true);
    try {
      const result = await generateMockQuestions({ exam, subject, difficulty: 'medium', count: 10, language: i18n.language });
      if (result.data && result.data.questions) {
        const batchId = Date.now();
        const newQs = result.data.questions.map((q, i) => ({ ...q, id: `batch${batchId}-${i}` }));
        setQuestions(prev => [...prev, ...newQs]);
        setCurrent(current + 1);
      }
    } catch (err) {
      console.error(err);
    }
    setLoadingMore(false);
  };

  const selectAnswer = (qId, option) => {
    if (!showResult) setAnswers({ ...answers, [qId]: option });
  };

  const handleSubmit = () => {
    clearInterval(intervalRef.current);
    setShowResult(true);
    setStarted(false);
  };

  const getScore = () => {
    let correct = 0;
    questions.forEach(q => { if (answers[q.id] === q.correctAnswer) correct++; });
    return correct;
  };

  const subjects = exam ? SUBJECTS[exam] || [] : [];

  if (!started && !showResult) {
    return (
      <main className="page-wrapper" id="mock-test">
        <div className="page-with-sidebar">
          <header className="page-header animate-fadeInUp">
            <h1><Brain size={28} style={{ verticalAlign: 'middle' }} aria-hidden="true" /> {t('mockTest.title')}</h1>
            <p>{t('mockTest.subtitle')}</p>
          </header>
          <div className="content-area">
            <section className="card animate-fadeInUp" style={{ maxWidth: 500, margin: '0 auto' }}>
              <div className="input-group" style={{ marginBottom: 16 }}>
                <label htmlFor="exam-select">{t('studyPlanner.exam')}</label>
                <select id="exam-select" value={exam} onChange={e => { setExam(e.target.value); setSubject(''); }}>
                  <option value="">Select Exam</option>
                  {EXAMS.map(e => <option key={e.id} value={e.id}>{e.icon} {e.name}</option>)}
                </select>
              </div>
              <div className="input-group" style={{ marginBottom: 16 }}>
                <label htmlFor="subject-select">{t('mockTest.selectSubject')}</label>
                <select id="subject-select" value={subject} onChange={e => setSubject(e.target.value)}>
                  <option value="">All Subjects</option>
                  {subjects.map(s => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>
              <button className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center' }} onClick={startQuiz} disabled={loading || !exam} aria-busy={loading}>
                {loading ? <><span className="spinner" style={{ width: 18, height: 18 }} aria-hidden="true" /> Generating questions...</> : <><Sparkles size={18} aria-hidden="true" /> {t('mockTest.start')}</>}
              </button>
              {error && <p role="alert" style={{ color: '#ff6b6b', marginTop: 12, fontSize: '0.85rem' }}>{error}</p>}
            </section>
          </div>
        </div>
      </main>
    );
  }

  if (showResult) {
    const score = getScore();
    return (
      <main className="page-wrapper" id="mock-test-result">
        <div className="page-with-sidebar">
          <div className="content-area">
            <header className="animate-fadeInUp" style={{ textAlign: 'center', marginBottom: 32 }}>
              <h1>{t('mockTest.result')}</h1>
              <div style={{ fontSize: '3rem', fontWeight: 900, color: score / questions.length >= 0.7 ? 'var(--accent-green)' : score / questions.length >= 0.4 ? 'var(--accent-orange)' : '#ff6b6b', margin: '16px 0' }}>
                {score}/{questions.length}
              </div>
              <p>{Math.round((score / questions.length) * 100)}% Accuracy</p>
              <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginTop: 16, flexWrap: 'wrap' }}>
                <button className="btn btn-primary" onClick={() => { setShowResult(false); setQuestions([]); }}>
                  <RotateCcw size={16} aria-hidden="true" /> {t('mockTest.retake')}
                </button>
                <button className="btn btn-outline" onClick={() => {
                  const examName = EXAMS.find(e => e.id === exam)?.name || 'Competitive';
                  generateQuestionPdf(
                    `Mock Test Result: ${examName}`,
                    `Score: ${score}/${questions.length} (${Math.round((score/questions.length)*100)}%) | Subject: ${subject || 'All'}`,
                    questions.map(q => ({
                      ...q,
                      question: `${q.question}${answers[q.id] === q.correctAnswer ? ' (Correct ✓)' : ` (Your Answer: ${answers[q.id] || 'None'} ✗)`}`
                    })),
                    `Mock_Test_${examName}_${Date.now()}.pdf`
                  );
                }}>
                  <Download size={16} aria-hidden="true" /> PDF
                </button>
                <button className="btn btn-outline" onClick={() => {
                   const examName = EXAMS.find(e => e.id === exam)?.name || 'Competitive';
                   let text = `Mock Test Result: ${examName}\nScore: ${score}/${questions.length}\nAccuracy: ${Math.round((score/questions.length)*100)}%\n\n`;
                   questions.forEach((q, i) => {
                     text += `Q${i+1}: ${q.question}\nYour Answer: ${answers[q.id] || 'None'}\nCorrect Answer: ${q.correctAnswer}\nExplanation: ${q.explanation}\n\n`;
                   });
                   const blob = new Blob([text], { type: 'text/plain;charset=utf-8' });
                   const url = URL.createObjectURL(blob);
                   const link = document.createElement('a');
                   link.href = url;
                   link.download = `Mock_Test_${examName}_${Date.now()}.txt`;
                   link.click();
                   URL.revokeObjectURL(url);
                }}>
                  <Download size={16} aria-hidden="true" /> Text
                </button>
              </div>
              {!user && (
                <div className="card" style={{ marginTop: 24, background: 'var(--primary-bg)', border: '1px solid var(--border-blue)', textAlign: 'center' }}>
                  <h4 style={{ color: 'var(--primary)', marginBottom: 8 }}>Want to save these results?</h4>
                  <p style={{ fontSize: '0.9rem', marginBottom: 16 }}>Create a free account to track your performance over time and unlock AI analytics.</p>
                  <Link to="/login" className="btn btn-primary" style={{ margin: '0 auto' }}>Login / Create Account</Link>
                </div>
              )}
            </header>
            {user && (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {questions.map((q, i) => {
                const correctAnswer = q.correctAnswer;
                const isCorrect = answers[q.id] === correctAnswer;
                return (
                  <article key={q.id} className="card animate-fadeInUp" style={{ borderLeft: `4px solid ${isCorrect ? 'var(--accent-green)' : '#ff6b6b'}` }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
                      {isCorrect ? <CheckCircle size={18} color="var(--accent-green)" aria-hidden="true" /> : <XCircle size={18} color="#ff6b6b" aria-hidden="true" />}
                      <strong>Q{i + 1}.</strong> {q.question}
                    </div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginBottom: 8 }}>
                      {q.options?.map((opt, oi) => {
                        const isSelected = answers[q.id] === opt;
                        const isAnswer = correctAnswer === opt;
                        return (
                          <div key={oi} style={{ padding: '6px 12px', borderRadius: 8, fontSize: '0.9rem', background: isAnswer ? 'rgba(0,201,167,0.1)' : isSelected && !isAnswer ? 'rgba(255,64,64,0.1)' : 'transparent', color: isAnswer ? 'var(--accent-green)' : isSelected && !isAnswer ? '#ff6b6b' : 'var(--text-secondary)', fontWeight: isAnswer || isSelected ? 600 : 400 }}>
                            {opt} {isAnswer && '✓'} {isSelected && !isAnswer && '✗'}
                          </div>
                        );
                      })}
                    </div>
                    {q.explanation && <div style={{ padding: '10px 14px', background: 'var(--bg-glass)', borderRadius: 8, fontSize: '0.85rem', color: 'var(--text-secondary)' }}>💡 {q.explanation}</div>}
                  </article>
                );
              })}
            </div>
            )}
          </div>
        </div>
      </main>
    );
  }

  const q = questions[current];
  return (
    <main className="page-wrapper" id="mock-test-active">
      <div className="page-with-sidebar">
        <div className="content-area">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>{t('mockTest.question')} {current + 1} {t('mockTest.of')} ∞ ({questions.length} Loaded)</span>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: timer < 60 ? '#ff6b6b' : 'var(--accent-orange)', fontWeight: 700, fontSize: '1.2rem' }}>
                <Clock size={20} aria-hidden="true" /> {Math.floor(timer / 60)}:{(timer % 60).toString().padStart(2, '0')}
              </div>
              <button className="btn btn-secondary" onClick={handleSubmit}>
                <RotateCcw size={16} aria-hidden="true" /> Finish Early
              </button>
            </div>
          </div>

          {timer === 0 && (
            <div className="card animate-fadeIn" style={{ marginBottom: 20, borderLeft: '4px solid #ff6b6b' }}>
              <h3 style={{ color: '#ff6b6b', display: 'flex', alignItems: 'center', gap: 8 }}>
                <XCircle size={20} /> Time's Up!
              </h3>
              <p style={{ marginTop: 8 }}>Your 10 minutes are over. Submitting your test automatically.</p>
            </div>
          )}

          {/* Removed conversation block per user request */}
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
              <button className="btn btn-primary btn-lg" onClick={() => setCurrent(current + 1)}>{t('mockTest.next')} <ArrowRight size={18} aria-hidden="true" /></button>
            ) : (
              <button className="btn btn-primary btn-lg" onClick={fetchMoreQuestions} disabled={loadingMore}>
                {loadingMore ? 'Loading More...' : 'Load Next Questions'} <ArrowRight size={18} aria-hidden="true" />
              </button>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}
