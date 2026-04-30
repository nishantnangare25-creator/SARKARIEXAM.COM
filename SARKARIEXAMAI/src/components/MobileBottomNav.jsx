import { NavLink } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { LayoutDashboard, Brain, Bot, BarChart3, BookOpen } from 'lucide-react';
import './MobileBottomNav.css';

export default function MobileBottomNav() {
  const { t } = useTranslation();

  const navItems = [
    { path: '/dashboard', icon: LayoutDashboard, label: 'dashboard' },
    { path: '/mock-test', icon: Brain, label: 'mockTest' },
    { path: '/notes', icon: BookOpen, label: 'notes' },
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
            {t(`nav.${item.label}`, { defaultValue: item.label.charAt(0).toUpperCase() + item.label.slice(1) })}
          </span>
        </NavLink>
      ))}
    </nav>
  );
}
