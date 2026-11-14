
import { useEffect, useState } from "react";
import {
  CalendarDays,
  Bell,
  ClipboardList,
  BriefcaseBusiness,
  Clock,
  Plus,
  ArrowRight,
  RefreshCw,
  CheckCircle,
} from "lucide-react";

import { Link } from "react-router-dom";
import { getDashboardData } from "../../api/dashboardService";
import { getOpportunityStatistics } from "../../api/opportunityService";
import {
  formatTime,
  formatDate,
  formatDateTime,
} from "../../utils/dateTime";

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [opportunityStats, setOpportunityStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      setError("");

      const [dashboardData, opportunityData] = await Promise.all([
        getDashboardData(),
        getOpportunityStatistics(),
      ]);

      setDashboard(dashboardData);
      setOpportunityStats(opportunityData);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load dashboard information."
      );
    } finally {
      setLoading(false);
    }
  };

  const summaryCards = [
    {
      title: "Upcoming Events",
      value: dashboard?.upcomingEvents ?? 0,
      icon: CalendarDays,
      color: "purple",
      link: "/events",
    },
    {
      title: "Active Reminders",
      value: dashboard?.scheduledReminders ?? 0,
      icon: Bell,
      color: "blue",
      link: "/reminders",
    },
    {
      title: "Pending Tasks",
      value: dashboard?.pendingActions ?? 0,
      icon: ClipboardList,
      color: "orange",
      link: "/tasks",
    },
    {
      title: "Unread Notifications",
      value: dashboard?.unreadNotifications ?? 0,
      icon: Bell,
      color: "green",
      link: "/settings",
    },
    {
      title: "Total Opportunities",
      value: opportunityStats?.totalOpportunities ?? 0,
      icon: BriefcaseBusiness,
      color: "blue",
      link: "/opportunities",
    },
  ];

  const quickActions = [
    {
      title: "Create Event",
      description: "Schedule an important event",
      icon: CalendarDays,
      color: "purple",
      link: "/events",
    },
    {
      title: "Manage Reminders",
      description: "Keep track of important alerts",
      icon: Bell,
      color: "blue",
      link: "/reminders",
    },
    {
      title: "Manage Tasks",
      description: "Organize your daily activities",
      icon: ClipboardList,
      color: "orange",
      link: "/tasks",
    },
    {
      title: "Open Calendar",
      description: "View your scheduled events",
      icon: CalendarDays,
      color: "green",
      link: "/calendar",
    },
    {
      title: "My Opportunities",
      description: "Track your career applications",
      icon: BriefcaseBusiness,
      color: "purple",
      link: "/opportunities",
    },
  ];

  const formatLabel = (value) =>
    value
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  const recentEvents = dashboard?.recentEvents || [];
  const upcomingReminders = dashboard?.upcomingReminders || [];

  return (
    <div className="dashboard">
      {/* Welcome Section */}
      <div className="dashboard-welcome">
        <div>
          <h1>Welcome Back!</h1>
          <p>
            Here's what's happening with your personal workspace.
          </p>
        </div>

        <button
          className="secondary-button dashboard-refresh"
          onClick={loadDashboard}
          disabled={loading}
        >
          <RefreshCw size={16} />
          {loading ? "Loading..." : "Refresh"}
        </button>
      </div>

      {error && <div className="auth-error">{error}</div>}

      {/* Summary Cards */}
      <div className="summary-grid">
        {summaryCards.map((card) => {
          const Icon = card.icon;

          return (
            <Link
              to={card.link}
              className="summary-card"
              key={card.title}
            >
              <div className={`summary-icon ${card.color}`}>
                <Icon size={22} />
              </div>

              <div className="summary-card-content">
                <p>{card.title}</p>
                <h2>{loading ? "—" : card.value}</h2>
              </div>

              <ArrowRight
                className="summary-card-arrow"
                size={17}
              />
            </Link>
          );
        })}
      </div>

      {/* Main Dashboard Content */}
      <div className="dashboard-main-grid">
        {/* Recent Events */}
        <section className="dashboard-panel">
          <div className="dashboard-panel-header">
            <div>
              <h2>Recent Events</h2>
              <p>Your latest scheduled events.</p>
            </div>

            <Link to="/events" className="dashboard-view-link">
              View All <ArrowRight size={15} />
            </Link>
          </div>

          {loading ? (
            <div className="dashboard-empty">
              <p>Loading events...</p>
            </div>
          ) : recentEvents.length === 0 ? (
            <div className="dashboard-empty">
              <div className="dashboard-empty-icon purple">
                <CalendarDays size={27} />
              </div>

              <h3>No events available</h3>
              <p>Create your first event to get started.</p>

              <Link
                to="/events"
                className="dashboard-action-button"
              >
                <Plus size={16} />
                Create Event
              </Link>
            </div>
          ) : (
            <div className="dashboard-event-list">
              {recentEvents.map((event) => (
                <div
                  className="dashboard-event-item"
                  key={event.id}
                >
                  <div className="dashboard-event-icon">
                    <CalendarDays size={20} />
                  </div>

                  <div className="dashboard-event-info">
                    <h3>{event.title}</h3>

                    <p>{formatLabel(event.category)}</p>

                    <span>
                      <Clock size={13} />
                      {formatDate(event.eventDate)}
                      {event.eventTime
                        ? ` • ${formatTime(event.eventTime)}`
                        : ""}
                    </span>
                  </div>

                  <span
                    className={`event-status ${
                      event.status?.toLowerCase()
                    }`}
                  >
                    {formatLabel(event.status)}
                  </span>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* Upcoming Reminders */}
        <section className="dashboard-panel">
          <div className="dashboard-panel-header">
            <div>
              <h2>Upcoming Reminders</h2>
              <p>Stay on top of important alerts.</p>
            </div>

            <Link to="/reminders" className="dashboard-view-link">
              View All <ArrowRight size={15} />
            </Link>
          </div>

          {loading ? (
            <div className="dashboard-empty">
              <p>Loading reminders...</p>
            </div>
          ) : upcomingReminders.length === 0 ? (
            <div className="dashboard-empty">
              <div className="dashboard-empty-icon blue">
                <Bell size={27} />
              </div>

              <h3>No upcoming reminders</h3>
              <p>Your scheduled reminders will appear here.</p>

              <Link
                to="/reminders"
                className="dashboard-action-button"
              >
                <Plus size={16} />
                Create Reminder
              </Link>
            </div>
          ) : (
            <div className="dashboard-reminder-list">
              {upcomingReminders.map((reminder) => (
                <div
                  className="dashboard-reminder-item"
                  key={reminder.id}
                >
                  <div className="dashboard-reminder-icon">
                    <Clock size={19} />
                  </div>

                  <div className="dashboard-reminder-info">
                    <h3>{reminder.eventTitle}</h3>

                    <span>
                      {formatDateTime(reminder.reminderAt)}
                    </span>
                  </div>

                  <span
                    className={`reminder-status ${
                      reminder.status?.toLowerCase()
                    }`}
                  >
                    {formatLabel(reminder.status)}
                  </span>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>

      {/* Quick Actions */}
      <section className="dashboard-panel dashboard-quick-panel">
        <div className="dashboard-panel-header">
          <div>
            <h2>Quick Actions</h2>
            <p>Access your frequently used features.</p>
          </div>
        </div>

        <div className="dashboard-quick-actions">
          {quickActions.map((action) => {
            const Icon = action.icon;

            return (
              <Link
                to={action.link}
                className="quick-action-card"
                key={action.title}
              >
                <div className={`quick-action-icon ${action.color}`}>
                  <Icon size={22} />
                </div>

                <div className="quick-action-content">
                  <h3>{action.title}</h3>
                  <p>{action.description}</p>
                </div>

                <ArrowRight
                  size={17}
                  className="quick-action-arrow"
                />
              </Link>
            );
          })}
        </div>
      </section>

      {/* Dashboard Footer */}
      <div className="dashboard-footer">
        <span>
          <CheckCircle size={15} />
          Your workspace is ready
        </span>

        <span>Opportunity360 · Never forget what matters.</span>
      </div>
    </div>
  );
}
