import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import ReactMarkdown from 'react-markdown';
import { 
  Bot, Sparkles, Download, Send, 
  MessageSquare, User, Info, Trash2, Languages, Loader2, FileText
} from 'lucide-react';
import { generateTutorLesson } from '../services/ai';
import { useAuth } from '../contexts/AuthContext';
import { generateNotesPdf } from '../utils/pdfGenerator';
import './InteractiveTutor.css';

export default function InteractiveTutor() {
  const { t, i18n } = useTranslation();
  const { user } = useAuth();
  
  const [messages, setMessages] = useState([]);
  const [currentInput, setCurrentInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const scrollRef = useRef(null);

  // Auto-scroll to bottom
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTo({
        top: scrollRef.current.scrollHeight,
        behavior: 'smooth'
      });
    }
  }, [messages, loading]);


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
      setError("Download failed. Please check your browser permissions.");
    }
  };

  const clearChat = () => {
    if (window.confirm('Are you sure you want to clear the entire chat?')) {
      setMessages([]);
    }
  };

  return (
    <main className="page-wrapper tutor-page-wrapper">
      {/* Immersive Layout */}
      <div className="page-with-sidebar riya-tutor-container">
        
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
                Online
              </span>
              <span className="riya-lang-badge">
                <Languages size={10} />
                {i18n.language.toUpperCase()}
              </span>
            </div>
          </div>

          <div className="riya-header-actions">
            <button 
              className="btn-riya-action" 
              onClick={downloadChatPdf} 
              disabled={messages.length === 0}
              title="Download PDF"
            >
              <Download size={15} />
            </button>
            <button 
              className="btn-riya-action" 
              onClick={downloadChatText} 
              disabled={messages.length === 0}
              title="Download Text"
            >
              <FileText size={15} />
            </button>
            <button 
              className="btn-riya-action danger" 
              onClick={clearChat} 
              disabled={messages.length === 0}
              title="Clear History"
            >
              <Trash2 size={15} />
            </button>
          </div>
        </header>

        {/* Chat Scrolling Area */}
        <div className="riya-chat-area" ref={scrollRef}>
          {messages.length === 0 ? (
            <div style={{ margin: 'auto', textAlign: 'center', maxWidth: 450 }} className="animate-fadeIn">
              <div className="feature-icon indigo" style={{ margin: '0 auto 24px', width: 64, height: 64 }}>
                <Sparkles size={32} />
              </div>
              <h2 style={{ marginBottom: 12 }}>Hi! I'm Riya, your AI Tutor.</h2>
              <p style={{ color: 'var(--text-muted)', marginBottom: 32 }}>Ask me for topic explanations, problem solving steps, or exam strategies in your own language.</p>
              
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                {[
                  'Explain Fundamental Rights',
                  'UPSC Preparation Strategy',
                  'Solve quadratic equation',
                  'History of Indian Constitution'
                ].map(suggest => (
                  <button 
                    key={suggest} 
                    className="chip" 
                    onClick={() => setCurrentInput(suggest)}
                    style={{ padding: '12px', height: 'auto', whiteSpace: 'normal', textAlign: 'center' }}
                  >
                    "{suggest}"
                  </button>
                ))}
              </div>
            </div>
          ) : (
            messages.map((msg, idx) => (
              <div key={idx} className={`chat-msg-wrapper ${msg.role === 'assistant' ? 'assistant' : 'user'}`}>
                <div className="chat-msg-info">
                  <div className="chat-avatar">{msg.role === 'assistant' ? 'R' : 'U'}</div>
                  <span>{msg.role === 'assistant' ? 'RIYA' : 'YOU'}</span>
                </div>
                
                <div className="chat-bubble">
                  {msg.role === 'assistant' ? (
                    <div className="text-answer-card" style={{ fontSize: 'inherit' }}>
                      <ReactMarkdown>{msg.content}</ReactMarkdown>
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
                <span>RIYA IS THINKING...</span>
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
          <form className="riya-input-container" onSubmit={handleSendMessage}>
            <input
              className="riya-input-field"
              type="text"
              value={currentInput}
              onChange={(e) => setCurrentInput(e.target.value)}
              placeholder="Ask Riya anything..."
              disabled={loading}
              autoFocus
            />
            <button 
              type="submit" 
              className="riya-send-btn" 
              disabled={loading || !currentInput.trim()}
              title="Send Message"
            >
              {loading ? <Loader2 size={18} className="animate-spin" /> : <Send size={18} />}
            </button>
          </form>
        </section>

        <div style={{ padding: '4px 20px 8px', textAlign: 'center', fontSize: '0.65rem', color: 'var(--text-muted)', background: '#FFFFFF' }}>
          AI powered by Riya. Verify facts with textbooks.
        </div>
      </div>
    </main>
  );
}
