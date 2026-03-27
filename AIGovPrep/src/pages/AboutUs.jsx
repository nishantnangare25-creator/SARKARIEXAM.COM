import React from 'react';
import { useTranslation } from 'react-i18next';
import { Target, Brain, Sparkles, Rocket, Shield, Users, Landmark, FileText, BarChart3, CheckCircle } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function AboutUs() {
  const { t } = useTranslation();

  const values = [
    {
      icon: <Target className="purple" size={24} />,
      title: "Our Mission",
      desc: "To democratize high-quality government exam preparation for every aspirant in India — from mastering the complete UPSC syllabus to cracking MPSC, SSC, and Railway exams — through the power of Artificial Intelligence."
    },
    {
      icon: <Sparkles className="orange" size={24} />,
      title: "AI Innovation",
      desc: "We leverage state-of-the-art AI models and real UPSC exam samples to deliver personalized coaching that adapts to each student's unique learning pace and knowledge gaps."
    },
    {
      icon: <Rocket className="green" size={24} />,
      title: "Future Vision",
      desc: "Building a future where world-class exam coaching — including MPSC online classes and live doubt sessions — is accessible to every aspirant, regardless of location or financial background."
    }
  ];

  const features = [
    { icon: <Brain />, title: "Personalized AI Tutor", desc: "An interactive AI tutor that identifies your weak areas and guides you through complex UPSC syllabus topics with clarity and depth." },
    { icon: <FileText />, title: "Smart PYQ Analysis", desc: "AI-powered analysis of UPSC exam samples and previous year questions to identify high-yield patterns and predict likely exam topics." },
    { icon: <BarChart3 />, title: "Data-Driven Insights", desc: "Detailed performance tracking and predictive analytics to measure your exam readiness and optimize your preparation strategy." }
  ];

  const examsCovered = [
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
          <div className="badge badge-primary animate-fadeInUp" style={{ marginBottom: '16px' }}>About Sarkari AI</div>
          <h1 className="animate-fadeInUp" style={{ animationDelay: '0.1s', marginBottom: '20px' }}>
            Empowering Bharat's <span className="text-gradient">Future Leaders</span>
          </h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.2s', maxWidth: '800px', margin: '0 auto', fontSize: '1.2rem' }}>
            We are on a mission to revolutionize how India prepares for competitive exams. By combining deep pedagogical expertise with cutting-edge AI, we make exam success achievable for everyone — from a small town to a big city.
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
                Every year, millions of aspirants across India invest years of effort and significant resources preparing for government exams. From navigating the vast <strong>UPSC syllabus</strong> — covering History, Geography, Polity, Economy, and Current Affairs — to finding authentic <strong>UPSC exam samples</strong> for practice, the journey is both demanding and expensive.
              </p>
              <p style={{ marginBottom: '16px', fontSize: '1.05rem' }}>
                For state-level aspirants, the challenge is no different. Finding reliable and structured <strong>MPSC online classes</strong> that are also affordable is a struggle for most students outside major cities. High-quality coaching has historically been locked behind expensive institute fees and geography.
              </p>
              <p style={{ marginBottom: '24px', fontSize: '1.05rem' }}>
                <strong>Sarkari AI was built to change this.</strong> We combine the rigor of expert-curated content with the personalization of AI — giving every student access to a smart study planner, real UPSC exam samples for practice, and MPSC online classes-style interactive learning, all in one platform and completely accessible from any device.
              </p>
              <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Shield size={20} className="text-blue" />
                  <span style={{ fontWeight: 600 }}>Trusted by 50K+ Students</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Landmark size={20} className="text-saffron" />
                  <span style={{ fontWeight: 600 }}>15+ Exams Covered</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Users size={20} className="text-green" />
                  <span style={{ fontWeight: 600 }}>12 Indian Languages</span>
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
            <h2 className="animate-fadeInUp">Exams We <span className="text-gradient">Cover</span></h2>
            <p className="animate-fadeInUp">Comprehensive preparation for India's most sought-after government examinations.</p>
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
          <h2 style={{ color: 'white', marginBottom: '16px' }}>Start Your Journey Today</h2>
          <p style={{ color: 'white', opacity: 0.9, marginBottom: '32px', maxWidth: '600px', margin: '0 auto 32px' }}>
            Join 50,000+ aspirants who are already using AI-powered UPSC syllabus guides, real UPSC exam samples, and MPSC online classes to achieve their government job dreams.
          </p>
          <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', flexWrap: 'wrap' }}>
             <Link to="/mock-test" className="btn btn-lg" style={{ background: 'white', color: 'var(--primary)' }}>Try Free Mock Test</Link>
             <Link to="/dashboard" className="btn btn-lg btn-outline" style={{ color: 'white', borderColor: 'white' }}>Go to Dashboard</Link>
          </div>
        </section>
      </div>
    </div>
  );
}
