import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import LanguageSelector from './components/LanguageSelector';
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
import './i18n';
import './index.css';

function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <Router>
      <Navbar onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <Routes>
        <Route path="/" element={<Landing />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard onToggleSidebar={() => setSidebarOpen(true)} />} />
        <Route path="/study-planner" element={<StudyPlanner />} />
        <Route path="/mock-test" element={<MockTest />} />
        <Route path="/pyqs-mock-test" element={<PYQSMockTest />} />
        <Route path="/pyq-pdfs" element={<PYQPdfs />} />
        <Route path="/pyq-practice" element={<PYQPractice />} />
        <Route path="/past-papers" element={<PastPaperAnalyzer />} />
        <Route path="/notes" element={<NotesGenerator />} />
        <Route path="/tutor" element={<InteractiveTutor />} />
        <Route path="/analytics" element={<Analytics />} />
        <Route path="/forum" element={<Forum />} />
        <Route path="/peer-matching" element={<PeerMatching />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="/about" element={<AboutUs />} />
        <Route path="/privacy" element={<PrivacyPolicy />} />
        <Route path="/blog" element={<Blog />} />
        <Route path="/blog/:id" element={<BlogPost />} />
      </Routes>
      <MobileBottomNav />
    </Router>
  );
}

export default function App() {
  const [languageChosen, setLanguageChosen] = useState(
    () => localStorage.getItem('languageSelected') === 'true'
  );

  // Show language selection splash on first visit
  if (!languageChosen) {
    return (
      <LanguageSelector onSelect={() => setLanguageChosen(true)} />
    );
  }

  return (
    <AuthProvider>
      <AppLayout />
    </AuthProvider>
  );
}
