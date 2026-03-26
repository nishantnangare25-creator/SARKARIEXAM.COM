import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ArrowLeft, BookOpen, CheckCircle, XCircle, Loader2, Award, FileText } from 'lucide-react';
import { convertPdfToQuiz, generatePYQSMockQuestions } from '../services/ai';
import { saveTestResult } from '../services/firebase';
import { useAuth } from '../contexts/AuthContext';
import ReactMarkdown from 'react-markdown';

export default function PYQPractice() {
  const { i18n } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const { pdfInfo, extractedText } = location.state || {};

  const [questions, setQuestions] = useState([]);
  const [currentIdx, setCurrentIdx] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState({});
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    let isMounted = true;
    
    const fetchQuestions = async () => {
      setIsLoading(true);
      try {
        let result;
        if (extractedText) {
          // It's a custom uploaded PDF text
          result = await convertPdfToQuiz({ text: extractedText, language: i18n?.language || 'en' });
        } else if (pdfInfo) {
          // It's an existing PDF card from the library
          result = await generatePYQSMockQuestions({ 
            topic: pdfInfo.title,
            year: pdfInfo.year,
            count: 10, 
            language: i18n?.language || 'en' 
          });
        }
        
        if (isMounted && result?.data?.questions) {
          setQuestions(result.data.questions);
        }
      } catch (e) {
        console.error("Failed to parse interactive quiz:", e);
      } finally {
        if (isMounted) setIsLoading(false);
      }
    };
    
    if (!questions.length) {
      fetchQuestions();
    }
    
    return () => { isMounted = false; };
  }, [extractedText, pdfInfo, i18n?.language]);

  const handleSelectOption = (idx, opt) => {
    if (isSubmitted) return;
    setSelectedAnswers(prev => ({ ...prev, [idx]: opt }));
  };

  const calculateScore = () => {
    let correct = 0;
    questions.forEach((q, i) => {
      if (selectedAnswers[i] && selectedAnswers[i].trim().toLowerCase() === q.correctAnswer.trim().toLowerCase()) {
        correct++;
      }
    });
    return { correct, total: questions.length };
  };

  const handleSubmit = () => {
    setIsSubmitted(true);
    const score = calculateScore();
    if (user) {
      saveTestResult(user.uid, {
        type: 'PYQ Practice',
        subject: pdfInfo?.title || 'Custom Uploaded PYQ',
        score: score.correct,
        total: score.total,
        date: new Date().toISOString().split('T')[0]
      }).catch(console.error);
    }
  };

  if (isLoading) {
    return (
      <main className="page-wrapper">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '60vh', gap: 24 }}>
          <Loader2 size={64} className="text-primary animate-spin" />
          <h2>Analyzing Paper & Extracting Questions...</h2>
          <p style={{ color: 'var(--text-secondary)' }}>Our AI is dynamically building your interactive mock test. This might take a moment.</p>
        </div>
      </main>
    );
  }

  if (!questions.length) {
    return (
      <main className="page-wrapper">
        <div style={{ textAlign: 'center', padding: '60px 20px' }}>
          <FileText size={48} className="text-muted" style={{ marginBottom: 16 }} />
          <h2>Failed to generate practice test</h2>
          <p>We couldn't extract enough viable questions from this document.</p>
          <button onClick={() => navigate(-1)} className="btn btn-primary" style={{ marginTop: 20 }}>Return to Library</button>
        </div>
      </main>
    );
  }

  const currentQ = questions[currentIdx];
  const isLastQ = currentIdx === questions.length - 1;

  return (
    <main className="page-wrapper auto-scroll">
      <div className="page-with-sidebar">
        <header className="page-header animate-fadeInUp">
          <button onClick={() => navigate(-1)} className="btn btn-icon" style={{ marginBottom: 16 }}>
            <ArrowLeft size={20} /> <span style={{ marginLeft: 8 }}>Back to Library</span>
          </button>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <p className="badge badge-primary" style={{ marginBottom: 8 }}>INTERACTIVE PRACTICE</p>
              <h1><BookOpen size={28} className="text-primary" style={{ verticalAlign: 'middle', marginRight: 12 }} /> PYQ Practice Mode</h1>
              <p>Attempting: <strong>{pdfInfo?.title || 'Custom Uploaded PYQ Paper'}</strong></p>
            </div>
            {isSubmitted && (
              <div className="card" style={{ background: 'rgba(var(--primary-rgb), 0.1)', borderColor: 'var(--primary)', padding: '12px 24px', textAlign: 'center' }}>
                <Award size={32} className="text-primary" style={{ marginBottom: 4 }} />
                <h2 style={{ color: 'var(--primary)', margin: 0 }}>{calculateScore().correct} / {calculateScore().total}</h2>
                <small style={{ color: 'var(--text-secondary)' }}>Final Score</small>
              </div>
            )}
          </div>
        </header>

        <section className="card animate-fadeInUp" style={{ marginBottom: 24, padding: '32px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-light)', paddingBottom: 16, marginBottom: 24 }}>
            <h3 style={{ margin: 0 }}>Question {currentIdx + 1} of {questions.length}</h3>
            {isSubmitted && (
              <span className={`badge ${selectedAnswers[currentIdx]?.trim().toLowerCase() === currentQ.correctAnswer.trim().toLowerCase() ? 'badge-green' : 'badge-red'}`}>
                {selectedAnswers[currentIdx]?.trim().toLowerCase() === currentQ.correctAnswer.trim().toLowerCase() ? 'Correct' : 'Incorrect'}
              </span>
            )}
          </div>
          
          <h2 style={{ fontSize: '1.3rem', lineHeight: 1.6, marginBottom: 32 }}>{currentQ.question}</h2>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 32 }}>
            {currentQ.options.map((opt, i) => {
              const isSelected = selectedAnswers[currentIdx] === opt;
              const holdsCorrectAnswer = isSubmitted && opt.trim().toLowerCase() === currentQ.correctAnswer.trim().toLowerCase();
              const holdsWrongSelection = isSubmitted && isSelected && !holdsCorrectAnswer;

              let btnStyle = { textAlign: 'left', padding: '16px 20px', fontSize: '1.05rem', justifyContent: 'flex-start', border: '1px solid var(--border-light)' };
              
              if (holdsCorrectAnswer) {
                btnStyle.background = 'rgba(16, 185, 129, 0.1)';
                btnStyle.borderColor = '#10B981';
                btnStyle.color = '#10B981';
              } else if (holdsWrongSelection) {
                btnStyle.background = 'rgba(239, 68, 68, 0.1)';
                btnStyle.borderColor = '#EF4444';
                btnStyle.color = '#EF4444';
              } else if (isSelected && !isSubmitted) {
                btnStyle.background = 'rgba(var(--primary-rgb), 0.1)';
                btnStyle.borderColor = 'var(--primary)';
              }

              return (
                <button 
                  key={i} 
                  className="btn btn-outline" 
                  style={btnStyle}
                  onClick={() => handleSelectOption(currentIdx, opt)}
                  disabled={isSubmitted}
                >
                  <span style={{ fontWeight: 'bold', marginRight: 12 }}>{String.fromCharCode(65 + i)}.</span>
                  {opt}
                  {holdsCorrectAnswer && <CheckCircle size={20} style={{ marginLeft: 'auto' }} />}
                  {holdsWrongSelection && <XCircle size={20} style={{ marginLeft: 'auto' }} />}
                </button>
              );
            })}
          </div>

          {isSubmitted && currentQ.explanation && (
            <div style={{ background: 'var(--bg-tertiary)', padding: 24, borderRadius: 12, marginTop: 24, borderLeft: '4px solid var(--primary)' }}>
              <h4 style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 8 }}>
                <BookOpen size={18} className="text-primary"/> AI Explanation:
              </h4>
              <div style={{ lineHeight: 1.6, color: 'var(--text-primary)', margin: 0 }}>
                <ReactMarkdown>{currentQ.explanation}</ReactMarkdown>
              </div>
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 40, borderTop: '1px solid var(--border-light)', paddingTop: 24 }}>
            <button 
              className="btn btn-outline" 
              disabled={currentIdx === 0}
              onClick={() => setCurrentIdx(prev => prev - 1)}
            >
              Previous Question
            </button>

            {!isSubmitted ? (
              <button 
                className="btn btn-primary"
                onClick={isLastQ ? handleSubmit : () => setCurrentIdx(prev => prev + 1)}
                disabled={!selectedAnswers[currentIdx] && !isLastQ} // Force selection to proceed, except on last Q where they can submit
              >
                {isLastQ ? 'Submit Test' : 'Next Question'}
              </button>
            ) : (
              <button 
                className="btn btn-primary"
                onClick={() => setCurrentIdx(prev => prev + 1)}
                disabled={isLastQ}
              >
                Next Explanation
              </button>
            )}
          </div>
        </section>

      </div>
    </main>
  );
}
