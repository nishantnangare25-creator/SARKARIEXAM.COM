import React, { useState, useEffect } from 'react';
import { HashRouter as Router, Routes, Route, useLocation, Link, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import MobileBottomNav from './components/MobileBottomNav';
import Landing from './pages/Landing';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import StudyPlanner from './pages/StudyPlanner';
import MockTest from './pages/MockTest';
import PYQSMockTest from './pages/PYQSMockTest';
import PYQPdfs from './pages/PYQPdfs';
import PYQPractice from './pages/PYQPractice';
import PastPaperAnalyzer from './pages/PastPaperAnalyzer';
import NotesGenerator from './pages/NotesGenerator';
import InteractiveTutor from './pages/InteractiveTutor';
import Analytics from './pages/Analytics';
import Forum from './pages/Forum';
import PeerMatching from './pages/PeerMatching';
import Settings from './pages/Settings';
import AboutUs from './pages/AboutUs';
import PrivacyPolicy from './pages/PrivacyPolicy';
import Blog from './pages/Blog';
import BlogPost from './pages/BlogPost';
import CurrentAffairs from './pages/CurrentAffairs';
import './i18n';
import './index.css';

class PageErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }
  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }
  componentDidCatch(error, errorInfo) {
    console.error('[Global Error]', error, errorInfo);
  }
  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '40px 20px', textAlign: 'center', background: '#f8fafc', minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
          <h1 style={{ marginBottom: '16px', color: '#1e293b' }}>Oops! Something went wrong.</h1>
          <p style={{ color: '#64748b', marginBottom: '24px', maxWidth: '400px' }}>The page encountered an error. Please try refreshing or going back to the dashboard.</p>
          <button 
            onClick={() => window.location.href = '/'}
            style={{ padding: '12px 24px', background: '#2563eb', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold' }}
          >
            Back to Home
          </button>
          {process.env.NODE_ENV === 'development' && (
            <pre style={{ marginTop: '32px', textAlign: 'left', padding: '16px', background: '#fee2e2', color: '#991b1b', borderRadius: '8px', overflow: 'auto', maxWidth: '90%' }}>
              {this.state.error?.toString()}
            </pre>
          )}
        </div>
      );
    }
    return this.props.children;
  }
}

function AppLayout({ sidebarOpen, setSidebarOpen }) {
  const location = useLocation();

  useEffect(() => {
    // Signal Flutter that SPA route change is finished and UI is ready
    if (window.FlutterPageReady) {
      console.log('[SPA] Route changed, signaling ready:', location.pathname);
      window.FlutterPageReady.postMessage('ready');
    }
    // Also scroll to top on route change
    window.scrollTo(0, 0);
  }, [location]);

  const isPublicPage = ['/', '/login', '/about', '/privacy'].includes(location.pathname);
  const isImmersivePage = ['/tutor'].includes(location.pathname);
  const showSidebar = !isPublicPage && !isImmersivePage;

  return (
    <>
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />
      {showSidebar && (
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      )}
      <PageErrorBoundary>
        <Routes>
          <Route path="/"               element={<Landing />} />
          <Route path="/login"          element={<Login />} />
          <Route path="/dashboard"      element={<Dashboard onToggleSidebar={() => setSidebarOpen(true)} />} />
          <Route path="/study-planner"  element={<StudyPlanner />} />
          <Route path="/mock-test"      element={<MockTest />} />
          <Route path="/pyqs-mock-test" element={<PYQSMockTest />} />
          <Route path="/pyq-pdfs"       element={<PYQPdfs />} />
          <Route path="/pyq-practice"   element={<PYQPractice />} />
          <Route path="/past-papers"    element={<PastPaperAnalyzer />} />
          <Route path="/notes"          element={<NotesGenerator />} />
          <Route path="/tutor"          element={<InteractiveTutor />} />
          <Route path="/analytics"      element={<Analytics />} />
          <Route path="/forum"          element={<Forum />} />
          <Route path="/peer-matching"  element={<PeerMatching />} />
          <Route path="/settings"       element={<Settings />} />
          <Route path="/about"          element={<AboutUs />} />
          <Route path="/privacy"        element={<PrivacyPolicy />} />
          <Route path="/blog"           element={<Blog />} />
          <Route path="/current-affairs"  element={<CurrentAffairs />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </PageErrorBoundary>
      {/* Mobile bottom nav: only shown on phones (<768px), hidden on tablet/desktop */}
      {showSidebar && <MobileBottomNav />}
    </>
  );
}

export default function App() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    // --- GLOBAL ERROR REPORTING FOR FLUTTER ---
    const handleError = (error) => {
      const msg = error.message || 'Unknown JS Error';
      console.error('[CRASH]', msg);
      if (window.FlutterPageError) {
        window.FlutterPageError.postMessage(msg);
      }
    };

    window.onerror = (msg, url, line, col, error) => handleError(error || { message: msg });
    window.onunhandledrejection = (event) => handleError(event.reason || { message: 'Promise rejection' });

    console.log('[SPA] App mounted');
  }, []);

  return (
    <Router>
      <AuthProvider>
        <AppLayout sidebarOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
      </AuthProvider>
    </Router>
  );
}
