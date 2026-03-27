import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import { logout } from '../services/firebase';
import {
  LayoutDashboard, Brain, Target, BookOpen, FileText,
  GraduationCap, Bot, BarChart3, MessageSquare, Users,
  Settings, X, Newspaper, Zap, FileDown, LogOut
} from 'lucide-react';
import './Sidebar.css';

const navSections = [
  {
    label: 'Main',
    items: [
      { path: '/dashboard', icon: LayoutDashboard, label: 'nav.dashboard', iconColor: 'blue' },
      { path: '/blog', icon: Newspaper, label: 'Blog', iconColor: 'green' },
    ]
  },
  {
    label: 'Tests',
    items: [
      { path: '/mock-test', icon: Brain, label: 'nav.mockTest', iconColor: 'blue' },
      { path: '/pyqs-mock-test', icon: Target, label: 'nav.pyqsMockTest', iconColor: 'saffron' },
    ]
  },
  {
    label: 'Study Tools',
    items: [
      { path: '/study-planner', icon: BookOpen, label: 'nav.studyPlanner', iconColor: 'green' },
      { path: '/notes', icon: GraduationCap, label: 'nav.notes', iconColor: 'green' },
      { path: '/past-papers', icon: FileText, label: 'nav.pastPaper', iconColor: 'saffron' },
      { path: '/pyq-pdfs', icon: FileDown, label: 'nav.pyqPdfs', iconColor: 'saffron' },
    ]
  },
  {
    label: 'AI',
    items: [
      { path: '/tutor', icon: Bot, label: 'nav.tutor', iconColor: 'blue' },
      { path: '/analytics', icon: BarChart3, label: 'nav.analytics', iconColor: 'green' },
    ]
  },
  {
    label: 'Community',
    items: [
      { path: '/forum', icon: MessageSquare, label: 'nav.forum', iconColor: 'saffron' },
      { path: '/peer-matching', icon: Users, label: 'nav.peerMatch', iconColor: 'green' },
    ]
  },
  {
    label: 'Account',
    items: [
      { path: '/settings', icon: Settings, label: 'nav.settings', iconColor: 'gray' },
    ]
  }
];

export default function Sidebar({ isOpen, onClose }) {
  const { t } = useTranslation();
  const { user } = useAuth();

  return (
    <>
      {isOpen && <div className="sidebar-overlay" onClick={onClose} />}
      <aside className={`sidebar ${isOpen ? 'open' : ''}`} id="main-sidebar" aria-label="Main navigation">

        {/* Brand strip */}
        <div className="sidebar-brand">
          <span className="sidebar-brand-name">🎯 Sarkari Exam AI</span>
          <button className="btn-icon sidebar-close" onClick={onClose} aria-label="Close sidebar">
            <X size={16} />
          </button>
        </div>

        {/* Nav sections */}
        <nav style={{ flex: 1 }}>
          {navSections.map(section => (
            <div key={section.label} className="sidebar-section">
              <div className="sidebar-section-label">{section.label}</div>
              {section.items.map(item => (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}
                  onClick={onClose}
                >
                  <span className={`link-icon ${item.iconColor}`}>
                    <item.icon size={16} />
                  </span>
                  <span>{item.label.includes('.') ? t(item.label) : item.label}</span>
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        {/* Footer */}
        <div className="sidebar-footer">
          <div className="sidebar-footer-info">
            <div className="dot" />
            <span className="sidebar-footer-text">
              {user ? `${user.displayName?.split(' ')[0] || 'Student'} · Active` : 'AI-Powered Prep'}
            </span>
          </div>
          {user && (
            <button className="sidebar-logout-btn" onClick={() => { logout(); onClose(); }} title={t('nav.logout')}>
              <LogOut size={16} />
              <span>{t('nav.logout')}</span>
            </button>
          )}
        </div>
      </aside>
    </>
  );
}
