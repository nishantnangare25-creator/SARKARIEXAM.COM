import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { FORUM_CATEGORIES } from '../utils/constants';
import { MessageSquare, Plus, Send, ArrowLeft } from 'lucide-react';
import './Auth.css';

const demoThreads = [
  { id: '1', title: 'How to prepare for UPSC Prelims 2026?', category: 'exam-strategies', author: 'Rahul S.', replies: [{ author: 'Priya M.', content: 'Focus on NCERTs first, then move to standard reference books.' }, { author: 'Amit K.', content: "Mock tests are key. Start them at least 3 months before the exam." }], content: "I'm starting my UPSC preparation. What's the best strategy for Prelims?", createdAt: '2 hours ago' },
  { id: '2', title: 'Budget 2026 Key Highlights for Exams', category: 'current-affairs', author: 'Sneha P.', replies: [{ author: 'Vikram D.', content: 'Focus on the fiscal deficit target and new tax slabs.' }], content: 'Here are the key points from Budget 2026 that are important for competitive exams...', createdAt: '5 hours ago' },
  { id: '3', title: 'Difference between Article 14 and Article 19?', category: 'doubt-solving', author: 'Anjali R.', replies: [], content: 'Can someone explain the fundamental difference between Article 14 (Right to Equality) and Article 19 (Right to Freedom)?', createdAt: '1 day ago' },
];

export default function Forum() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [activeCategory, setActiveCategory] = useState('all');
  const [threads, setThreads] = useState(demoThreads);
  const [selectedThread, setSelectedThread] = useState(null);
  const [showNew, setShowNew] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');
  const [newCategory, setNewCategory] = useState('general');
  const [replyText, setReplyText] = useState('');

  const filtered = activeCategory === 'all' ? threads : threads.filter(th => th.category === activeCategory);

  const createPost = () => {
    if (!newTitle.trim()) return;
    const post = { id: Date.now().toString(), title: newTitle, content: newContent, category: newCategory, author: user?.displayName || 'Anonymous', replies: [], createdAt: 'Just now' };
    setThreads([post, ...threads]);
    setShowNew(false);
    setNewTitle('');
    setNewContent('');
  };

  const addReply = () => {
    if (!replyText.trim() || !selectedThread) return;
    const updated = threads.map(th => {
      if (th.id === selectedThread.id) {
        return { ...th, replies: [...th.replies, { author: user?.displayName || 'Anonymous', content: replyText }] };
      }
      return th;
    });
    setThreads(updated);
    setSelectedThread(updated.find(th => th.id === selectedThread.id));
    setReplyText('');
  };

  const categoryNames = { 'current-affairs': t('forum.currentAffairs'), 'exam-strategies': t('forum.examStrategies'), 'doubt-solving': t('forum.doubtSolving'), general: t('forum.general') };

  if (selectedThread) {
    return (
      <div className="page-wrapper">
        <div className="page-with-sidebar">
          <button className="btn btn-secondary btn-sm" onClick={() => setSelectedThread(null)} style={{ marginBottom: 16 }}>
            <ArrowLeft size={14} /> {t('common.back')}
          </button>
          <div className="card animate-fadeInUp">
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
              <span className="badge badge-primary">{categoryNames[selectedThread.category] || selectedThread.category}</span>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{selectedThread.createdAt}</span>
            </div>
            <h2 style={{ marginBottom: 8 }}>{selectedThread.title}</h2>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: 12 }}>{t('common.by')} {selectedThread.author}</p>
            <p style={{ lineHeight: 1.7 }}>{selectedThread.content}</p>
          </div>

          <h4 style={{ margin: '24px 0 12px' }}>💬 {selectedThread.replies.length} {t('forum.replies')}</h4>
          {selectedThread.replies.map((r, i) => (
            <div key={i} className="card animate-fadeInUp" style={{ marginBottom: 8, padding: 16 }}>
              <div style={{ fontWeight: 600, fontSize: '0.85rem', marginBottom: 4 }}>{r.author}</div>
              <p style={{ fontSize: '0.9rem' }}>{r.content}</p>
            </div>
          ))}

          <div className="card" style={{ marginTop: 16, display: 'flex', gap: 8 }}>
            <input type="text" value={replyText} onChange={e => setReplyText(e.target.value)} placeholder={`${t('forum.reply')}...`} style={{ flex: 1 }} onKeyDown={e => e.key === 'Enter' && addReply()} />
            <button className="btn btn-primary" onClick={addReply}><Send size={16} /></button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><MessageSquare size={28} style={{ verticalAlign: 'middle' }} /> {t('forum.title')}</h1>
          <p>{t('forum.subtitle')}</p>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 8 }}>
          <div className="tabs">
            <button className={`tab ${activeCategory === 'all' ? 'active' : ''}`} onClick={() => setActiveCategory('all')}>{t('common.all') || 'All'}</button>
            {FORUM_CATEGORIES.map(c => (
              <button key={c.id} className={`tab ${activeCategory === c.id ? 'active' : ''}`} onClick={() => setActiveCategory(c.id)}>
                {c.icon} {categoryNames[c.id]}
              </button>
            ))}
          </div>
          <button className="btn btn-primary btn-sm" onClick={() => setShowNew(true)}>
            <Plus size={16} /> {t('forum.newPost')}
          </button>
        </div>

        {showNew && (
          <div className="card animate-fadeInUp" style={{ marginBottom: 16 }}>
            <h4 style={{ marginBottom: 12 }}>{t('forum.newPost')}</h4>
            <div className="input-group" style={{ marginBottom: 8 }}>
              <input type="text" value={newTitle} onChange={e => setNewTitle(e.target.value)} placeholder={t('forum.postTitle')} />
            </div>
            <div className="input-group" style={{ marginBottom: 8 }}>
              <select value={newCategory} onChange={e => setNewCategory(e.target.value)}>
                {FORUM_CATEGORIES.map(c => <option key={c.id} value={c.id}>{categoryNames[c.id]}</option>)}
              </select>
            </div>
            <div className="input-group" style={{ marginBottom: 12 }}>
              <textarea value={newContent} onChange={e => setNewContent(e.target.value)} rows={3} placeholder={t('forum.postContent')} />
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn btn-primary btn-sm" onClick={createPost}>{t('forum.post')}</button>
              <button className="btn btn-secondary btn-sm" onClick={() => setShowNew(false)}>{t('common.cancel')}</button>
            </div>
          </div>
        )}

        {filtered.length === 0 && <div className="card" style={{ textAlign: 'center', padding: 40 }}><p>{t('forum.noPostsYet')}</p></div>}

        {filtered.map(thread => (
          <div key={thread.id} className="card animate-fadeInUp" style={{ marginBottom: 8, cursor: 'pointer' }} onClick={() => setSelectedThread(thread)}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
              <span className="badge badge-primary">{categoryNames[thread.category] || thread.category}</span>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{thread.createdAt}</span>
            </div>
            <h4 style={{ marginBottom: 4 }}>{thread.title}</h4>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{t('common.by')} {thread.author} · {thread.replies.length} {t('forum.replies')}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
