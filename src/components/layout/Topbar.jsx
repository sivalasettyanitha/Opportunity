
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Bell, Search, LogOut } from "lucide-react";
import NotificationDropdown from "../common/NotificationDropdown";

export default function Topbar() {
  const [showNotifications, setShowNotifications] = useState(false);
  const navigate = useNavigate();

  // Get logged-in user details from local storage.
  let user = {};

  try {
    user = JSON.parse(localStorage.getItem("user")) || {};
  } catch {
    user = {};
  }

  const userName = user.name || "My Account";
  const userInitial = userName.charAt(0).toUpperCase();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login", { replace: true });
  };

  return (
    <header className="topbar">

      <div className="topbar-search">
        <Search size={18} />
        <input
          type="text"
          placeholder="Search events, tasks and reminders..."
        />
      </div>

      <div className="topbar-actions">

        <div className="notification-wrapper">
          <button
            className="notification-button"
            aria-label="Notifications"
            onClick={() =>
              setShowNotifications((previous) => !previous)
            }
          >
            <Bell size={20} />
            <span className="notification-dot"></span>
          </button>

          {showNotifications && (
            <NotificationDropdown
              onClose={() => setShowNotifications(false)}
            />
          )}
        </div>

        <div className="topbar-user">
          <div className="user-avatar">
            {userInitial}
          </div>

          <div className="user-details">
            <strong>{userName}</strong>
            <span>{user.email || "Personal Workspace"}</span>
          </div>
        </div>

        <button
          className="logout-button"
          onClick={handleLogout}
          title="Logout"
          aria-label="Logout"
        >
          <LogOut size={19} />
          <span>Logout</span>
        </button>

      </div>
    </header>
  );
}
