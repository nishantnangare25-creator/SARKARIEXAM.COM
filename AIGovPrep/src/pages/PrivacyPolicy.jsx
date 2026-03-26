import React from 'react';
import { Shield, Lock, Eye, FileText, UserCheck, Mail } from 'lucide-react';

export default function PrivacyPolicy() {
  const sections = [
    {
      icon: <Eye className="text-blue" />,
      title: "Data Collection",
      content: "We collect information you provide directly to us, such as when you create an account, update your profile, or use our AI tools. This includes your name, email address, language preferences, and performance data from mock tests."
    },
    {
      icon: <Lock className="text-saffron" />,
      title: "How We Use Data",
      content: "Your data is primarily used to personalize your learning experience. Our AI models analyze your performance to identify weak areas and generate customized study plans. We also use aggregated, non-identifiable data to improve our algorithms."
    },
    {
      icon: <Shield className="text-green" />,
      title: "Data Security",
      content: "We implement industry-standard security measures to protect your personal information. This includes encryption for data at rest and in transit, regular security audits, and strict access controls for our infrastructure."
    },
    {
      icon: <UserCheck className="text-blue" />,
      title: "Your Rights",
      content: "You have the right to access, correct, or delete your personal information at any time. You can also export your performance data. We believe in complete transparency and give you full control over your digital footprint on our platform."
    }
  ];

  return (
    <div className="page-wrapper animate-fadeIn">
      <div className="page-container" style={{ maxWidth: '800px' }}>
        <section className="page-header" style={{ marginBottom: '40px' }}>
          <h1 className="animate-fadeInUp">Privacy Policy</h1>
          <p className="animate-fadeInUp" style={{ animationDelay: '0.1s' }}>
            Last Updated: March 25, 2026. Your trust is our most valuable asset.
          </p>
        </section>

        <div className="card-glass animate-fadeInUp" style={{ animationDelay: '0.2s', padding: '32px', marginBottom: '40px' }}>
          <div className="alert alert-info">
             <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                <Shield size={20} />
                <span><strong>Commitment to Privacy:</strong> Sarkari AI is built with privacy-by-design principles. We never sell your data to third parties.</span>
             </div>
          </div>
          
          <div className="text-answer-card">
            <p>
              At Sarkari AI, we understand that your data is private. This policy outlines how we handle your information with the transparency and respect you deserve.
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

            <h3>Cookies and Tracking</h3>
            <p>
              We use essential cookies to maintain your session and remember your preferences. We also use analytics cookies to understand how users interact with our platform, which helps us improve the user experience.
            </p>

            <h3>Third-Party Services</h3>
            <p>
              We use trusted third-party services for essential functions like authentication (Firebase/Google Auth) and AI processing (OpenRouter/OpenAI). These providers only receive the data necessary to perform their specific services.
            </p>

            <div className="card-colored" style={{ background: 'var(--bg-tertiary)', marginTop: '40px' }}>
               <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: '12px' }}>
                  <Mail className="text-blue" size={20} />
                  <h4 style={{ margin: 0 }}>Contact Us</h4>
               </div>
               <p style={{ marginBottom: 0 }}>
                 If you have any questions about our privacy practices or wish to exercise your data rights, please contact our privacy team at <strong>privacy@sarkaraiai.com</strong>.
               </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
