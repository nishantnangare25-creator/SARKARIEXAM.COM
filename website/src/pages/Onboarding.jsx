import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { saveUserProfile } from '../services/firebase';
import { languages } from '../i18n';
import { EXAMS, SUBJECTS, PREP_LEVELS } from '../utils/constants';
import { ArrowRight, ArrowLeft, CheckCircle } from 'lucide-react';
import './Auth.css';

export default function Onboarding() {
  const { t, i18n } = useTranslation();
  const { user, setProfile } = useAuth();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [data, setData] = useState({ exam: '', language: 'en', hours: 4, level: 'beginner', weakSubjects: [], strongSubjects: [] });

  const toggleSubject = (type, subject) => {
    setData(prev => {
      const arr = prev[type].includes(subject)
        ? prev[type].filter(s => s !== subject)
        : [...prev[type], subject];
      return { ...prev, [type]: arr };
    });
  };

  const finish = async () => {
    if (user) {
      const profileData = { ...data, onboarded: true };
      await saveUserProfile(user.uid, profileData);
      setProfile(profileData);
    }
    navigate('/dashboard');
  };

  const subjects = data.exam ? SUBJECTS[data.exam] || [] : [];

  return (
    <div className="auth-page" id="onboarding-page">
      <div className="auth-card animate-fadeInUp" style={{ maxWidth: 520 }}>
        <div className="auth-header">
          <h1>{t('onboarding.title')}</h1>
          <div className="onboarding-steps">
            {[1,2,3,4].map(s => (
              <div key={s} className={`step-dot ${step >= s ? 'active' : ''}`}>
                {step > s ? <CheckCircle size={16} /> : s}
              </div>
            ))}
          </div>
        </div>

        {step === 1 && (
          <div className="onboarding-content animate-fadeIn">
            <h3>{t('onboarding.step1')}</h3>
            <div className="exam-select-grid">
              {EXAMS.map(exam => (
                <button key={exam.id}
                  className={`exam-select-btn ${data.exam === exam.id ? 'selected' : ''}`}
                  onClick={() => setData({ ...data, exam: exam.id })}>
                  <span>{exam.icon}</span> {exam.name}
                </button>
              ))}
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="onboarding-content animate-fadeIn">
            <h3>{t('onboarding.step2')}</h3>
            <div className="lang-select-grid">
              {languages.map(l => (
                <button key={l.code}
                  className={`exam-select-btn ${data.language === l.code ? 'selected' : ''}`}
                  onClick={() => { setData({ ...data, language: l.code }); i18n.changeLanguage(l.code); }}>
                  {l.nativeName} ({l.name})
                </button>
              ))}
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="onboarding-content animate-fadeIn">
            <h3>{t('onboarding.step3')}</h3>
            <div className="input-group">
              <label>{t('onboarding.hoursLabel')}</label>
              <input type="range" min="1" max="12" value={data.hours}
                onChange={e => setData({ ...data, hours: parseInt(e.target.value) })} />
              <span className="range-value">{data.hours} {t('onboarding.hoursSuffix') || 'hrs/day'}</span>
            </div>
            <div className="input-group" style={{ marginTop: 16 }}>
              <label>{t('onboarding.levelLabel')}</label>
              <div className="level-btns">
                {PREP_LEVELS.map(lvl => (
                  <button key={lvl}
                    className={`exam-select-btn ${data.level === lvl ? 'selected' : ''}`}
                    onClick={() => setData({ ...data, level: lvl })}>
                    {t(`studyPlanner.${lvl}`)}
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}

        {step === 4 && (
          <div className="onboarding-content animate-fadeIn">
            <h3>{t('onboarding.step4')}</h3>
            {subjects.length > 0 ? (
              <>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: 12 }}>
                  {t('onboarding.step4Desc')}
                </p>
                <div className="subject-assessment">
                  {subjects.map(sub => (
                    <div key={sub} className="subject-row">
                      <span>{sub}</span>
                      <div className="subject-btns">
                        <button className={`sub-btn weak ${data.weakSubjects.includes(sub) ? 'active' : ''}`}
                          onClick={() => toggleSubject('weakSubjects', sub)}>{t('onboarding.weakLabel') || 'Weak'}</button>
                        <button className={`sub-btn strong ${data.strongSubjects.includes(sub) ? 'active' : ''}`}
                          onClick={() => toggleSubject('strongSubjects', sub)}>{t('onboarding.strongLabel') || 'Strong'}</button>
                      </div>
                    </div>
                  ))}
                </div>
              </>
            ) : <p>{t('onboarding.noExamSelected') || 'Please select an exam first.'}</p>}
          </div>
        )}

        <div className="onboarding-actions">
          {step > 1 && (
            <button className="btn btn-secondary" onClick={() => setStep(step - 1)}>
              <ArrowLeft size={16} /> {t('onboarding.back')}
            </button>
          )}
          {step < 4 ? (
            <button className="btn btn-primary" onClick={() => setStep(step + 1)}
              disabled={step === 1 && !data.exam}>
              {t('onboarding.next')} <ArrowRight size={16} />
            </button>
          ) : (
            <button className="btn btn-primary" onClick={finish}>
              {t('onboarding.finish')} <ArrowRight size={16} />
            </button>
          )}
        </div>
      </div>

      <style>{`
        .onboarding-steps { display: flex; gap: 12px; justify-content: center; margin-top: 16px; }
        .step-dot { width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 0.8rem; font-weight: 700; background: var(--bg-glass); border: 2px solid var(--border-color); color: var(--text-muted); transition: all 0.3s; }
        .step-dot.active { background: var(--primary); border-color: var(--primary); color: white; }
        .onboarding-content { margin: 24px 0; }
        .onboarding-content h3 { margin-bottom: 16px; font-size: 1.1rem; }
        .exam-select-grid, .lang-select-grid, .level-btns { display: flex; flex-direction: column; gap: 8px; }
        .exam-select-btn { padding: 12px 16px; background: var(--bg-glass); border: 1px solid var(--border-color); border-radius: var(--radius-md); color: var(--text-secondary); font-size: 0.9rem; cursor: pointer; text-align: left; transition: all 0.2s; display: flex; align-items: center; gap: 8px; }
        .exam-select-btn:hover { border-color: var(--primary); color: var(--text-primary); }
        .exam-select-btn.selected { background: var(--primary-glow); border-color: var(--primary); color: var(--primary-light); font-weight: 600; }
        .range-value { font-weight: 700; color: var(--primary); font-size: 1.1rem; }
        .subject-assessment { display: flex; flex-direction: column; gap: 8px; max-height: 300px; overflow-y: auto; }
        .subject-row { display: flex; align-items: center; justify-content: space-between; padding: 8px 12px; background: var(--bg-glass); border-radius: var(--radius-sm); }
        .subject-btns { display: flex; gap: 6px; }
        .sub-btn { padding: 4px 12px; border-radius: var(--radius-full); border: 1px solid var(--border-color); background: none; color: var(--text-muted); font-size: 0.75rem; cursor: pointer; transition: all 0.2s; }
        .sub-btn.weak.active { background: rgba(255,64,64,0.15); border-color: #ff4040; color: #ff6b6b; }
        .sub-btn.strong.active { background: rgba(0,201,167,0.15); border-color: var(--accent-green); color: var(--accent-green); }
        .onboarding-actions { display: flex; justify-content: space-between; gap: 12px; margin-top: 8px; }
        .onboarding-actions .btn { flex: 1; justify-content: center; }
        input[type=range] { -webkit-appearance: none; width: 100%; height: 6px; border-radius: 3px; background: var(--border-color); outline: none; }
        input[type=range]::-webkit-slider-thumb { -webkit-appearance: none; width: 20px; height: 20px; border-radius: 50%; background: var(--primary); cursor: pointer; }
      `}</style>
    </div>
  );
}
