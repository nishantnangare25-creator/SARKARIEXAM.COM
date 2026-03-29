import { useState, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import ReactMarkdown from 'react-markdown';
import { 
  Bot, Sparkles, Volume2, Download, Send, 
  MessageSquare, User, Info, Trash2, Languages
} from 'lucide-react';
import { generateTutorLesson } from '../services/ai';
import { useAuth } from '../contexts/AuthContext';

export default function InteractiveTutor() {
  const { t, i18n } = useTranslation();
  const { user } = useAuth();
  
  const [messages, setMessages] = useState([]);
  const [currentInput, setCurrentInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [speakingIndex, setSpeakingIndex] = useState(null);
  const scrollRef = useRef(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, loading]);

  useEffect(() => {
    return () => {
      window.speechSynthesis.cancel();
    };
  }, []);

  const toggleSpeech = (text, index) => {
    if (isPlaying && speakingIndex === index) {
      window.speechSynthesis.cancel();
      setIsPlaying(false);
      setSpeakingIndex(null);
      return;
    }

    window.speechSynthesis.cancel(); 
    if (!text) return;

    const cleanText = text
      .replace(/[#*`_]/g, '')
      .replace(/\[([^\]]+)\]\([^\)]+\)/g, '$1')
      .replace(/\n\n/g, '. ')
      .replace(/\n/g, ' ');

    const utterance = new SpeechSynthesisUtterance(cleanText);
    const langMap = {
      'en': 'en-IN', 'hi': 'hi-IN', 'pa': 'pa-IN', 'mr': 'mr-IN',
      'ta': 'ta-IN', 'te': 'te-IN', 'bn': 'bn-IN', 'gu': 'gu-IN',
      'kn': 'kn-IN', 'ml': 'ml-IN', 'or': 'or-IN', 'as': 'as-IN', 'ur': 'ur-IN'
    };
    
    utterance.lang = langMap[i18n.language] || i18n.language;
    utterance.rate = 1.0; 
    
    utterance.onend = () => { setIsPlaying(false); setSpeakingIndex(null); };
    utterance.onerror = () => { setIsPlaying(false); setSpeakingIndex(null); };

    window.speechSynthesis.speak(utterance);
    setIsPlaying(true);
    setSpeakingIndex(index);
  };

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
      
      // We could auto-play here, but maybe it's too much for everyone
      // toggleSpeech(aiResponse, finalHistory.length - 1);
    } catch (err) {
      setError(err.message || t('common.error'));
    } finally {
      setLoading(false);
    }
  };

  const downloadChat = async () => {
    if (messages.length === 0) return;
    let content = "===== Riya AI Tutor Session =====\n\n";
    messages.forEach((msg) => {
      content += `[${msg.role === 'user' ? 'You' : 'Riya'}]:\n${msg.content}\n\n`;
    });
    
    const filename = `riya_tutor_session.txt`;
    const mimeType = 'text/plain;charset=utf-8';

    // 1. Try Native Web Share API (Best for Mobile/WebViews)
    if (navigator.share && navigator.canShare) {
      try {
        const file = new File([content], filename, { type: mimeType });
        if (navigator.canShare({ files: [file] })) {
          await navigator.share({
            title: 'Tutor Session Log',
            files: [file]
          });
          return; // Success via native share/save sheet
        }
      } catch (err) {
        console.log('Web Share failed or was cancelled', err);
        // Fall through to standard download
      }
    }

    // 2. Standard Blob Download (Fallback for Desktop)
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.style.display = 'none';
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    
    // Synchronous click preserves the user gesture! (Crucial for mobile)
    a.click();
    
    setTimeout(() => {
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    }, 1000);
  };

  const clearChat = () => setMessages([]);

  return (
    <main className="page-wrapper">
      <div className="page-with-sidebar" style={{ height: 'calc(100vh - var(--navbar-height))', display: 'flex', flexDirection: 'column' }}>
        
        {/* Tutor Header */}
        <header className="page-header tutor-header" style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%', maxWidth: 1000 }}>
          <div className="tutor-brand">
            <h1 style={{ display: 'flex', alignItems: 'center', gap: 12, margin: 0 }}>
              <Bot size={28} className="text-blue" /> Riya AI
            </h1>
            <p className="tutor-lang-info" style={{ display: 'flex', alignItems: 'center', gap: 6, margin: 0 }}>
              <Languages size={14} className="text-muted" /> 
              Selected Language: <strong className="text-blue">{i18n.language.toUpperCase()}</strong>
            </p>
          </div>
          <div className="tutor-actions" style={{ display: 'flex', gap: 8 }}>
            <button className="btn btn-sm btn-secondary btn-compact" onClick={downloadChat} disabled={messages.length === 0} title="Download Notes">
              <Download size={16} /> <span className="hide-mobile">Download</span>
            </button>
            <button className="btn btn-sm btn-icon" onClick={clearChat} title="Clear Chat">
              <Trash2 size={16} />
            </button>
          </div>
        </header>

        {/* Chat Container */}
        <section className="card tutor-chat-container" style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: 0, overflow: 'hidden', border: '1px solid var(--border-color)', background: '#FFFFFF', width: '100%', maxWidth: 1000 }}>
          
          {/* Scroll Area */}
          <div ref={scrollRef} style={{ flex: 1, overflowY: 'auto', padding: '24px', display: 'flex', flexDirection: 'column', gap: 24, background: '#F9FAFB' }}>
            {messages.length === 0 ? (
              <div style={{ margin: 'auto', textAlign: 'center', maxWidth: 400, opacity: 0.8 }} className="animate-fadeIn">
                <div className="feature-icon indigo" style={{ margin: '0 auto 20px', width: 64, height: 64, fontSize: '2rem' }}>
                  <Sparkles size={32} />
                </div>
                <h3>Ask Riya your doubts!</h3>
                <p style={{ marginTop: 8 }}>Get topic explanations, solve complex problems, or ask for a custom study plan in your preferred language.</p>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, justifyContent: 'center', marginTop: 24 }}>
                  <button onClick={() => setCurrentInput('Explain the Basic Structure Doctrine')} className="chip">"Explain Basic Structure Doctrine"</button>
                  <button onClick={() => setCurrentInput('How to prepare for UPSC Prelims?')} className="chip">"UPSC Prelims Strategy"</button>
                  <button onClick={() => setCurrentInput('Solve this: 2x + 5 = 15')} className="chip">"Solve Math Problem"</button>
                </div>
              </div>
            ) : (
              messages.map((msg, idx) => (
                <div key={idx} style={{ display: 'flex', flexDirection: 'column', alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start', maxWidth: '85%' }}>
                   <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 6, alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start' }}>
                      {msg.role === 'assistant' && <div className="profile-avatar" style={{ width: 24, height: 24, fontSize: '0.65rem' }}>R</div>}
                      <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                        {msg.role === 'user' ? 'YOU' : 'RIYA'}
                      </span>
                      {msg.role === 'user' && <div className="profile-avatar" style={{ width: 24, height: 24, fontSize: '0.65rem', background: 'var(--accent-orange)' }}>U</div>}
                   </div>
                   
                   <div className="card" style={{ 
                     padding: '12px 18px', 
                     background: msg.role === 'user' ? 'var(--primary)' : '#FFFFFF',
                     color: msg.role === 'user' ? '#FFFFFF' : 'var(--text-primary)',
                     borderRadius: msg.role === 'user' ? '20px 4px 20px 20px' : '4px 20px 20px 20px',
                     boxShadow: 'var(--shadow-sm)',
                     border: msg.role === 'user' ? 'none' : '1px solid var(--border-color)'
                   }}>
                      {msg.role === 'assistant' ? (
                        <div className="text-answer-card" style={{ fontSize: '0.92rem' }}>
                          <ReactMarkdown>{msg.content}</ReactMarkdown>
                        </div>
                      ) : (
                        <div style={{ fontSize: '0.92rem' }}>{msg.content}</div>
                      )}

                      {msg.role === 'assistant' && (
                        <div style={{ marginTop: 12, paddingTop: 12, borderTop: '1px solid var(--border-light)', display: 'flex', justifyContent: 'flex-end' }}>
                          <button onClick={() => toggleSpeech(msg.content, idx)} className="btn-icon" style={{ padding: 6, border: 'none' }}>
                            <Volume2 size={16} className={isPlaying && speakingIndex === idx ? 'text-blue' : 'text-muted'} />
                          </button>
                        </div>
                      )}
                   </div>
                </div>
              ))
            )}
            
            {loading && (
              <div style={{ alignSelf: 'flex-start', display: 'flex', gap: 12, alignItems: 'center' }}>
                <div className="profile-avatar" style={{ width: 24, height: 24, fontSize: '0.65rem' }}>R</div>
                <div className="card" style={{ padding: '8px 16px', background: '#FFFFFF', borderRadius: '4px 20px 20px 20px' }}>
                  <div style={{ display: 'flex', gap: 4 }}>
                    <div className="dot" style={{ width: 6, height: 6, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite' }} />
                    <div className="dot" style={{ width: 6, height: 6, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite', animationDelay: '0.2s' }} />
                    <div className="dot" style={{ width: 6, height: 6, background: 'var(--primary)', borderRadius: '50%', animation: 'pulse 1s infinite', animationDelay: '0.4s' }} />
                  </div>
                </div>
              </div>
            )}
            {error && <div className="alert alert-error" style={{ alignSelf: 'center' }}>{error}</div>}
          </div>

          {/* Input Area */}
          <form onSubmit={handleSendMessage} style={{ padding: '20px 24px', background: '#FFFFFF', borderTop: '1px solid var(--border-color)', display: 'flex', gap: 12 }}>
            <div style={{ flex: 1, position: 'relative' }}>
              <input
                type="text"
                value={currentInput}
                onChange={(e) => setCurrentInput(e.target.value)}
                placeholder="Type your study question here..."
                disabled={loading}
                style={{ borderRadius: 'var(--radius-full)', paddingRight: 48, background: '#F3F4F6', border: 'none' }}
              />
              <div style={{ position: 'absolute', right: 14, top: '50%', transform: 'translateY(-50%)', opacity: 0.5 }}>
                 <MessageSquare size={16} />
              </div>
            </div>
            <button type="submit" className="btn btn-primary" style={{ borderRadius: 'var(--radius-full)', padding: '0 20px' }} disabled={loading || !currentInput.trim()}>
              <Send size={18} />
            </button>
          </form>
        </section>

        {/* Disclaimer */}
        <p className="tutor-disclaimer" style={{ textAlign: 'center', fontSize: '0.72rem', marginTop: 12, color: 'var(--text-muted)', width: '100%' }}>
          <Info size={12} style={{ verticalAlign: 'middle', marginRight: 4 }} /> 
          Riya AI can make mistakes. Verify important info with official sources.
        </p>
      </div>
    </main>
  );
}
