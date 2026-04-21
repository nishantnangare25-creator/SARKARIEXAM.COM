import React, { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, Link } from 'react-router-dom';
import { 
  Bot, Sparkles, Download, Send, Zap,
  Info, Trash2, Languages, Loader2, FileText,
  ArrowLeft
} from 'lucide-react';
import { generateTutorLesson } from '../services/ai';
import { useAuth } from '../contexts/AuthContext';
import { generateNotesPdf } from '../utils/pdfGenerator';
import './InteractiveTutor.css';

// Safe markdown renderer — if ReactMarkdown fails, falls back to plain text
// Safe markdown renderer — if ReactMarkdown fails, falls back to plain text
function SafeMarkdown({ children }) {
  const [MarkdownComp, setMarkdownComp] = React.useState(null);
  const [failed, setFailed] = React.useState(false);

  React.useEffect(() => {
    let isMounted = true;
    import('react-markdown')
      .then(mod => {
        if (isMounted) setMarkdownComp(() => mod.default);
      })
      .catch((err) => {
        console.error('SafeMarkdown import failed:', err);
        if (isMounted) setFailed(true);
      });
    return () => { isMounted = false; };
  }, []);

  const fallback = <div style={{ whiteSpace: 'pre-wrap', lineHeight: 1.7, wordBreak: 'break-word' }}>{children || ''}</div>;

  if (failed || !children) return fallback;
  if (!MarkdownComp) return fallback;

  try {
    return <MarkdownComp>{children}</MarkdownComp>;
  } catch (err) {
    return fallback;
  }
}

// Local ErrorBoundary specifically for this page
class TutorErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, errorMsg: '' };
  }
  static getDerivedStateFromError(error) {
    return { hasError: true, errorMsg: error?.message || 'Unknown error' };
  }
  render() {
    if (this.state.hasError) {
      return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '80vh', gap: 16, padding: 24, textAlign: 'center' }}>
          <Bot size={48} style={{ color: '#1A56DB', opacity: 0.4 }} />
          <h2 style={{ color: '#111827' }}>Riya AI — Loading Error</h2>
          <p style={{ color: '#6B7280', maxWidth: 400 }}>Something went wrong loading the tutor. Please tap below to retry.</p>
          <p style={{ fontSize: '0.75rem', color: '#9CA3AF', maxWidth: 400 }}>{this.state.errorMsg}</p>
          <button
            onClick={() => { this.setState({ hasError: false }); window.location.reload(); }}
            style={{ padding: '10px 24px', background: '#1A56DB', color: 'white', border: 'none', borderRadius: 8, cursor: 'pointer', fontWeight: 600 }}
          >Retry</button>
        </div>
      );
    }
    return this.props.children;
  }
}

