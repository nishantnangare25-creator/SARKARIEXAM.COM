import React from 'react';
import { useTranslation } from 'react-i18next';
import { Target, Brain, Sparkles, Rocket, Shield, Users, Landmark, FileText, BarChart3, CheckCircle } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function AboutUs() {
  const { t } = useTranslation();

  const values = [
    {
      icon: <Target className="purple" size={24} />,
      title: t('about.mission.title'),
      desc: t('about.mission.desc')
    },
    {
      icon: <Sparkles className="orange" size={24} />,
      title: t('about.innovation.title'),
      desc: t('about.innovation.desc')
    },
    {
      icon: <Rocket className="green" size={24} />,
      title: t('about.vision.title'),
      desc: t('about.vision.desc')
    }
  ];

  const features = [
    { icon: <Brain />, title: t('about.features.tutor.title'), desc: t('about.features.tutor.desc') },
    { icon: <FileText />, title: t('about.features.pyq.title'), desc: t('about.features.pyq.desc') },
    { icon: <BarChart3 />, title: t('about.features.data.title'), desc: t('about.features.data.desc') }
  ];

  const examsCovered = t('about.examsList', { returnObjects: true }) || [
    "UPSC Civil Services (IAS/IPS/IFS)",
    "MPSC State Services",
    "SSC CGL, CHSL & MTS",
    "IBPS PO & Clerk (Banking)",
    "Railway RRB NTPC & Group D",
    "UPSC CAPF & CDS",
    "State PSC Exams (All India)",
  ];

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-container">
        {/* Hero Section */}
        <section className="page-header text-center" style={{ textAlign: 'center', marginBottom: '60px' }}>
          <div className="badge badge-primary animate-fadeInUp" style={{ marginBottom: '16px' }}>{t('footer.about')}</div>
          <h1 className="animate-fadeInUp" style={{ animationDelay: '0.1s', marginBottom: '20px' }}>
            {t('about.hero.title_line1')} <span className="text-gradient">{t('about.hero.title_accent')}</span>
          </h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.2s', maxWidth: '800px', margin: '0 auto', fontSize: '1.2rem' }}>
            {t('about.hero.subtitle')}
          </p>
        </section>

        {/* Our Pillars */}
        <div className="grid-3" style={{ marginBottom: '80px' }}>
          {values.map((v, i) => (
            <div key={i} className="card animate-fadeInUp" style={{ animationDelay: `${0.3 + i * 0.1}s`, display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center' }}>
              <div className="feature-icon" style={{ backgroundColor: 'var(--bg-tertiary)', marginBottom: '20px' }}>
                {v.icon}
              </div>
              <h3>{v.title}</h3>
              <p>{v.desc}</p>
            </div>
          ))}
        </div>

        {/* Story Section */}
        <section className="card-glass animate-fadeInUp" style={{ animationDelay: '0.6s', padding: '60px', marginBottom: '80px' }}>
          <div className="dashboard-layout" style={{ alignItems: 'center' }}>
            <div className="story-content">
              <h2 style={{ marginBottom: '20px' }}>{t('about.story.title')}</h2>
              <p style={{ marginBottom: '16px', fontSize: '1.05rem' }}>
                {t('about.story.p1')}
              </p>
              <p style={{ marginBottom: '16px', fontSize: '1.05rem' }}>
                {t('about.story.p2')}
              </p>
              <p style={{ marginBottom: '24px', fontSize: '1.05rem' }}>
                {t('about.story.p3')}
              </p>
              <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Shield size={20} className="text-blue" />
                  <span style={{ fontWeight: 600 }}>{t('about.stats.trusted')}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Landmark size={20} className="text-saffron" />
                  <span style={{ fontWeight: 600 }}>{t('about.stats.exams')}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Users size={20} className="text-green" />
                  <span style={{ fontWeight: 600 }}>{t('about.stats.languages')}</span>
                </div>
              </div>
            </div>
            <div className="story-image" style={{ display: 'flex', justifyContent: 'center' }}>
               <div style={{ position: 'relative', width: '300px', height: '300px' }}>
                  <div className="hero-orb orb-1" style={{ width: '200px', height: '200px', top: '20%', left: '20%' }}></div>
                  <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', zIndex: 2 }}>
                    <Brain size={120} className="text-blue animate-pulse" />
                  </div>
               </div>
            </div>
          </div>
        </section>

        {/* Exams Covered */}
        <section style={{ marginBottom: '80px' }}>
          <div className="section-header text-center" style={{ textAlign: 'center', marginBottom: '40px' }}>
            <h2 className="animate-fadeInUp">{t('about.exams.title_line1')} <span className="text-gradient">{t('about.exams.title_accent')}</span></h2>
            <p className="animate-fadeInUp">{t('about.exams.subtitle')}</p>
          </div>
          <div className="card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '16px' }}>
            {examsCovered.map((exam, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '12px', padding: '12px', background: 'var(--bg-tertiary)', borderRadius: '12px' }}>
                <CheckCircle size={20} style={{ color: 'var(--success)', flexShrink: 0 }} />
                <span style={{ fontWeight: 500, fontSize: '0.95rem' }}>{exam}</span>
              </div>
            ))}
          </div>
        </section>

        {/* AI Solutions */}
        <section style={{ marginBottom: '80px' }}>
          <div className="section-header text-center" style={{ textAlign: 'center', marginBottom: '40px' }}>
            <h2 className="animate-fadeInUp">{t('about.solutions.title_line1')} <span className="text-gradient">{t('about.solutions.title_accent')}</span></h2>
            <p className="animate-fadeInUp">{t('about.solutions.subtitle')}</p>
          </div>
          <div className="grid-3">
            {features.map((f, i) => (
              <div key={i} className="card-colored animate-fadeInUp" style={{ 
                animationDelay: `${0.7 + i * 0.1}s`,
                background: i === 0 ? 'var(--bg-accent-green)' : i === 1 ? 'var(--bg-accent-saffron)' : 'var(--primary-bg)',
                border: '1px solid var(--border-color)'
              }}>
                <div className="feature-icon" style={{ background: 'white' }}>
                  {f.icon}
                </div>
                <h4>{f.title}</h4>
                <p style={{ color: 'var(--text-primary)', opacity: 0.8 }}>{f.desc}</p>
              </div>
            ))}
          </div>
        </section>

        {/* CTA */}
        <section className="cta-section animate-fadeInUp" style={{ borderRadius: 'var(--radius-xl)', padding: '60px', textAlign: 'center', background: 'var(--gradient-primary)', color: 'white' }}>
          <h2 style={{ color: 'white', marginBottom: '16px' }}>{t('about.cta.title')}</h2>
          <p style={{ color: 'white', opacity: 0.9, marginBottom: '32px', maxWidth: '600px', margin: '0 auto 32px' }}>
            {t('about.cta.subtitle')}
          </p>
          <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', flexWrap: 'wrap' }}>
             <Link to="/mock-test" className="btn btn-lg" style={{ background: 'white', color: 'var(--primary)' }}>{t('home.cta')}</Link>
             <Link to="/dashboard" className="btn btn-lg btn-outline" style={{ color: 'white', borderColor: 'white' }}>{t('nav.dashboard')}</Link>
          </div>
        </section>
      </div>
    </div>
  );
}
