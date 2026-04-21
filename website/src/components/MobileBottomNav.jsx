import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LayoutDashboard, Brain, Target, Bot, BarChart3 } from 'lucide-react';
import './MobileBottomNav.css';

export default function MobileBottomNav() {
  const { t } = useTranslation();

  const navItems = [
    { path: '/dashboard', icon: LayoutDashboard, label: 'dashboard' },
    { path: '/mock-test', icon: Brain, label: 'mockTest' },
    { path: '/pyqs-mock-test', icon: Target, label: 'pyqsMockTest' },
    { path: '/tutor', icon: Bot, label: 'tutor' },
    { path: '/analytics', icon: BarChart3, label: 'analytics' }
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
            {t(`nav.${item.label}`, { defaultValue: item.label === 'pyqsMockTest' ? 'PYQs' : item.label })}
          </span>
        </NavLink>
      ))}
    </nav>
  );
}
