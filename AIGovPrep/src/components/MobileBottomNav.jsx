import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LayoutDashboard, Brain, Target, Bot, Settings } from 'lucide-react';
import './MobileBottomNav.css';

export default function MobileBottomNav() {
  const { t } = useTranslation();

  const navItems = [
    { path: '/dashboard', icon: LayoutDashboard, label: 'nav.dashboard' },
    { path: '/mock-test', icon: Brain, label: 'nav.mockTest' },
    { path: '/pyqs-mock-test', icon: Target, label: 'PYQs' },
    { path: '/tutor', icon: Bot, label: 'AI Tutor' },
    { path: '/settings', icon: Settings, label: 'Settings' }
  ];

  return (
    <nav className="mobile-bottom-nav">
      {navItems.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          className={({ isActive }) => `bottom-nav-item ${isActive ? 'active' : ''}`}
        >
          <item.icon size={22} className="nav-icon" />
          <span className="nav-label">
            {['PYQs', 'AI Tutor', 'Settings'].includes(item.label) ? item.label : t(item.label)}
          </span>
        </NavLink>
      ))}
    </nav>
  );
}
