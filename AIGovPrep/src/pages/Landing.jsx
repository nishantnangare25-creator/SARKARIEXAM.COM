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
    { icon: <FileText size={24} />, title: t('home.feature3Title'), desc: t('home.feature3Desc'), color: 'orange', link: '/past-papers' },
    { icon: <GraduationCap size={24} />, title: t('home.feature4Title'), desc: t('home.feature4Desc'), color: 'blue', link: '/notes' },
    { icon: <BarChart3 size={24} />, title: t('home.feature5Title'), desc: t('home.feature5Desc'), color: 'pink', link: '/analytics' },
    { icon: <Users size={24} />, title: t('home.feature6Title'), desc: t('home.feature6Desc'), color: 'purple', link: '/peer-matching' },
    { icon: <Zap size={24} />, title: 'AI Riya Tutor', desc: 'Get instant personalized lessons on any topic in your own language using our AI tutor Riya.', color: 'orange', link: '/tutor' },
    { icon: <Target size={24} />, title: 'PYQs Practice', desc: 'Practice previous year questions from real government exam papers with AI-powered quiz mode.', color: 'green', link: '/pyq-pdfs' },
    { icon: <Shield size={24} />, title: 'PYQs Mock Test', desc: 'Take full-length mock tests based on real PYQ patterns to simulate actual exam conditions.', color: 'blue', link: '/pyqs-mock-test' },
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
            <Sparkles size={14} aria-hidden="true" /> AI-Powered Exam Success
          </div>
          
          <div className="hero-main-wrapper">
            {/* Floating Glassmorphism Cards */}
            <div className="floating-card fc-1">
              <Brain size={18} className="fc-icon purple" />
              <span>AI Analyzing weak topics...</span>
            </div>
            <div className="floating-card fc-2">
              <Target size={18} className="fc-icon orange" />
              <span>Focus Area: Indian Polity</span>
            </div>
            <div className="floating-card fc-3">
              <TrendingUp size={18} className="fc-icon green" />
              <span>+15% Improvement</span>
            </div>

            <h1 id="hero-title" className="hero-title animate-fadeInUp">
              Your <span>AI-Powered</span> Exam<br />Preparation Partner
            </h1>
            <p className="hero-subtitle animate-fadeInUp">
              <span className="subtitle-highlight">Stop Guessing. Start Mastering.</span> Get data-driven insights and personalized AI coaching to crack India's toughest exams.
            </p>
            <div className="hero-actions animate-fadeInUp">
              <Link to="/mock-test" className="btn-hero-primary" aria-label="Start your first mock test">
                Start Learning Free
              </Link>
              <a href="#features" className="btn-hero-secondary" aria-label="Learn more about our AI features">
                Explore Features
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
      <section className="features-section" id="features" aria-labelledby="features-title">
        <div className="section-header animate-fadeInUp">
          <h2 id="features-title">Master Every Subject with <span className="text-gradient">Precision AI</span></h2>
          <p>Don't just study—study smart with tools designed by AI experts for Indian aspirants.</p>
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
      <section className="exams-section" id="exams" aria-labelledby="exams-title">
        <div className="section-header animate-fadeInUp">
          <h2 id="exams-title">{t('home.examTitle')}</h2>
          <p>Tailored AI optimization for 15+ major Indian competitive exams.</p>
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

      {/* CTA Section */}
      <section className="cta-section" aria-labelledby="cta-title">
        <div className="cta-content animate-fadeInUp">
          <h2 id="cta-title">Ready to Outperform the Competition?</h2>
          <p>Join 50,000+ successful aspirants who use AI to stay ahead of the curve.</p>
        </div>
      </section>

      {/* SEO Content Section */}
      <section className="seo-section" aria-label="About Sarkari ExamAI" style={{ padding: '80px 20px', background: 'var(--bg-secondary)', textAlign: 'center', borderTop: '1px solid var(--border-light)' }}>
        <div className="animate-fadeInUp" style={{ maxWidth: 900, margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '24px', color: 'var(--text-secondary)', lineHeight: '1.8', fontSize: '1.05rem' }}>
          <p style={{ color: 'var(--text-primary)', fontSize: '1.15rem' }}>
            <strong>Welcome to India’s most advanced AI-powered government exam preparation platform.</strong> Prepare for UPSC, NDA, SSC, Banking, Railway, and all Sarkari exams with smart AI tools.
          </p>
          <p>
            Practice UPSC prelims question papers, take daily 10-minute mock tests, and improve your performance with AI-based analytics. Get personalized study plans, topic-wise PYQs, and real-time current affairs updates.
          </p>
          <p>
            Our platform supports Hindi, English, and multiple Indian languages so every student can learn in their preferred language. Start your preparation today and boost your chances of success in government exams.
          </p>
        </div>
      </section>

      {/* Footer */}
      <footer className="landing-footer">
        <div className="footer-content">
          <div className="footer-brand">
            <span className="logo-text">Sarkari<span className="logo-ai">AI</span></span>
            <p>India's Leading AI-Powered Exam Preparation Platform</p>
          </div>
          <nav className="footer-links" aria-label="Platform Links">
            <h4>Platform</h4>
            <Link to="/study-planner">Study Planner</Link>
            <Link to="/mock-test">Mock Tests</Link>
            <Link to="/notes">Notes Generator</Link>
            <Link to="/analytics">Analytics</Link>
          </nav>
          <nav className="footer-links" aria-label="Exam Categories">
            <h4>Exams</h4>
            <span>UPSC Civil Services</span>
            <span>MPSC</span>
            <span>SSC CGL/CHSL</span>
            <span>Banking IBPS/SBI</span>
          </nav>
          <nav className="footer-links" aria-label="Company Links">
            <h4>Company</h4>
            <Link to="/about">About Us</Link>
            <a href="#">Contact</a>
            <Link to="/privacy">Privacy Policy</Link>
            <a href="#">Terms of Service</a>
          </nav>
        </div>
        <div className="footer-bottom">
          <p>© 2026 Sarkari AI. All rights reserved. Built with ❤️ for Bharat's Future Leaders.</p>
        </div>
      </footer>
    </main>
  );
}