export default function InteractiveTutor() {
  const { t, i18n } = useTranslation();
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const trialUsed = localStorage.getItem('sarkari_trial_used') === 'true';
  const [messages, setMessages] = useState([]);
  const [currentInput, setCurrentInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const scrollRef = useRef(null);

  // Auto-scroll to bottom
  useEffect(() => {
    if (scrollRef.current) {
      try {
        scrollRef.current.scrollTo({
          top: scrollRef.current.scrollHeight,
          behavior: 'smooth'
        });
      } catch (e) {
        // Fallback for older browsers/webviews that don't support scrollTo options
        scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
      }
    }
  }, [messages, loading]);

  if (authLoading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100vh', background: '#FFFFFF' }}>
        <Loader2 size={32} className="text-blue animate-spin" />
      </div>
    );
  }

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!currentInput.trim()) return;

    const userMsg = currentInput.trim();
    setCurrentInput('');
    setError(null);
    
    const newHistory = [...messages, { role: 'user', content: userMsg }];
    setMessages(newHistory);
    setLoading(true);
    
    try {
      const aiResponse = await generateTutorLesson({
        history: newHistory,
        language: i18n.language
      });
      
      const finalHistory = [...newHistory, { role: 'assistant', content: aiResponse }];
      setMessages(finalHistory);
      
      // Mark trial used after first response
      if (!user) {
        localStorage.setItem('sarkari_trial_used', 'true');
      }
    } catch (err) {
      setError(err.message || t('common.error'));
    } finally {
      setLoading(false);
    }
  };

  const downloadChatPdf = () => {
    try {
      if (messages.length === 0) return;
      
      const sessionDate = new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
      const filename = `Riya_Session_${sessionDate.replace(' ', '_')}.pdf`;
      
      let content = "# Riya AI Tutor Session Log\n\n";
      messages.forEach((msg) => {
        const roleName = msg.role === 'user' ? 'You' : 'Riya';
        content += `### ${roleName}\n${msg.content}\n\n`;
      });
      
      generateNotesPdf(
        'Riya AI Tutor Session',
        content,
        filename
      );
    } catch (err) {
      console.error("PDF Download Error:", err);
      setError("Unable to generate PDF. Please try the Text download.");
    }
  };

  const downloadChatText = async () => {
    try {
      if (messages.length === 0) return;
      
      const sessionDate = new Date().toLocaleDateString('en-IN', { day: '2-digit', month: 'short' });
      const filename = `Riya_Session_${sessionDate.replace(' ', '_')}.md`;
      
      let content = "# Riya AI Tutor Session Log\n\n";
      messages.forEach((msg) => {
        const roleName = msg.role === 'user' ? 'You' : 'Riya';
        content += `### ${roleName}\n${msg.content}\n\n`;
      });

      const mimeType = 'text/markdown;charset=utf-8';

      // Mobile Sharing Support (Best for avoids file number/naming issues)
      if (navigator.share && navigator.canShare) {
        try {
          const file = new File([content], filename, { type: mimeType });
          if (navigator.canShare({ files: [file] })) {
            await navigator.share({
              title: `Riya AI Session (${sessionDate})`,
              text: 'My study session log with Riya AI Tutor',
              files: [file]
            });
            return;
          }
        } catch (err) {
          console.warn('Navigator share failed, using fallback:', err);
        }
      }

      // Fallback Download (Traditional)
      const blob = new Blob([content], { type: mimeType });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      setTimeout(() => {
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
      }, 100);
    } catch (err) {
      console.error("Text Download Error:", err);
      setError(t('tutor.error.downloadFailed'));
    }
  };

  const clearChat = () => {
    if (window.confirm(t('tutor.clearChatConfirm'))) {
      setMessages([]);
    }
  };

  return (
    <TutorErrorBoundary>
    <main className="page-wrapper tutor-page-wrapper">
      {/* Immersive Layout — removed page-with-sidebar to fix margin gap */}
      <div className="immersive-tutor-layout riya-tutor-container">
        
        {/* Modern Compact Header */}
        <header className="riya-header">
          <div className="riya-brand-group">
            <div className="riya-brand-title">
              <Bot size={22} className="text-blue" />
              <span>Riya AI</span>
            </div>
            <div className="riya-badge-row">
              <span className="riya-status-badge">
                <span className="dot" style={{ width: 6, height: 6, background: 'var(--accent-green)', borderRadius: '50%' }} />
                {t('common.online') === 'common.online' ? 'Online' : t('common.online')}
              </span>
              <span className="riya-lang-badge">
                <Languages size={10} />
                {(i18n.language || 'en').toUpperCase()}
              </span>
            </div>
          </div>

          <div className="riya-header-actions">
            <button 
              className="btn-riya-action" 
              onClick={downloadChatPdf} 
              disabled={messages.length === 0}
              title={t('tutor.actions.downloadPdf') === 'tutor.actions.downloadPdf' ? 'Download PDF' : t('tutor.actions.downloadPdf')}
            >
              <Download size={15} />
            </button>
            <button 
              className="btn-riya-action" 
              onClick={downloadChatText} 
              disabled={messages.length === 0}
              title={t('tutor.actions.downloadText') === 'tutor.actions.downloadText' ? 'Download Text' : t('tutor.actions.downloadText')}
            >
              <FileText size={15} />
            </button>
            <button 
              className="btn-riya-action danger" 
              onClick={clearChat} 
              disabled={messages.length === 0}
              title={t('tutor.actions.clearChat') === 'tutor.actions.clearChat' ? 'Clear Chat' : t('tutor.actions.clearChat')}
            >
              <Trash2 size={15} />
            </button>
          </div>
        </header>

        {/* Chat Scrolling Area */}
        <div className="riya-chat-area" ref={scrollRef}>
          {trialUsed && !user ? (
            <div style={{ margin: 'auto', textAlign: 'center', maxWidth: 450 }} className="animate-fadeIn">
               <section className="card-trial-barrier">
                <div className="feature-icon indigo" style={{ margin: '0 auto', width: 64, height: 64 }}>
                  <Zap size={32} fill="currentColor" />
                </div>
                <h2 style={{ marginBottom: 12 }}>Trial Completed</h2>
                <p style={{ color: 'var(--text-secondary)', marginBottom: 24, lineHeight: 1.6 }}>
                  You have completed your free trial with Riya AI. To continue this session and get 24/7 tutoring, please login.
                </p>
                <Link to="/login" className="btn btn-primary btn-lg" style={{ width: '100%', justifyContent: 'center', textDecoration: 'none' }}>
                  Login to Continue
                </Link>
              </section>
            </div>
          ) : messages.length === 0 ? (
            <div style={{ 
              margin: 'auto', 
              textAlign: 'center', 
              maxWidth: 460, 
              padding: '24px 16px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 20
            }} className="animate-fadeIn">
              {/* Welcome Header — hardcoded, no translation keys */}
              <div style={{
                width: 64,
                height: 64,
                background: 'var(--primary-bg)',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                border: '2px solid var(--border-blue)'
              }}>
                <Bot size={30} style={{ color: 'var(--primary)' }} />
              </div>
              <div>
                <h3 style={{ color: 'var(--text-primary)', marginBottom: 8, fontSize: '1.1rem' }}>
                  Hi! I'm Riya, your AI Tutor 👋
                </h3>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem', lineHeight: 1.6 }}>
                  Ask me anything about your exam preparation. I can explain concepts, solve doubts, and help you study smarter!
                </p>
              </div>
              <div className="tutor-suggestions-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, width: '100%' }}>
                {[
                  "Explain UPSC Syllabus",
                  "What are Ashok's Edicts?",
                  "2024 Current Affairs",
                  "Indian Constitution basics"
                ].map(suggest => (
                  <button 
                    key={suggest} 
                    className="chip" 
                    onClick={() => setCurrentInput(suggest)}
                    style={{ padding: '12px', height: 'auto', whiteSpace: 'normal', textAlign: 'center', cursor: 'pointer' }}
                  >
                    {suggest}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            messages.map((msg, idx) => (
              <div key={idx} className={`chat-msg-wrapper ${msg.role === 'assistant' ? 'assistant' : 'user'}`}>
                <div className="chat-msg-info">
                  <div className="chat-avatar">{msg.role === 'assistant' ? 'R' : (user?.displayName?.[0] || 'U')}</div>
                  <span>{msg.role === 'assistant' 
                    ? (t('tutor.role.assistant') === 'tutor.role.assistant' ? 'Riya AI' : t('tutor.role.assistant')) 
                    : (t('tutor.role.user') === 'tutor.role.user' ? 'You' : t('tutor.role.user'))}</span>
                </div>
                
                <div className="chat-bubble">
                  {msg.role === 'assistant' ? (
                    <div className="text-answer-card" style={{ fontSize: 'inherit' }}>
                      <SafeMarkdown>{msg.content || ''}</SafeMarkdown>
                    </div>
                  ) : (
                    <div>{msg.content}</div>
                  )}

                </div>
              </div>
            ))
          )}
          
          {loading && (
            <div className="chat-msg-wrapper assistant">
              <div className="chat-msg-info">
                <div className="chat-avatar">R</div>
                <span>{t('tutor.thinking') === 'tutor.thinking' ? 'Riya is thinking...' : t('tutor.thinking')}</span>
              </div>
              <div className="chat-bubble" style={{ padding: '12px 20px' }}>
                <div style={{ display: 'flex', gap: 6 }}>
                  <div className="dot" style={{ width: 8, height: 8, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite' }} />
                  <div className="dot" style={{ width: 8, height: 8, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite', animationDelay: '0.2s' }} />
                  <div className="dot" style={{ width: 8, height: 8, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite', animationDelay: '0.4s' }} />
                </div>
              </div>
            </div>
          )}

          {error && (
            <div className="alert alert-error" style={{ alignSelf: 'center', maxWidth: '100%' }}>
              <Info size={16} /> {error}
            </div>
          )}
        </div>

        {/* Pinned Input Footer */}
        <section className="riya-input-wrapper">
          <button 
            type="button" 
            className="btn btn-icon" 
            onClick={() => navigate('/dashboard')}
            title="Exit to Dashboard"
            style={{ 
              background: 'var(--bg-tertiary)', 
              color: 'var(--text-primary)', 
              width: '44px', 
              height: '44px', 
              borderRadius: '50%',
              border: '1.5px solid var(--border-color)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0
            }}
          >
            <ArrowLeft size={20} />
          </button>

          <form className="riya-input-container" onSubmit={handleSendMessage}>
            <input
              className="riya-input-field"
              type="text"
              value={currentInput}
              onChange={(e) => setCurrentInput(e.target.value)}
              placeholder={t('tutor.inputPlaceholder') === 'tutor.inputPlaceholder' ? 'Ask Riya anything...' : t('tutor.inputPlaceholder')}
              disabled={loading}
              autoFocus
            />
            <button 
              type="submit" 
              className="riya-send-btn" 
              disabled={loading || !currentInput.trim()}
              title={t('common.send') || 'Send'}
            >
              {loading ? <Loader2 size={18} className="animate-spin" /> : <Send size={18} />}
            </button>
          </form>
        </section>

        <div style={{ padding: '4px 20px 8px', textAlign: 'center', fontSize: '0.65rem', color: 'var(--text-muted)', background: '#FFFFFF' }}>
          {t('tutor.footer') === 'tutor.footer' ? 'Riya AI may make mistakes. Verify important facts.' : t('tutor.footer')}
        </div>
      </div>
    </main>
    </TutorErrorBoundary>
  );
}

