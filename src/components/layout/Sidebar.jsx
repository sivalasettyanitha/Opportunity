
import { NavLink } from "react-router-dom";

import {
  LayoutDashboard,
  CalendarDays,
  CalendarCheck,
  Bell,
  CheckSquare,
  BriefcaseBusiness,
  BarChart3,
  UserRound,
  Settings,
} from "lucide-react";

const menuItems = [
  {
    name: "Dashboard",
    path: "/",
    icon: LayoutDashboard,
  },
  {
    name: "Calendar",
    path: "/calendar",
    icon: CalendarDays,
  },
  {
    name: "Events",
    path: "/events",
    icon: CalendarCheck,
  },
  {
    name: "Reminders",
    path: "/reminders",
    icon: Bell,
  },
  {
    name: "My Tasks",
    path: "/tasks",
    icon: CheckSquare,
  },
  {
    name: "Opportunities",
    path: "/opportunities",
    icon: BriefcaseBusiness,
  },
  {
    name: "Statistics",
    path: "/statistics",
    icon: BarChart3,
  },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="brand-icon">O</div>

        <div>
          <h2>Opportunity360</h2>
          <span>Never forget what matters.</span>
        </div>
      </div>

      <div className="sidebar-section">
        WORKSPACE
      </div>

      <nav className="sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === "/"}
              className={({ isActive }) =>
                `sidebar-link ${isActive ? "active" : ""}`
              }
            >
              <Icon size={19} />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      <div className="sidebar-bottom">
        <NavLink
          to="/profile"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "active" : ""}`
          }
        >
          <UserRound size={19} />
          <span>My Profile</span>
        </NavLink>

        <NavLink
          to="/settings"
          className={({ isActive }) =>
            `sidebar-link ${isActive ? "active" : ""}`
          }
        >
          <Settings size={19} />
          <span>Settings</span>
        </NavLink>
      </div>
    </aside>
  );
}
