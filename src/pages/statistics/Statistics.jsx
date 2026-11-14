
import { useEffect, useState } from "react";
import {
  CalendarDays,
  CheckCircle,
  XCircle,
  Clock,
  ClipboardList,
  TrendingUp,
  AlertCircle,
} from "lucide-react";

import { getAllStatistics } from "../../api/statisticsService";

export default function Statistics() {
  const [eventStats, setEventStats] = useState(null);
  const [taskStats, setTaskStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadStatistics();
  }, []);

  const loadStatistics = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllStatistics();

      setEventStats(response.eventStatistics);
      setTaskStats(response.taskStatistics);
    } catch (error) {
      setError(
        error.response?.data?.message ||
        "Unable to load statistics."
      );
    } finally {
      setLoading(false);
    }
  };

  const eventCards = [
    {
      title: "Total Events",
      value: eventStats?.totalEvents ?? 0,
      icon: CalendarDays,
      color: "purple",
    },
    {
      title: "Upcoming Events",
      value: eventStats?.upcomingEvents ?? 0,
      icon: Clock,
      color: "blue",
    },
    {
      title: "Completed Events",
      value: eventStats?.completedEvents ?? 0,
      icon: CheckCircle,
      color: "green",
    },
    {
      title: "Cancelled Events",
      value: eventStats?.cancelledEvents ?? 0,
      icon: XCircle,
      color: "orange",
    },
  ];

  const taskCards = [
    {
      title: "Total Tasks",
      value: taskStats?.totalActions ?? 0,
      icon: ClipboardList,
      color: "purple",
    },
    {
      title: "Pending Tasks",
      value: taskStats?.pendingActions ?? 0,
      icon: Clock,
      color: "blue",
    },
    {
      title: "In Progress",
      value: taskStats?.inProgressActions ?? 0,
      icon: TrendingUp,
      color: "orange",
    },
    {
      title: "Completed Tasks",
      value: taskStats?.completedActions ?? 0,
      icon: CheckCircle,
      color: "green",
    },
  ];

  const categoryCounts = eventStats?.categoryCounts || {};

  const categories = Object.entries(categoryCounts);

  const maxCategoryCount = Math.max(
    1,
    ...categories.map(([, count]) => count)
  );

  const totalTasks = taskStats?.totalActions ?? 0;
  const completedTasks = taskStats?.completedActions ?? 0;

  const completionPercentage =
    totalTasks > 0
      ? Math.round((completedTasks / totalTasks) * 100)
      : 0;

  const formatLabel = (value) =>
    value
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  const renderCards = (cards) => (
    <div className="statistics-summary-grid">
      {cards.map((card) => {
        const Icon = card.icon;

        return (
          <div className="statistics-summary-card" key={card.title}>
            <div className={`statistics-card-icon ${card.color}`}>
              <Icon size={21} />
            </div>

            <div>
              <p>{card.title}</p>
              <h2>{loading ? "—" : card.value}</h2>
            </div>
          </div>
        );
      })}
    </div>
  );

  return (
    <div className="statistics-page">

      <div className="statistics-header">
        <div>
          <h1>Statistics</h1>
          <p>
            Understand your events, tasks and overall productivity.
          </p>
        </div>

        <button
          className="secondary-button"
          onClick={loadStatistics}
          disabled={loading}
        >
          {loading ? "Loading..." : "Refresh Statistics"}
        </button>
      </div>

      {error && (
        <div className="auth-error">{error}</div>
      )}

      {/* Event Statistics */}
      <section className="statistics-section">
        <div className="statistics-section-heading">
          <CalendarDays size={19} />
          <h2>Event Overview</h2>
        </div>

        {renderCards(eventCards)}
      </section>

      {/* Task Statistics */}
      <section className="statistics-section">
        <div className="statistics-section-heading">
          <ClipboardList size={19} />
          <h2>Task Overview</h2>
        </div>

        {renderCards(taskCards)}
      </section>

      {/* Event Categories */}
      <section className="statistics-panel">
        <div className="statistics-panel-heading">
          <h2>Events by Category</h2>
          <p>Distribution of your events across categories.</p>
        </div>

        {loading ? (
          <p>Loading category statistics...</p>
        ) : categories.length === 0 ? (
          <p>No category data available.</p>
        ) : (
          <div className="statistics-category-list">
            {categories.map(([category, count]) => (
              <div className="statistics-category-item" key={category}>
                <div className="statistics-category-info">
                  <span>{formatLabel(category)}</span>
                  <strong>{count}</strong>
                </div>

                <div className="statistics-bar-background">
                  <div
                    className="statistics-bar-fill"
                    style={{
                      width: `${(count / maxCategoryCount) * 100}%`,
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* Task Completion */}
      <section className="statistics-panel">
        <div className="statistics-panel-heading">
          <h2>Task Completion</h2>
          <p>Your completed tasks compared with all tasks.</p>
        </div>

        {loading ? (
          <p>Loading task completion...</p>
        ) : (
          <div className="statistics-completion">

            <div className="statistics-completion-icon">
              <TrendingUp size={24} />
            </div>

            <div className="statistics-completion-details">
              <h2>{completionPercentage}%</h2>
              <p>
                {completedTasks} of {totalTasks} tasks completed
              </p>

              <div className="statistics-bar-background">
                <div
                  className="statistics-bar-fill completion"
                  style={{
                    width: `${completionPercentage}%`,
                  }}
                />
              </div>
            </div>

          </div>
        )}
      </section>

      {/* Overdue Tasks */}
      <section className="statistics-overdue-panel">
        <div>
          <AlertCircle size={21} />
          <div>
            <h3>Overdue Tasks</h3>
            <p>Tasks whose due dates have passed and remain unfinished.</p>
          </div>
        </div>

        <strong>
          {loading ? "—" : taskStats?.overdueActions ?? 0}
        </strong>
      </section>

    </div>
  );
}
