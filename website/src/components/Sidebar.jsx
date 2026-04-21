import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../contexts/AuthContext';
import {
  LayoutDashboard, Brain, Target, BookOpen, FileText,
  GraduationCap, Bot, BarChart3, MessageSquare, Users,
  Settings, X, Newspaper, FileDown
} from 'lucide-react';
import logo from '../assets/logo.png';
import './Sidebar.css';

const navSections = [
  {
    label: 'Main',
    items: [
      { path: '/dashboard',       icon: LayoutDashboard, label: 'dashboard',       iconColor: 'blue'    },
      { path: '/current-affairs', icon: Newspaper,       label: 'currentAffairs', iconColor: 'saffron' },
      { path: '/blog',            icon: Newspaper,       label: 'blog',           iconColor: 'green'   },
    ]
  },
  {
    label: 'Tests',
    items: [
      { path: '/mock-test',      icon: Brain,  label: 'mockTest',      iconColor: 'blue'    },
      { path: '/pyqs-mock-test', icon: Target, label: 'pyqsMockTest',  iconColor: 'saffron' },
    ]
  },
  {
    label: 'Study Tools',
    items: [
      { path: '/study-planner', icon: BookOpen,     label: 'studyPlanner', iconColor: 'green'   },
      { path: '/notes',         icon: GraduationCap, label: 'notes',        iconColor: 'green'   },
      { path: '/past-papers',   icon: FileText,      label: 'pastPaper',    iconColor: 'saffron' },
      { path: '/pyq-pdfs',      icon: FileDown,      label: 'pyqPdfs',      iconColor: 'saffron' },
    ]
  },
  {
    label: 'AI',
    items: [
      { path: '/tutor',     icon: Bot,      label: 'tutor',     iconColor: 'blue'  },
      { path: '/analytics', icon: BarChart3, label: 'analytics', iconColor: 'green' },
    ]
  },
  {
    label: 'Community',
    items: [
      { path: '/forum',         icon: MessageSquare, label: 'forum',     iconColor: 'saffron' },
      { path: '/peer-matching', icon: Users,         label: 'peerMatch', iconColor: 'green'   },
    ]
  },
  {
    label: 'Account',
    items: [
      { path: '/settings', icon: Settings, label: 'settings', iconColor: 'gray' },
    ]
  }
];

export default function Sidebar({ isOpen, onClose }) {
  const { t } = useTranslation();
  const { user } = useAuth();

  return (
    <>
      <div
        className={`sidebar-overlay ${isOpen ? 'visible' : ''}`}
        onClick={onClose}
        aria-hidden="true"
      />

      <aside
        className={`sidebar ${isOpen ? 'open' : ''}`}
        id="main-sidebar"
        aria-label="Main navigation"
      >
        <div className="sidebar-brand">
          <img src={logo} alt="Logo" className="sidebar-logo-img" />
          <button
            className="sidebar-close"
            onClick={onClose}
            aria-label="Close sidebar"
          >
            <X size={16} />
          </button>
        </div>

        <nav style={{ flex: 1, overflowY: 'auto', overflowX: 'hidden' }}>
          {navSections.map(section => (
            <div key={section.label} className="sidebar-section">
              <div className="sidebar-section-label">{t(`nav.sections.${section.label.toLowerCase().replace(' ', '')}`, { defaultValue: section.label })}</div>
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
                  <span>{t(`nav.${item.label}`, { defaultValue: item.label === 'currentAffairs' ? 'Current Affairs' : item.label.charAt(0).toUpperCase() + item.label.slice(1) })}</span>
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="sidebar-footer-info">
            <div className="dot" />
            <span className="sidebar-footer-text">
              {user? `${user.displayName?.split(' ')[0] || 'Student'} · Active` : 'AI-Powered Prep'}
            </span>
          </div>
        </div>
      </aside>
    </>
  );
}
