import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { logout } from '../services/firebase';
import { 
  Settings as SettingsIcon, User, LogOut, Info, ShieldCheck, LogIn, ChevronRight, Key 
} from 'lucide-react';
import './Auth.css';

export default function Settings() {
  const { t } = useTranslation();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [customKey, setCustomKey] = useState('');
  const [keySaved, setKeySaved] = useState(false);

  useEffect(() => {
    const saved = localStorage.getItem('sarkari_custom_gemini_key');
    if (saved) setCustomKey(saved);
  }, []);

  const handleSaveKey = () => {
    if (customKey.trim()) {
      localStorage.setItem('sarkari_custom_gemini_key', customKey.trim());
      setKeySaved(true);
      setTimeout(() => setKeySaved(false), 3000);
    } else {
      localStorage.removeItem('sarkari_custom_gemini_key');
      setKeySaved(true);
      setTimeout(() => setKeySaved(false), 3000);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/');
    } catch (error) {
      console.error('Failed to log out', error);
    }
  };

  const initials = user?.displayName
    ? user.displayName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : 'ST';

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><SettingsIcon size={28} style={{ verticalAlign: 'middle', marginRight: '10px' }} /> {t('nav.settings', 'Settings')}</h1>
        </div>

        <div className="settings-container" style={{ maxWidth: '800px', margin: '0 auto', width: '100%', display: 'flex', flexDirection: 'column', gap: '24px', paddingBottom: '40px' }}>
          
          {/* Account & Profile Section */}
          <div className="card animate-fadeInUp" style={{ padding: '24px' }}>
            <h3 style={{ marginBottom: 20, display: 'flex', alignItems: 'center', gap: 10, fontSize: '1.2rem', color: 'var(--text-primary)' }}>
              <User size={22} color="var(--primary)" /> Profile Management
            </h3>
            
            {user ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px', background: 'var(--bg-glass)', borderRadius: '12px', border: '1px solid var(--border-color)' }}>
                  <div style={{
                    width: '60px', height: '60px', borderRadius: '50%', background: 'linear-gradient(135deg, var(--primary), var(--secondary))',
                    display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '1.5rem', fontWeight: 600
                  }}>
                    {initials}
                  </div>
                  <div style={{ flex: 1 }}>
                    <h4 style={{ margin: 0, fontSize: '1.1rem', color: 'var(--text-primary)' }}>{user.displayName || 'Student'}</h4>
                    <p style={{ margin: '4px 0 0 0', color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{user.email || 'No email provided'}</p>
                  </div>
                </div>
                
                <button 
                  onClick={handleLogout}
                  className="btn btn-outline" 
                  style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px', color: '#ef4444', borderColor: 'rgba(239, 68, 68, 0.3)' }}
                >
                  <LogOut size={18} /> Logout
                </button>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '20px 0' }}>
                <p style={{ color: 'var(--text-secondary)', marginBottom: '16px' }}>You are currently browsing as a guest. Log in to save your progress and access personalized tests.</p>
                <Link to="/login" className="btn btn-primary" style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}>
                  <LogIn size={18} /> Login / Sign Up
                </Link>
              </div>
            )}
          </div>

          {/* BYOK Developer Section */}
          <div className="card animate-fadeInUp" style={{ animationDelay: '0.05s', padding: '24px', border: '1px solid rgba(168, 85, 247, 0.3)' }}>
            <h3 style={{ marginBottom: 10, display: 'flex', alignItems: 'center', gap: 10, fontSize: '1.2rem', color: 'var(--text-primary)' }}>
              <Key size={22} color="#a855f7" /> Custom API Key (BYOK)
            </h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '16px' }}>
              If our servers are busy, you can use your own free <strong>Google Gemini API Key</strong> to completely bypass rate limits and access premium features.
            </p>
            <div style={{ display: 'flex', gap: '10px', flexDirection: 'column' }}>
              <input
                type="password"
                placeholder="Paste your Gemini AI Key here (AIzaSy...)"
                value={customKey}
                onChange={(e) => setCustomKey(e.target.value)}
                className="input"
                style={{ width: '100%', padding: '12px', background: 'var(--bg-glass)' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <a href="https://aistudio.google.com/app/apikey" target="_blank" rel="noreferrer" style={{ fontSize: '0.85rem', color: 'var(--primary)' }}>Get Free Key</a>
                <button onClick={handleSaveKey} className="btn btn-primary" style={{ padding: '8px 20px' }}>
                  {keySaved ? 'Saved!' : 'Save Key'}
                </button>
              </div>
            </div>
          </div>

          {/* Information & Legal Section */}
          <div className="card animate-fadeInUp" style={{ animationDelay: '0.1s', padding: '24px' }}>
            <h3 style={{ marginBottom: 20, display: 'flex', alignItems: 'center', gap: 10, fontSize: '1.2rem', color: 'var(--text-primary)' }}>
              <Info size={22} color="var(--secondary)" /> Information
            </h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              <Link to="/about" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px', background: 'var(--bg-glass)', borderRadius: '12px', border: '1px solid var(--border-color)', color: 'var(--text-primary)', textDecoration: 'none', transition: 'all 0.2s' }} className="hover-lift">
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div style={{ padding: '8px', background: 'rgba(56, 189, 248, 0.1)', borderRadius: '8px', color: '#38bdf8' }}>
                    <Info size={20} />
                  </div>
                  <span style={{ fontWeight: 500 }}>About Us</span>
                </div>
                <ChevronRight size={18} color="var(--text-secondary)" />
              </Link>

              <Link to="/privacy" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px', background: 'var(--bg-glass)', borderRadius: '12px', border: '1px solid var(--border-color)', color: 'var(--text-primary)', textDecoration: 'none', transition: 'all 0.2s' }} className="hover-lift">
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <div style={{ padding: '8px', background: 'rgba(168, 85, 247, 0.1)', borderRadius: '8px', color: '#a855f7' }}>
                    <ShieldCheck size={20} />
                  </div>
                  <span style={{ fontWeight: 500 }}>Privacy Policy</span>
                </div>
                <ChevronRight size={18} color="var(--text-secondary)" />
              </Link>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}
