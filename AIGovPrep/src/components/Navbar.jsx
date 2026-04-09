import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { logout } from '../services/firebase';
import { languages } from '../i18n';
import { Menu, X, Globe, LogOut, User, Search, ChevronDown } from 'lucide-react';
import './Navbar.css';

import logo from '../assets/logo.png';

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
            <img src={logo} alt="Sarkari Exam AI" className="navbar-logo-img" />
            <span className="navbar-brand-name">
              Sarkari<span>Exam</span>AI
            </span>
          </Link>
        </div>

        {/* Center — Search (hidden on landing & mobile) */}
        {!isLanding && (
          <div className="navbar-search">
            <Search size={16} />
            <input type="text" placeholder="Search topics, exams, notes..." aria-label="Search" />
          </div>


        )}

        {/* Landing nav links */}
        {isLanding && (
          <div className={`navbar-links ${menuOpen ? 'open' : ''}`}>
            <a href="#features" onClick={() => setMenuOpen(false)}>{t('home.ctaSecondary')}</a>
            <a href="#exams" onClick={() => setMenuOpen(false)}>{t('home.examTitle')}</a>
            {!user && <Link to="/login" style={{ fontWeight: 600, color: 'var(--primary)' }} onClick={() => setMenuOpen(false)}>{t('nav.login')}</Link>}

            <div className="mobile-menu-cta">
              <Link to="/dashboard" className="btn btn-sm btn-nav-dashboard" onClick={() => setMenuOpen(false)}>{t('dashboard.header.overview')}</Link>
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
              <Link to="/settings" className="profile-pill" title={t('nav.settings')}>
                <div className="profile-avatar">{initials}</div>
                <span>{user.displayName?.split(' ')[0] || t('nav.profile')}</span>
              </Link>


            </div>
          ) : (
            <div className="auth-btns">
              <Link to="/login" className="btn btn-sm btn-outline btn-login">{t('nav.login')}</Link>
              <Link to="/login?mode=signup" className="btn btn-sm btn-nav-dashboard btn-signup">
                <span className="btn-text-full">{t('nav.signup')}</span>
                <span className="btn-text-short">{t('nav.signup')}</span>
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
