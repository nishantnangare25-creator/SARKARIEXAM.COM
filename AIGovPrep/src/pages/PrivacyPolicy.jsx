import React from 'react';
import { useTranslation } from 'react-i18next';
import { Shield, Lock, Eye, FileText, UserCheck, Mail } from 'lucide-react';

export default function PrivacyPolicy() {
  const { t } = useTranslation();
  const sections = [
    {
      icon: <Eye className="text-blue" />,
      title: t('privacy.collection.title'),
      content: t('privacy.collection.content')
    },
    {
      icon: <Lock className="text-saffron" />,
      title: t('privacy.usage.title'),
      content: t('privacy.usage.content')
    },
    {
      icon: <Shield className="text-green" />,
      title: t('privacy.security.title'),
      content: t('privacy.security.content')
    },
    {
      icon: <UserCheck className="text-blue" />,
      title: t('privacy.rights.title'),
      content: t('privacy.rights.content')
    }
  ];

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-container" style={{ maxWidth: '800px' }}>
        <section className="page-header" style={{ marginBottom: '40px' }}>
          <h1 className="animate-fadeInUp">{t('privacy.title')}</h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.1s' }}>
            {t('privacy.updated')}
          </p>
        </section>

        <div className="card-glass animate-fadeInUp" style={{ animationDelay: '0.2s', padding: '32px', marginBottom: '40px' }}>
          <div className="alert alert-info">
             <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                <Shield size={20} />
                <span><strong>{t('privacy.commitment')}</strong> {t('privacy.commitmentDesc')}</span>
             </div>
          </div>
          
          <div className="text-answer-card">
            <p>
              {t('privacy.intro')}
            </p>
            
            <div className="divider"></div>

            {sections.map((s, i) => (
              <div key={i} style={{ marginBottom: '32px' }}>
                <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '12px' }}>
                   {s.icon}
                   <h3 style={{ margin: 0 }}>{s.title}</h3>
                </div>
                <p>{s.content}</p>
              </div>
            ))}

            <div className="divider"></div>

            <h3>{t('privacy.cookiesTitle')}</h3>
            <p>
              {t('privacy.cookiesDesc')}
            </p>

            <h3>{t('privacy.thirdPartyTitle')}</h3>
            <p>
              {t('privacy.thirdPartyDesc')}
            </p>

            <div className="card-colored" style={{ background: 'var(--bg-tertiary)', marginTop: '40px' }}>
               <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '12px' }}>
                  <Mail className="text-blue" size={20} />
                  <h4 style={{ margin: 0 }}>{t('privacy.contact.title')}</h4>
               </div>
               <p style={{ marginBottom: 0 }}>
                 {t('privacy.contact.desc')}
               </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
