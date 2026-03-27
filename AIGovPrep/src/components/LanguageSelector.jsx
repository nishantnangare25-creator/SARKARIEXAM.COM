import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { languages } from '../i18n';
import { ArrowRight, Sparkles } from 'lucide-react';
import './LanguageSelector.css';

// Language color palette + emojis for premium look
const langMeta = {
  en:  { emoji: '🇮🇳', gradient: 'linear-gradient(135deg,#1a56db,#3b82f6)' },
  hi:  { emoji: '🇮🇳', gradient: 'linear-gradient(135deg,#f59e0b,#f97316)' },
  mr:  { emoji: '🟠', gradient: 'linear-gradient(135deg,#f97316,#ef4444)' },
  ta:  { emoji: '🔵', gradient: 'linear-gradient(135deg,#6366f1,#8b5cf6)' },
  te:  { emoji: '🟢', gradient: 'linear-gradient(135deg,#10b981,#059669)' },
  kn:  { emoji: '🔴', gradient: 'linear-gradient(135deg,#ef4444,#dc2626)' },
  ml:  { emoji: '🟤', gradient: 'linear-gradient(135deg,#92400e,#b45309)' },
  bn:  { emoji: '🟣', gradient: 'linear-gradient(135deg,#7c3aed,#6d28d9)' },
  gu:  { emoji: '🟡', gradient: 'linear-gradient(135deg,#d97706,#f59e0b)' },
  pa:  { emoji: '🟠', gradient: 'linear-gradient(135deg,#ea580c,#f97316)' },
  ur:  { emoji: '🌙', gradient: 'linear-gradient(135deg,#0f766e,#0d9488)' },
  or:  { emoji: '🟢', gradient: 'linear-gradient(135deg,#16a34a,#15803d)' },
  as:  { emoji: '🔵', gradient: 'linear-gradient(135deg,#2563eb,#1d4ed8)' },
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
      {/* Animated background orbs */}
      <div className="lang-splash-bg">
        <div className="lang-orb lang-orb-1"></div>
        <div className="lang-orb lang-orb-2"></div>
        <div className="lang-orb lang-orb-3"></div>
      </div>

      <div className="lang-splash-card animate-fadeInUp">
        {/* Branding */}
        <div className="lang-splash-header">
          <div className="lang-logo">
            <span className="lang-logo-icon">🎯</span>
            <span>Sarkari <span className="accent">Exam</span> AI</span>
          </div>
          <h1>Choose Your Language</h1>
          <h2>अपनी भाषा चुनें</h2>
          <p>आपली भाषा निवडा &bull; உங்கள் மொழியை தேர்ந்தெடுக்கவும் &bull; আপনার ভাষা বেছে নিন</p>
        </div>

        {/* Language Grid */}
        <div className="lang-grid">
          {languages.map((lang) => {
            const meta = langMeta[lang.code] || { emoji: '🌐', gradient: 'linear-gradient(135deg,#1a56db,#3b82f6)' };
            const isSelected = selected === lang.code;
            return (
              <button
                key={lang.code}
                className={`lang-card ${isSelected ? 'selected' : ''}`}
                onClick={() => handleSelect(lang.code)}
                style={isSelected ? { background: meta.gradient, borderColor: 'transparent' } : {}}
              >
                <span className="lang-emoji">{meta.emoji}</span>
                <span className="lang-native" style={isSelected ? { color: 'white' } : {}}>{lang.nativeName}</span>
                <span className="lang-english" style={isSelected ? { color: 'rgba(255,255,255,0.8)' } : {}}>{lang.name.toUpperCase()}</span>
                {isSelected && <span className="lang-check">✓</span>}
              </button>
            );
          })}
        </div>

        {/* CTA Button */}
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
  );
}
