import React from 'react';
import { useTranslation } from 'react-i18next';
import { Target, Brain, Sparkles, Rocket, Shield, Users, Landmark, FileText, BarChart3 } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function AboutUs() {
  const { t } = useTranslation();

  const values = [
    {
      icon: <Target className="purple" size={24} />,
      title: "Our Mission",
      desc: "To democratize high-quality government exam preparation for every aspirant in India through the power of Artificial Intelligence."
    },
    {
      icon: <Sparkles className="orange" size={24} />,
      title: "AI Innovation",
      desc: "We leverage state-of-the-art LLMs and data analytics to provide personalized coaching that adapts to each student's unique learning curve."
    },
    {
      icon: <Rocket className="green" size={24} />,
      title: "Future Vision",
      desc: "Building a future where technology bridges the gap between dreams and success, making premium education accessible to all."
    }
  ];

  const features = [
    { icon: <Brain />, title: "Personalized AI Tutor", desc: "Interactive AI that understands your weak points and helps you master complex topics." },
    { icon: <FileText />, title: "Smart PYQ Analysis", desc: "Automated analysis of previous year questions to identify patterns and high-yield topics." },
    { icon: <BarChart3 />, title: "Data-Driven Insights", desc: "Detailed performance tracking and predictive analytics to measure exam readiness." }
  ];

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-container">
        {/* Hero Section */}
        <section className="page-header text-center" style={{ textAlign: 'center', marginBottom: '60px' }}>
          <div className="badge badge-primary animate-fadeInUp" style={{ marginBottom: '16px' }}>About Sarkari AI</div>
          <h1 className="animate-fadeInUp" style={{ animationDelay: '0.1s', marginBottom: '20px' }}>
            Empowering Bharat's <span className="text-gradient">Future Leaders</span>
          </h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.2s', maxWidth: '800px', margin: '0 auto', fontSize: '1.2rem' }}>
            We are on a mission to revolutionize how India prepares for competitive exams. By combining pedagogical expertise with cutting-edge AI, we make success achievable for everyone.
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
              <h2 style={{ marginBottom: '20px' }}>The Problem We're Solving</h2>
              <p style={{ marginBottom: '16px', fontSize: '1.05rem' }}>
                Every year, millions of students across India spend countless hours and significant resources preparing for government exams like UPSC, SSC, and Banking. From mastering the rigorous <strong>UPSC syllabus</strong> to finding reliable <strong>UPSC examexamples</strong>, the journey is demanding. Similarly, aspirants looking for comprehensive <strong>MPSC syllabus</strong> coverage or accessible <strong>MPSC online classes</strong> often find that high-quality coaching is expensive and geographically restricted.
              </p>
              <p style={{ marginBottom: '24px', fontSize: '1.05rem' }}>
                Sarkari AI was born out of a simple idea: <strong>Technology should be the great equalizer.</strong> Whether you are preparing for the ultimate <strong>AI civil services exam</strong>, exploring <strong>MPSC online classes free</strong> of high costs, hunting for a quality <strong>MPSC examsc pyq book</strong>, or tackling <strong>UPMPSC</strong>, we've built a platform that provides the same level of personalized attention as a private tutor. It acts as your dedicated <strong>AI exam</strong> coach at a fraction of the cost.
              </p>
              <div style={{ display: 'flex', gap: '16px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Shield size={20} className="text-blue" />
                  <span style={{ fontWeight: 600 }}>Trusted by 50K+ Students</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Landmark size={20} className="text-saffron" />
                  <span style={{ fontWeight: 600 }}>15+ Exams Covered</span>
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

        {/* AI Solutions */}
        <section style={{ marginBottom: '80px' }}>
          <div className="section-header text-center" style={{ textAlign: 'center', marginBottom: '40px' }}>
            <h2 className="animate-fadeInUp">Our <span className="text-gradient">AI-Driven</span> Solutions</h2>
            <p className="animate-fadeInUp">Tools designed to accelerate your learning and maximize performance.</p>
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
          <h2 style={{ color: 'white', marginBottom: '16px' }}>Ready to Start Your Journey?</h2>
          <p style={{ color: 'white', opacity: 0.9, marginBottom: '32px', maxWidth: '600px', margin: '0 auto 32px' }}>
            Join thousands of aspirants who are already using AI to transform their preparation and achieve their goals.
          </p>
          <div style={{ display: 'flex', gap: '16px', justifyContent: 'center' }}>
             <Link to="/mock-test" className="btn btn-lg" style={{ background: 'white', color: 'var(--primary)' }}>Start Mock Test</Link>
             <Link to="/dashboard" className="btn btn-lg btn-outline" style={{ color: 'white', borderColor: 'white' }}>Go to Dashboard</Link>
          </div>
        </section>
      </div>
    </div>
  );
}
