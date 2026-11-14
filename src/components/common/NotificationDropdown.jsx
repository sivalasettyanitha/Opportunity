
import { useState } from "react";
import {
  Bell,
  CalendarDays,
  Clock,
  CheckCheck,
  X,
} from "lucide-react";

const initialNotifications = [
  {
    id: 1,
    title: "Upcoming Event",
    message: "Your Java Developer Interview is coming up.",
    time: "10 minutes ago",
    type: "EVENT",
    read: false,
  },
  {
    id: 2,
    title: "Reminder Alert",
    message: "Don't forget your scheduled appointment.",
    time: "1 hour ago",
    type: "REMINDER",
    read: false,
  },
  {
    id: 3,
    title: "Task Update",
    message: "You have pending preparation tasks.",
    time: "Yesterday",
    type: "TASK",
    read: true,
  },
];

export default function NotificationDropdown({ onClose }) {
  const [notifications, setNotifications] = useState(
    initialNotifications
  );

  const unreadCount = notifications.filter(
    (notification) => !notification.read
  ).length;

  const markAsRead = (id) => {
    setNotifications((previous) =>
      previous.map((notification) =>
        notification.id === id
          ? { ...notification, read: true }
          : notification
      )
    );
  };

  const markAllAsRead = () => {
    setNotifications((previous) =>
      previous.map((notification) => ({
        ...notification,
        read: true,
      }))
    );
  };

  const getNotificationIcon = (type) => {
    switch (type) {
      case "EVENT":
        return <CalendarDays size={18} />;
      case "REMINDER":
        return <Clock size={18} />;
      default:
        return <Bell size={18} />;
    }
  };

  return (
    <div className="notification-dropdown">

      <div className="notification-dropdown-header">
        <div>
          <h3>Notifications</h3>
          <span>{unreadCount} unread notifications</span>
        </div>

        <button
          className="notification-close-button"
          onClick={onClose}
          aria-label="Close notifications"
        >
          <X size={18} />
        </button>
      </div>

      <div className="notification-dropdown-actions">
        <button
          onClick={markAllAsRead}
          disabled={unreadCount === 0}
        >
          <CheckCheck size={14} />
          Mark all as read
        </button>
      </div>

      <div className="notification-dropdown-list">

        {notifications.length > 0 ? (
          notifications.map((notification) => (
            <div
              className={`notification-item ${
                notification.read ? "read" : "unread"
              }`}
              key={notification.id}
              onClick={() => markAsRead(notification.id)}
            >
              <div className="notification-item-icon">
                {getNotificationIcon(notification.type)}
              </div>

              <div className="notification-item-content">
                <h4>{notification.title}</h4>
                <p>{notification.message}</p>
                <span>{notification.time}</span>
              </div>

              {!notification.read && (
                <span className="notification-unread-dot" />
              )}
            </div>
          ))
        ) : (
          <div className="notification-empty">
            <Bell size={28} />
            <p>No notifications available.</p>
          </div>
        )}

      </div>

      <div className="notification-dropdown-footer">
        <span>You're all caught up!</span>
      </div>

    </div>
  );
}
