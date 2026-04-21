import { useState, useEffect } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { loginWithEmail, loginWithGoogle, loginWithGoogleRedirect, registerWithEmail } from '../services/firebase';
import { Mail, Lock, Eye, EyeOff, Smartphone } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import logo from '../assets/logo.png';
import './Auth.css';

export default function Login() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const [isRegister, setIsRegister] = useState(searchParams.get('mode') === 'signup');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      navigate('/dashboard');
    }
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      if (isRegister) {
        await registerWithEmail(email, password);
        setSuccess(t('auth.successRegister') || 'Account created successfully! Redirecting...');
      } else {
        await loginWithEmail(email, password);
        setSuccess(t('auth.successLogin') || 'Login successful! Welcome back.');
      }
      setTimeout(() => navigate('/dashboard'), 1000);
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  const handleGoogle = async () => {
    setError('');
    setLoading(true);
    try {
      // Only use redirect for actual Flutter WebViews (has FlutterDownload bridge)
      const isFlutterWebView = window.FlutterDownload !== undefined;
      if (isFlutterWebView) {
        await loginWithGoogleRedirect();
        return;
      }
      // Use popup for all normal browsers (desktop & mobile)
      await loginWithGoogle();
      setSuccess(t('auth.successGoogle') || 'Google se login ho gaya! Redirect ho raha hai...');
      setTimeout(() => navigate('/dashboard'), 1000);
    } catch (err) {
      let msg = err.message;
      if (err.code === 'auth/popup-blocked') {
        msg = 'Browser ne popup block kiya. Chrome settings mein popup allow karein ya Email/Password se login karein.';
      } else if (err.code === 'auth/popup-closed-by-user') {
        msg = 'Google sign-in cancel hua. Phir se try karein.';
      } else if (err.code === 'auth/unauthorized-domain') {
        msg = 'Yeh domain Firebase mein authorized nahi hai. Firebase Console > Authentication > Settings > Authorized domains mein "localhost" add karein.';
      } else if (err.code === 'auth/operation-not-allowed') {
        msg = 'Google Sign-In enable nahi hai. Firebase Console > Authentication > Sign-in methods mein Google enable karein.';
      }
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page" id="login-page">
      <div className="auth-card animate-fadeInUp">
        <div className="auth-header">
          <Link to="/" className="auth-logo">
            <img src={logo} alt="Sarkari Exam AI" className="auth-logo-img" />
          </Link>
          <h1>{isRegister ? t('auth.signup') : t('auth.loginTitle')}</h1>
          <p>{isRegister ? t('auth.signupSubtitle') : t('auth.loginSubtitle')}</p>
        </div>

        {error && <div className="auth-error">{error}</div>}
        {success && <div className="auth-success" style={{ background: 'var(--bg-accent-green)', color: 'var(--accent-green)', padding: '12px', borderRadius: '8px', marginBottom: '20px', fontSize: '0.9rem', textAlign: 'center', border: '1px solid var(--accent-green)' }}>{success}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="input-group">
            <label>{t('auth.email')}</label>
            <div className="input-with-icon">
              <Mail size={18} />
              <input type="email" value={email} onChange={e => setEmail(e.target.value)}
                placeholder="you@example.com" required />
            </div>
          </div>

          <div className="input-group">
            <label>{t('auth.password')}</label>
            <div className="input-with-icon">
              <Lock size={18} />
              <input type={showPw ? 'text' : 'password'} value={password}
                onChange={e => setPassword(e.target.value)} placeholder="••••••••" required />
              <button type="button" className="pw-toggle" onClick={() => setShowPw(!showPw)}>
                {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-lg auth-submit" disabled={loading}>
            {loading ? t('common.loading') : (isRegister ? t('auth.signup') : t('auth.login'))}
          </button>
        </form>

        <div className="auth-divider"><span>OR</span></div>

        <button className="btn btn-secondary btn-lg google-btn" onClick={handleGoogle}>
          <svg width="18" height="18" viewBox="0 0 24 24"><path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4"/><path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/><path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/><path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/></svg>
          {t('auth.googleLogin')}
        </button>

        <div style={{ marginTop: '24px', textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
          {isRegister ? t('auth.alreadyHaveAccount') : t('auth.dontHaveAccount')}
          <button 
            type="button" 
            onClick={() => setIsRegister(!isRegister)}
            style={{ marginLeft: '8px', background: 'none', border: 'none', color: 'var(--primary)', fontWeight: 600, cursor: 'pointer', textDecoration: 'underline' }}
          >
            {isRegister ? t('auth.login') : t('auth.signupFree')}
          </button>
        </div>

      </div>
    </div>
  );
}
