import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { languages } from '../i18n';
import { ArrowRight, Sparkles } from 'lucide-react';
import logo from '../assets/logo.png';
import './LanguageSelector.css';

// Sleek gradients for selection states
const langMeta = {
  en:  { gradient: 'linear-gradient(135deg,#1a56db,#3b82f6)' },
  hi:  { gradient: 'linear-gradient(135deg,#f59e0b,#f97316)' },
  mr:  { gradient: 'linear-gradient(135deg,#f97316,#ef4444)' },
  ta:  { gradient: 'linear-gradient(135deg,#6366f1,#8b5cf6)' },
  te:  { gradient: 'linear-gradient(135deg,#10b981,#059669)' },
  kn:  { gradient: 'linear-gradient(135deg,#ef4444,#dc2626)' },
  ml:  { gradient: 'linear-gradient(135deg,#92400e,#b45309)' },
  bn:  { gradient: 'linear-gradient(135deg,#7c3aed,#6d28d9)' },
  gu:  { gradient: 'linear-gradient(135deg,#d97706,#f59e0b)' },
  pa:  { gradient: 'linear-gradient(135deg,#ea580c,#f97316)' },
  ur:  { gradient: 'linear-gradient(135deg,#0f766e,#0d9488)' },
  or:  { gradient: 'linear-gradient(135deg,#16a34a,#15803d)' },
  as:  { gradient: 'linear-gradient(135deg,#2563eb,#1d4ed8)' },
};

export default function LanguageSelector({ onSelect }) {
  const { i18n } = useTranslation();
  const [selected, setSelected] = useState('');

  const handleSelect = (code) => {
    setSelected(code);
    i18n.changeLanguage(code);
  };

  const handleContinue = () => {
    if (selected) {
      localStorage.setItem('languageSelected', 'true');
      localStorage.setItem('i18nextLng', selected);
      onSelect(selected);
    }
  };

  const selectedLang = languages.find(l => l.code === selected);

  return (
    <div className="lang-splash" id="language-selector">
      <div className="lang-splash-bg">
        <div className="lang-orb lang-orb-1"></div>
        <div className="lang-orb lang-orb-2"></div>
      </div>

      <div className="lang-splash-card animate-fadeInUp">
        <div className="lang-logo-center">
          <img src={logo} alt="Logo" className="lang-logo-img" />
        </div>

        <div className="lang-splash-header">
          <h1>Choose Your Language</h1>
          <h2>अपनी भाषा चुनें</h2>
          <p>आपली भाषा निवडा &bull; உங்கள் மொழியை தேர்ந்தெடுக்கவும் &bull; আপনার ভাষা বেছে নিন</p>
        </div>

        <div className="lang-grid">
          {languages.map((lang) => {
            const meta = langMeta[lang.code] || { gradient: 'linear-gradient(135deg,#1a56db,#3b82f6)' };
            const isSelected = selected === lang.code;
            return (
              <button
                key={lang.code}
                className={`lang-card ${isSelected ? 'selected' : ''}`}
                onClick={() => handleSelect(lang.code)}
                style={isSelected ? { background: meta.gradient, borderColor: 'transparent' } : {}}
              >
                <div className="lang-content-wrapper">
                  <span className="lang-native" style={isSelected ? { color: 'white' } : {}}>{lang.nativeName}</span>
                  <span className="lang-english" style={isSelected ? { color: 'rgba(255,255,255,0.8)' } : {}}>{lang.name.toUpperCase()}</span>
                </div>
                {isSelected && <span className="lang-check">✓</span>}
              </button>
            );
          })}
        </div>

        <div className="lang-footer-action">
          <button
            className="btn btn-primary btn-lg lang-continue"
            onClick={handleContinue}
            disabled={!selected}
          >
            <Sparkles size={18} />
            {selected
              ? `Continue · ${selectedLang?.nativeName || ''}`
              : 'Select a Language'}
            <ArrowRight size={18} />
          </button>
        </div>
      </div>
    </div>
  );
}
