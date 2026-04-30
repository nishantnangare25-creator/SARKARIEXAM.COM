import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { EXAMS } from '../utils/constants';
import { BookOpen, Brain, FileText, BarChart3, Users, GraduationCap, ArrowRight, Sparkles, Star, Zap, Shield, Target, TrendingUp } from 'lucide-react';
import './Landing.css';

export default function Landing() {
  const { t } = useTranslation();

  const features = [
    { icon: <BookOpen size={24} />, title: t('home.feature1Title'), desc: t('home.feature1Desc'), color: 'purple', link: '/study-planner' },
    { icon: <Brain size={24} />, title: t('home.feature2Title'), desc: t('home.feature2Desc'), color: 'green', link: '/mock-test' },
    { icon: <FileText size={24} />, title: t('home.feature3Title'), desc: t('home.feature3Desc'), color: 'orange', link: '/past-paper' },
    { icon: <GraduationCap size={24} />, title: t('home.feature4Title'), desc: t('home.feature4Desc'), color: 'blue', link: '/notes' },
    { icon: <BarChart3 size={24} />, title: t('home.feature5Title'), desc: t('home.feature5Desc'), color: 'pink', link: '/analytics' },
    { icon: <Users size={24} />, title: t('home.feature6Title'), desc: t('home.feature6Desc'), color: 'purple', link: '/peer-matching' },
    { icon: <Zap size={24} />, title: t('home.feature7Title'), desc: t('home.feature7Desc'), color: 'orange', link: '/tutor' },
    { icon: <Target size={24} />, title: t('home.feature8Title'), desc: t('home.feature8Desc'), color: 'green', link: '/pyq-pdfs' },
    { icon: <Shield size={24} />, title: t('home.feature9Title'), desc: t('home.feature9Desc'), color: 'blue', link: '/pyqs-mock-test' },
  ];


  const stats = [
    { value: '50K+', label: t('home.stats1'), icon: <Users size={20} /> },
    { value: '1M+', label: t('home.stats2'), icon: <Brain size={20} /> },
    { value: '7+', label: t('home.stats3'), icon: <Star size={20} /> },
    { value: '5+', label: t('home.stats4'), icon: <Sparkles size={20} /> },
  ];

  return (
    <main className="landing" id="landing-page">
      {/* Hero Section */}
      <section className="hero" aria-labelledby="hero-title">
        <div className="hero-bg" aria-hidden="true">
          <div className="hero-orb orb-1"></div>
          <div className="hero-orb orb-2"></div>
          <div className="hero-orb orb-3"></div>
        </div>
        <div className="hero-content">
          <div className="hero-badge animate-fadeInUp">
            <Sparkles size={14} aria-hidden="true" /> {t('home.heroBadge')}
          </div>

          
          <div className="hero-main-wrapper">
            {/* Floating Glassmorphism Cards */}
            <div className="floating-card fc-1">
              <Brain size={18} className="fc-icon purple" />
              <span>{t('home.floating.analyzing')}</span>
            </div>
            <div className="floating-card fc-2">
              <Target size={18} className="fc-icon orange" />
              <span>{t('home.floating.focus')}</span>
            </div>
            <div className="floating-card fc-3">
              <TrendingUp size={18} className="fc-icon green" />
              <span>{t('home.floating.improvement')}</span>
            </div>

            <h1 id="hero-title" className="hero-title animate-fadeInUp">
              {t('home.hero_line1')} <span>{t('home.hero_accent')}</span> {t('home.hero_line2')}
            </h1>
            <p className="hero-subtitle animate-fadeInUp">
              {t('home.subtitle')}
            </p>
            <div className="hero-actions animate-fadeInUp">
              <Link to="/mock-test" className="btn-hero-primary" aria-label={t('home.cta')}>
                {t('home.cta')}
              </Link>
              <a href="#features" className="btn-hero-secondary" aria-label={t('home.ctaSecondary')}>
                {t('home.ctaSecondary')}
              </a>
            </div>
          </div>
        </div>

        {/* Stats Bar */}
        <div className="stats-bar animate-fadeInUp" role="region" aria-label="Platform Statistics">
          {stats.map((stat, i) => (
            <div key={i} className="stat-item">
              <div className="stat-icon" aria-hidden="true">{stat.icon}</div>
              <div className="stat-value">{stat.value}</div>
              <div className="stat-label">{stat.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="features-section">
        <div className="section-header animate-fadeInUp">
          <h2 id="features-title">{t('home.precisionTitle')}</h2>
          <p>{t('home.precisionDesc')}</p>
        </div>
        <div className="features-grid">
          {features.map((f, i) => (
            <Link to={f.link} key={i} className={`feature-card animate-fadeInUp`} style={{ animationDelay: `${i * 0.1}s` }} aria-label={`Explore ${f.title}`}>
              <div className={`feature-icon ${f.color}`} aria-hidden="true">{f.icon}</div>
              <h3>{f.title}</h3>
              <p>{f.desc}</p>
              <span className="feature-arrow"><ArrowRight size={16} aria-hidden="true" /></span>
            </Link>
          ))}
        </div>
      </section>

      {/* Exams Section */}
      <section className="exams-section">
        <div className="section-header animate-fadeInUp">
          <h2 id="exams-title">{t('home.examTitle')}</h2>
          <p>{t('home.examSubtitle')}</p>
        </div>
        <div className="exams-grid">
          {EXAMS.map((exam, i) => (
            <div key={exam.id} className="exam-card animate-fadeInUp" style={{ animationDelay: `${i * 0.08}s` }}>
              <span className="exam-icon" aria-hidden="true">{exam.icon}</span>
              <span className="exam-name">{exam.name}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="cta-section" aria-labelledby="cta-title">
        <div className="cta-content animate-fadeInUp">
          <h2 id="cta-title">{t('home.ctaTitle')}</h2>
          <p>{t('home.ctaDesc')}</p>
        </div>
      </section>


      {/* Footer */}
      <footer className="landing-footer">
        <div className="footer-content">
          <div className="footer-brand">
            <span className="logo-text">Sarkari<span className="logo-ai">AI</span></span>
            <p>{t('footer.tagline')}</p>
          </div>
          <nav className="footer-links" aria-label={t('footer.platform')}>
            <h4>{t('footer.platform')}</h4>
            <Link to="/study-planner">{t('nav.studyPlanner')}</Link>
            <Link to="/mock-test">{t('nav.mockTest')}</Link>
            <Link to="/notes">{t('nav.notes')}</Link>
            <Link to="/analytics">{t('nav.analytics')}</Link>
          </nav>
          <nav className="footer-links" aria-label={t('footer.exams')}>
            <h4>{t('footer.exams')}</h4>
            <span>UPSC Civil Services</span>
            <span>MPSC</span>
            <span>SSC CGL/CHSL</span>
            <span>Banking IBPS/SBI</span>
          </nav>
          <nav className="footer-links" aria-label={t('footer.company')}>
            <h4>{t('footer.company')}</h4>
            <Link to="/about">{t('footer.about')}</Link>
            <a href="#">{t('footer.contact')}</a>
            <Link to="/privacy">{t('footer.privacy')}</Link>
            <a href="#">{t('footer.terms')}</a>
          </nav>
        </div>
        <div className="footer-bottom">
          <p>{t('footer.rights')}</p>
        </div>
      </footer>
    </main>
  );
}
