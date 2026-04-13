import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { Link } from 'react-router-dom';
import { generatePYQSMockQuestions } from '../services/ai';
import { 
  Brain, Clock, CheckCircle, XCircle, Sparkles, 
  ArrowRight, RotateCcw, AlertCircle, ChevronRight,
  TrendingUp, FileText, Trophy, Play, Target
} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import { EXAMS, SUBJECTS } from '../utils/constants';

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
  const [timer, setTimer] = useState(600); // 10 minutes
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
    if (!exam) {
      setError('Please select an exam to start the PYQ test.');
      return;
    }
    const topic = exam + (subject ? ` - ${subject}` : '');
    setLoading(true);
    setError('');
    try {
      const result = await generatePYQSMockQuestions({ topic, count: 10, language: i18n.language });
      if (result.data && result.data.questions) {
        const newQs = result.data.questions.map((q, i) => ({ ...q, id: `batch1-${i}` }));
        setQuestions(newQs);
        setStarted(true);
        setTimer(600);
        setCurrent(0);
        setAnswers({});
        setShowResult(false);
      } else {
        setError('Could not generate questions. Please try again.');
      }
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const fetchMoreQuestions = async () => {
    setLoadingMore(true);
    try {
      const topicString = exam + (subject ? ` - ${subject}` : '');
      const result = await generatePYQSMockQuestions({ topic: topicString, count: 10, language: i18n.language });
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
    if (!questions || !Array.isArray(questions)) return 0;
    questions.forEach(q => { if (q && q.id && answers && answers[q.id] === q.correctAnswer) correct++; });
    return correct;
  };

  const getAttempted = () => {
    return Object.keys(answers).length;
  };

  const subjectsList = exam ? SUBJECTS[exam] || [] : [];

  // ── SETUP SCREEN ──
  if (!started && !showResult) {
    const trialUsed = localStorage.getItem('sarkari_trial_used') === 'true';
    if (trialUsed && !user) {
      return (
        <main className="page-wrapper">
          <div className="page-with-sidebar">
            <div className="content-area">
              <section className="card-trial-barrier animate-fadeInUp">
                <div className="feature-icon red" style={{ margin: '0 auto', width: 64, height: 64 }}>
                  <Target size={32} fill="currentColor" />
                </div>
                <h2 style={{ marginBottom: 12 }}>Trial Completed</h2>
                <p style={{ color: 'var(--text-secondary)', marginBottom: 24, lineHeight: 1.6 }}>
                  You have experienced our PYQ Mock Test. To access more papers and track results, please login.
                </p>
                <Link to="/login" className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center' }}>
                  Login to Continue
                </Link>
              </section>
            </div>
          </div>
        </main>
      );
    }

    return (
      <main className="page-wrapper">
        <div className="page-with-sidebar">
          <header className="page-header animate-fadeInUp">
            <p className="badge badge-saffron" style={{ marginBottom: 8 }}>PREMIUM PRACTICE</p>
            <h1><Brain size={28} className="text-blue" style={{ verticalAlign: 'middle', marginRight: 12 }} /> PYQs Mock Test</h1>
            <p>Master your exam with a focused 10-minute challenge using real past year questions.</p>
          </header>

          <div className="content-area">
            <div className="grid-2 animate-fadeInUp">
              <section className="card">
                <h3 style={{ marginBottom: 20 }}>Configure Your Test</h3>
                <div className="input-group" style={{ marginBottom: 16 }}>
                  <label>Select Your Exam</label>
                  <select value={exam} onChange={e => { setExam(e.target.value); setSubject(''); }}>
                    <option value="">Choose an exam...</option>
                    {EXAMS.map(e => <option key={e.id} value={e.id}>{e.name}</option>)}
                  </select>
                </div>
                <div className="input-group" style={{ marginBottom: 24 }}>
                  <label>Target Subject (Optional)</label>
                  <select value={subject} onChange={e => setSubject(e.target.value)}>
                    <option value="">All Subjects</option>
                    {subjectsList.map(s => <option key={s} value={s}>{s}</option>)}
                  </select>
                </div>
                <button className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center' }} onClick={startQuiz} disabled={loading || !exam}>
                  {loading ? <><span className="spinner" style={{ width: 18, height: 18, marginRight: 8 }} /> Generating...</> : <><Play size={18} fill="white" /> Start 10-Min Session</>}
                </button>
                {error && <div className="alert alert-error" style={{ marginTop: 16 }}>{error}</div>}
              </section>

              <section className="card" style={{ background: 'var(--primary-bg)', border: '1px solid var(--border-blue)' }}>
                <h3 style={{ marginBottom: 16, color: 'var(--primary)' }}>How it works</h3>
                <ul style={{ display: 'flex', flexDirection: 'column', gap: 12, listStyle: 'none' }}>
                  <li style={{ display: 'flex', gap: 12 }}>
                    <div className="feature-icon indigo" style={{ width: 32, height: 32, fontSize: '1rem' }}><Clock size={16} /></div>
                    <div>
                      <strong style={{ display: 'block', fontSize: '0.9rem' }}>10-Minute Sprint</strong>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Short, high-intensity focus sessions to build mental stamina.</span>
                    </div>
                  </li>
                  <li style={{ display: 'flex', gap: 12 }}>
                    <div className="feature-icon saffron" style={{ width: 32, height: 32, fontSize: '1rem' }}><FileText size={16} /></div>
                    <div>
                      <strong style={{ display: 'block', fontSize: '0.9rem' }}>Real PYQ Patterns</strong>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Questions modeled after historic exam data and trending topics.</span>
                    </div>
                  </li>
                  <li style={{ display: 'flex', gap: 12 }}>
                    <div className="feature-icon green" style={{ width: 32, height: 32, fontSize: '1rem' }}><Sparkles size={16} /></div>
                    <div>
                      <strong style={{ display: 'block', fontSize: '0.9rem' }}>AI Explanations</strong>
                      <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Get instant, high-quality reasoning for every correct and incorrect answer.</span>
                    </div>
                  </li>
                </ul>
              </section>
            </div>
          </div>
        </div>
      </main>
    );
  }

  // ── RESULT SCREEN ──
  if (showResult) {
    const score = getScore() || 0;
    const total = (questions && Array.isArray(questions)) ? questions.length : 0;
    const percent = total > 0 ? Math.round((score/total)*100) : 0;
    
    if (total === 0) {
      return (
        <main className="page-wrapper">
          <div className="page-with-sidebar" style={{ textAlign: 'center', padding: '100px 20px' }}>
            <div className="feature-icon red" style={{ margin: '0 auto 24px' }}>
              <AlertCircle size={32} />
            </div>
            <h2>Result Unavailable</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>Difficulty retrieving results. Please try again.</p>
            <button className="btn btn-primary" onClick={() => { setShowResult(false); setQuestions([]); setAnswers({}); }}>
              Go Back
            </button>
          </div>
        </main>
      );
    }
    
    return (
      <main className="page-wrapper">
        <div className="page-with-sidebar">
          <header className="animate-fadeInUp" style={{ textAlign: 'center', marginBottom: 40 }}>
            <p className="badge badge-primary">Test Completed</p>
            <h1 style={{ marginTop: 12 }}>Mock Test Results</h1>
          </header>

          <div className="content-area">
            <div className="grid-2 animate-fadeInUp" style={{ marginBottom: 32 }}>
              <section className="card" style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
                <div style={{ width: 100, height: 100, borderRadius: '50%', border: '8px solid var(--primary-bg)', borderTopColor: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 800, color: 'var(--primary)', flexShrink: 0 }}>
                  {percent}%
                </div>
                <div>
                  <h3 style={{ marginBottom: 4 }}>You Scored {score}/{total}</h3>
                  <p style={{ fontSize: '0.9rem' }}>You attempted {total} questions. Your accuracy is {percent}% which is {percent >= 70 ? 'Excellent!' : percent >= 40 ? 'Good progress!' : 'Keep practicing!'}</p>
                </div>
              </section>

              <div className="card" style={{ background: 'var(--bg-accent-green)', border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                 <button className="btn btn-primary" onClick={() => { setShowResult(false); setQuestions([]); setAnswers({}); }}>
                  <RotateCcw size={18} style={{ marginRight: 8 }} /> Try Another PYQ Session
                </button>
              </div>
            </div>

            {!user && (
              <div className="card animate-fadeInUp" style={{ marginBottom: 32, background: 'var(--primary-bg)', border: '1px solid var(--border-blue)', textAlign: 'center' }}>
                <h3 style={{ color: 'var(--primary)', marginBottom: 8 }}>Want to track your PYQ progress?</h3>
                <p style={{ marginBottom: 20 }}>Logged-in users get detailed history, performance charts, and AI-powered preparation insights.</p>
                <Link to="/login" className="btn btn-primary" style={{ margin: '0 auto' }}>Login / Create Account</Link>
              </div>
            )}

            {user && (
              <>
                <h3 style={{ marginBottom: 20 }}>Question Review</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
              {questions?.map((q, i) => {
                const isCorrect = answers[q.id] === q.correctAnswer;
                return (
                  <article key={q.id} className="card animate-fadeInUp" style={{ borderLeft: `4px solid ${isCorrect ? 'var(--accent-green)' : 'var(--accent-red)'}` }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                      <span className={`badge ${isCorrect ? 'badge-green' : 'badge-red'}`}>
                        {isCorrect ? 'Correct' : 'Incorrect'}
                      </span>
                      <span className="text-muted" style={{ fontSize: '0.8rem' }}>Question {i + 1}</span>
                    </div>
                    <h4 style={{ marginBottom: 16 }}>{q.question}</h4>
                    <div className="grid-2" style={{ gap: 12, marginBottom: 16 }}>
                      {q.options?.map((opt, oi) => {
                        const isSelected = answers[q.id] === opt;
                        const isAnswer = q.correctAnswer === opt;
                        return (
                          <div key={oi} className="alert" style={{ 
                            margin: 0, 
                            padding: '10px 14px',
                            background: isAnswer ? 'var(--bg-accent-green)' : isSelected ? 'var(--bg-accent-red)' : 'var(--bg-tertiary)',
                            borderColor: isAnswer ? 'var(--accent-green)' : isSelected ? 'var(--accent-red)' : 'transparent',
                            color: isAnswer ? 'var(--accent-green)' : isSelected ? 'var(--accent-red)' : 'var(--text-secondary)',
                            display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                            borderLeftWidth: '1.5px'
                          }}>
                            <span style={{ fontWeight: isAnswer || isSelected ? 600 : 400 }}>{opt}</span>
                            {isAnswer && <CheckCircle size={14} />}
                            {isSelected && !isAnswer && <XCircle size={14} />}
                          </div>
                        );
                      })}
                    </div>
                    {q.explanation && (
                      <div style={{ padding: '16px', background: 'var(--primary-bg)', borderRadius: 12, borderTop: '1px solid var(--border-blue)' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8, color: 'var(--primary)', fontWeight: 600, fontSize: '0.85rem' }}>
                          <Sparkles size={14} /> AI EXPLANATION
                        </div>
                        <div className="text-answer-card" style={{ fontSize: '0.9rem' }}>
                          <ReactMarkdown>{q.explanation || ''}</ReactMarkdown>
                        </div>
                      </div>
                    )}
                  </article>
                );
              })}
            </div>
            </>
            )}
          </div>
        </div>
      </main>
    );
  }

  // ── ACTIVE TEST SCREEN ──
  const q = questions[current];

  if (!q && !loadingMore) {
    return (
      <main className="page-wrapper">
        <div className="page-with-sidebar" style={{ textAlign: 'center', padding: '100px 20px' }}>
          <div className="feature-icon red" style={{ margin: '0 auto 24px' }}>
            <AlertCircle size={32} />
          </div>
          <h2>Data Not Available</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: 24 }}>The question could not be loaded or the test data is empty. Please restart.</p>
          <button className="btn btn-primary" onClick={() => { setShowResult(false); setStarted(false); setQuestions([]); }}>
            Go Back
          </button>
        </div>
      </main>
    );
  }

  const progress = Math.round(((current + 1) / (questions.length || 1)) * 100);

  return (
    <main className="page-wrapper">
      <div className="page-with-sidebar">
        
        <div className="content-area">
          {/* Test Header */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, padding: '0 4px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div className={`badge ${timer < 60 ? 'badge-red' : 'badge-orange'}`} style={{ padding: '6px 14px', fontSize: '1rem', display: 'flex', gap: 8 }}>
                <Clock size={16} /> {Math.floor(timer / 60)}:{(timer % 60).toString().padStart(2, '0')}
              </div>
            </div>
            <button className="btn btn-outline btn-sm" onClick={handleSubmit}>Finish Early</button>
          </div>

          {/* Progress Bar */}
          <div className="progress-bar-wrap" style={{ height: 4, marginBottom: 32 }}>
            <div className="progress-bar-fill" style={{ width: `${progress}%` }} />
          </div>
          
          {timer === 0 && (
            <div className="alert alert-error animate-fadeIn" style={{ marginBottom: 24 }}>
               <AlertCircle size={18} style={{ marginRight: 8, verticalAlign: 'middle' }} /> Time's Up! Submitting your test automatically.
            </div>
          )}

          {/* Question Card */}
          <article className="card animate-fadeIn" style={{ minHeight: 400, display: 'flex', flexDirection: 'column' }}>
            <div style={{ marginBottom: 24 }}>
               <span className="text-muted" style={{ fontSize: '0.85rem', fontWeight: 600 }}>QUESTION {current + 1} OF {questions.length}</span>
               <h2 style={{ marginTop: 12, lineHeight: 1.4, fontSize: '1.75rem' }}>{q?.question}</h2>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 32 }}>
              {q?.options?.map((opt, i) => {
                const isSelected = answers[q.id] === opt;
                return (
                  <button 
                    key={i} 
                    onClick={() => selectAnswer(q.id, opt)}
                    className={`btn ${isSelected ? 'btn-primary' : 'btn-secondary'}`}
                    style={{ 
                      justifyContent: 'flex-start', 
                      padding: '16px 20px', 
                      textAlign: 'left', 
                      whiteSpace: 'normal',
                      height: 'auto',
                      fontSize: '1rem',
                      borderWidth: isSelected ? '0' : '1.5px'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12, width: '100%' }}>
                      <div style={{ 
                        width: 28, height: 28, 
                        borderRadius: '50%', 
                        background: isSelected ? 'rgba(255,255,255,0.2)' : 'var(--bg-tertiary)', 
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '0.8rem', fontWeight: 700, flexShrink: 0
                      }}>
                        {String.fromCharCode(65 + i)}
                      </div>
                      {opt}
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Nav Buttons */}
            <div style={{ marginTop: 'auto', display: 'flex', justifyContent: 'space-between', borderTop: '1px solid var(--border-light)', paddingTop: 24 }}>
              <button className="btn btn-ghost" disabled={current === 0} onClick={() => setCurrent(current - 1)}>
                Previous
              </button>
              <div style={{ display: 'flex', gap: 12 }}>
                {current < questions.length - 1 ? (
                  <button className="btn btn-primary" onClick={() => setCurrent(current + 1)}>
                    Next Question <ChevronRight size={18} />
                  </button>
                ) : (
                  <button className="btn btn-cta" onClick={fetchMoreQuestions} disabled={loadingMore}>
                    {loadingMore ? 'Loading...' : 'Load More PYQs'} <Sparkles size={16} fill="white" />
                  </button>
                )}
              </div>
            </div>
          </article>
        </div>
      </div>
    </main>
  );
}
