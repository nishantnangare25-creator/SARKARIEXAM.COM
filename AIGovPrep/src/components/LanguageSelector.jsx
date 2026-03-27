import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { languages } from '../i18n';
import { Globe, ArrowRight, Sparkles, BookOpen } from 'lucide-react';
import './LanguageSelector.css';

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

  return (
    <div className="lang-splash" id="language-selector">
      <div className="lang-splash-bg">
        <div className="lang-orb lang-orb-1"></div>
        <div className="lang-orb lang-orb-2"></div>
        <div className="lang-orb lang-orb-3"></div>
      </div>

      <div className="lang-splash-card animate-fadeInUp">
        <div className="lang-splash-header">
          <div className="lang-logo">
            <BookOpen className="logo-icon" size={32} />
            Sarkari <span className="accent">AI</span> Exam
          </div>
          <div className="lang-splash-icon">
            <Globe size={40} />
          </div>
          <h1>Choose Your Language</h1>
          <h2>अपनी भाषा चुनें</h2>
          <p>आपली भाषा निवडा • உங்கள் மொழியை தேர்ந்தெடுக்கவும் • আপনার ভাষা বেছে নিন</p>
        </div>

        <div className="lang-grid">
          {languages.map((lang) => (
            <button
              key={lang.code}
              className={`lang-card ${selected === lang.code ? 'selected' : ''}`}
              onClick={() => handleSelect(lang.code)}>
              <span className="lang-native">{lang.nativeName}</span>
              <span className="lang-english">{lang.name}</span>
            </button>
          ))}
        </div>

        <button
          className="btn btn-primary btn-lg lang-continue"
          onClick={handleContinue}
          disabled={!selected}>
          <Sparkles size={18} />
          {selected ? (
            languages.find(l => l.code === selected)?.code === 'en'
              ? 'Continue in English'
              : `Continue · ${languages.find(l => l.code === selected)?.nativeName}`
          ) : 'Select a Language'}
          <ArrowRight size={18} />
        </button>
      </div>
    </div>
  );
}
