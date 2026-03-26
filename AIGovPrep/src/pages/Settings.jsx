import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { languages } from '../i18n';
import { Settings as SettingsIcon, Key, Globe, Save, Check, RefreshCw, AlertCircle, CheckCircle2 } from 'lucide-react';
import './Auth.css';




export default function Settings() {
  const { t, i18n } = useTranslation();
  const [lang, setLang] = useState(i18n.language);

  const changeLang = (code) => {
    setLang(code);
    i18n.changeLanguage(code);
  };

  return (
    <div className="page-wrapper">
      <div className="page-with-sidebar">
        <div className="page-header animate-fadeInUp">
          <h1><SettingsIcon size={28} style={{ verticalAlign: 'middle' }} /> {t('settings.title')}</h1>
        </div>


        {/* Language */}
        <div className="card animate-fadeInUp">
          <h3 style={{ marginBottom: 12, display: 'flex', alignItems: 'center', gap: 8 }}>
            <Globe size={20} color="var(--primary)" /> {t('settings.language')}
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {languages.map(l => (
              <button key={l.code}
                onClick={() => changeLang(l.code)}
                style={{
                  padding: '12px 16px',
                  background: lang === l.code ? 'var(--primary-glow)' : 'var(--bg-glass)',
                  border: `1px solid ${lang === l.code ? 'var(--primary)' : 'var(--border-color)'}`,
                  borderRadius: 'var(--radius-md)',
                  color: lang === l.code ? 'var(--primary-light)' : 'var(--text-secondary)',
                  fontWeight: lang === l.code ? 600 : 400,
                  cursor: 'pointer',
                  textAlign: 'left',
                  fontSize: '0.9rem',
                  transition: 'all 0.2s',
                  display: 'flex',
                  justifyContent: 'space-between',
                }}>
                <span>{l.nativeName} ({l.name})</span>
                {lang === l.code && <Check size={16} />}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
