import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { Users, UserPlus, Check, BookOpen, GraduationCap } from 'lucide-react';
import './Auth.css';

const demoPeers = [
  { id: '1', name: 'Priya Sharma', exam: 'UPSC', level: 'Intermediate', subjects: ['History', 'Polity'], avatar: '👩‍🎓', studyHours: 6, streak: 12 },
  { id: '2', name: 'Rohit Kumar', exam: 'UPSC', level: 'Beginner', subjects: ['Geography', 'Economy'], avatar: '👨‍💻', studyHours: 4, streak: 8 },
  { id: '3', name: 'Sneha Patil', exam: 'MPSC', level: 'Advanced', subjects: ['Marathi', 'History'], avatar: '👩‍💼', studyHours: 8, streak: 30 },
  { id: '4', name: 'Amit Deshmukh', exam: 'SSC', level: 'Intermediate', subjects: ['Reasoning', 'Math'], avatar: '🧑‍🎓', studyHours: 5, streak: 15 },
  { id: '5', name: 'Fatima Khan', exam: 'Banking', level: 'Beginner', subjects: ['Quantitative', 'English'], avatar: '👩‍🏫', studyHours: 3, streak: 5 },
  { id: '6', name: 'Arjun Reddy', exam: 'NDA', level: 'Intermediate', subjects: ['Math', 'Physics'], avatar: '💂', studyHours: 7, streak: 22 },
];

export default function PeerMatching() {
  const { t } = useTranslation();
  const { profile } = useAuth();
  const [connected, setConnected] = useState([]);

  const toggleConnect = (peerId) => {
    setConnected(prev => prev.includes(peerId) ? prev.filter(id => id !== peerId) : [...prev, peerId]);
  };

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><Users size={28} style={{ verticalAlign: 'middle' }} /> {t('peerMatch.title')}</h1>
          <p>{t('peerMatch.subtitle')}</p>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
          {demoPeers.map((peer, i) => (
            <div key={peer.id} className="card animate-fadeInUp" style={{ animationDelay: `${i * 0.08}s` }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
                <div style={{ width: 48, height: 48, borderRadius: '50%', background: 'var(--bg-glass)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>
                  {peer.avatar}
                </div>
                <div>
                  <h4 style={{ marginBottom: 2 }}>{peer.name}</h4>
                  <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{peer.exam} · {t(`onboarding.${peer.level.toLowerCase()}`) || peer.level}</span>
                </div>
              </div>

              <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
                {peer.subjects.map(s => <span key={s} className="badge badge-primary">{s}</span>)}
              </div>

              <div style={{ display: 'flex', gap: 16, marginBottom: 16, fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                  <BookOpen size={14} /> {peer.studyHours}{t('studyPlanner.hours').split('/')[0]}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                  🔥 {peer.streak} {t('dashboard.stats.days')}
                </div>
              </div>

              <button
                className={`btn btn-sm ${connected.includes(peer.id) ? 'btn-secondary' : 'btn-primary'}`}
                style={{ width: '100%', justifyContent: 'center' }}
                onClick={() => toggleConnect(peer.id)}>
                {connected.includes(peer.id) ? <><Check size={14} /> {t('peerMatch.connected')}</> : <><UserPlus size={14} /> {t('peerMatch.connect')}</>}
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
