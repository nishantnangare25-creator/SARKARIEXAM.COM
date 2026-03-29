import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { logout } from '../services/firebase';
import { languages } from '../i18n';
import { Menu, X, Globe, LogOut, User, Search, ChevronDown } from 'lucide-react';
import './Navbar.css';

export default function Navbar({ onToggleSidebar }) {
  const { t, i18n } = useTranslation();
  const { user } = useAuth();
  const [langOpen, setLangOpen] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  const isLanding = location.pathname === '/';

  const changeLanguage = (code) => {
    i18n.changeLanguage(code);
    setLangOpen(false);
  };

  const currentLang = languages.find(l => l.code === i18n.language) || languages[0];

  const initials = user?.displayName
    ? user.displayName.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : 'ST';

  return (
    <nav className="navbar" id="main-navbar">
      <div className="navbar-inner">
        {/* Left */}
        <div className="navbar-left">
          {!isLanding && (
            <button className="btn-icon sidebar-toggle" onClick={onToggleSidebar} aria-label="Toggle sidebar">
              <Menu size={20} />
            </button>
          )}
          <Link to="/" className="navbar-logo">
            <span className="logo-text">
              <span className="logo-name">Sarkari</span>
              <span className="logo-ai" style={{ fontSize: '0.65em', marginLeft: '6px', fontWeight: '600' }}>ExamAI</span>
            </span>
          </Link>
        </div>

        {/* Center — Search (hidden on landing & mobile) */}
        {!isLanding && (
          <div className="navbar-search">
            <Search size={16} />
            <input type="text" placeholder="Search topics, exams, notes…" aria-label="Search" />
          </div>
        )}

        {/* Landing nav links */}
        {isLanding && (
          <div className={`navbar-links ${menuOpen ? 'open' : ''}`}>
            <a href="#features">{t('home.ctaSecondary')}</a>
            <a href="#exams">{t('home.examTitle')}</a>
            {!user && <Link to="/login" style={{ fontWeight: 600, color: 'var(--primary)' }}>Login / Sign Up</Link>}

            <div className="mobile-menu-cta">
              <Link to="/dashboard" className="btn btn-sm btn-nav-dashboard">Go to Dashboard</Link>
            </div>
          </div>
        )}



        {/* Right */}
        <div className="navbar-right">
          {/* Language */}
          <div className="lang-switcher">
            <button className="lang-switcher-btn sleek" onClick={() => setLangOpen(!langOpen)} title="Change language" aria-label="Language selector">
              <Globe size={16} strokeWidth={2.5} />
              <span className="lang-switcher-text">{currentLang.code.toUpperCase()}</span>
            </button>
            {langOpen && (
              <div className="lang-dropdown">
                {languages.map(l => (
                  <button
                    key={l.code}
                    className={`lang-option ${i18n.language === l.code ? 'active' : ''}`}
                    onClick={() => changeLanguage(l.code)}
                  >
                    {l.nativeName}
                  </button>
                ))}
              </div>
            )}
          </div>

          {user ? (
            <div className="user-menu">
              <Link to="/settings" className="profile-pill" title="Profile & Settings">
                <div className="profile-avatar">{initials}</div>
                <span>{user.displayName?.split(' ')[0] || 'Profile'}</span>
              </Link>
            </div>
          ) : (
            <div className="auth-btns" style={{ display: 'flex', gap: '8px' }}>
              <Link to="/login" className="btn btn-sm btn-outline">Login</Link>
              <Link to="/login" className="btn btn-sm btn-nav-dashboard">
                <span className="btn-text-full">Sign Up Free</span>
                <span className="btn-text-short">Sign Up</span>
              </Link>
            </div>
          )}

          {isLanding && (
            <button className="btn-icon mobile-menu" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle menu">
              {menuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          )}
        </div>
      </div>
    </nav>
  );
}
