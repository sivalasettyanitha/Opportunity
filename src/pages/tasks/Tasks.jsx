
import { useEffect, useState } from "react";
import {
  Plus,
  ClipboardList,
  Clock,
  CheckCircle,
  X,
  Ban,
} from "lucide-react";

import {
  getAllTasks,
  createTask,
  updateTaskStatus,
  cancelTask,
} from "../../api/taskService";

import { getAllEvents } from "../../api/eventService";
import TimeInput from "../../components/TimeInput";

const initialForm = {
  eventId: "",
  title: "",
  description: "",
  actionType: "GENERAL",
  dueAt: "",
};

const statuses = [
  "PENDING",
  "IN_PROGRESS",
  "COMPLETED",
];

const actionTypes = [
  "PREPARATION",
  "FOLLOW_UP",
  "GENERAL",
];

export default function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [events, setEvents] = useState([]);

  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState(initialForm);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [typeFilter, setTypeFilter] = useState("ALL");

  useEffect(() => {
    loadPageData();
  }, []);

  const loadPageData = async () => {
    try {
      setLoading(true);
      setError("");

      const [taskResponse, eventResponse] = await Promise.all([
        getAllTasks(),
        getAllEvents(),
      ]);

      setTasks(Array.isArray(taskResponse) ? taskResponse : []);
      setEvents(Array.isArray(eventResponse) ? eventResponse : []);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load tasks."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleCreateTask = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");

    try {
      setSaving(true);

      const taskData = {
        title: formData.title,
        description: formData.description,
        actionType: formData.actionType,
        dueAt: formData.dueAt || null,
      };

      await createTask(Number(formData.eventId), taskData);

      setMessage("Task created successfully!");
      setFormData(initialForm);
      setShowForm(false);

      await loadPageData();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to create task."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleStatusChange = async (taskId, status) => {
    setMessage("");
    setError("");

    try {
      await updateTaskStatus(taskId, status);

      setMessage("Task status updated successfully!");
      await loadPageData();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to update task status."
      );
    }
  };

  const handleCancelTask = async (taskId) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this task?"
    );

    if (!confirmed) return;

    setMessage("");
    setError("");

    try {
      await cancelTask(taskId);

      setMessage("Task cancelled successfully!");
      await loadPageData();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to cancel task."
      );
    }
  };

  const filteredTasks = tasks.filter((task) => {
    const searchText =
      `${task.title || ""} ${task.eventTitle || ""}`.toLowerCase();

    const matchesSearch = searchText.includes(
      search.toLowerCase()
    );

    const matchesStatus =
      statusFilter === "ALL" || task.status === statusFilter;

    const matchesType =
      typeFilter === "ALL" || task.actionType === typeFilter;

    return matchesSearch && matchesStatus && matchesType;
  });

  const countStatus = (status) =>
    tasks.filter((task) => task.status === status).length;

  const formatLabel = (value) =>
    value
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  // Format task due date and time using saved preferences.
  const formatDueDateTime = (dateTime) => {
    if (!dateTime) return "Date and time unavailable";

    const date = new Date(dateTime);

    if (Number.isNaN(date.getTime())) {
      return "Date and time unavailable";
    }

    const timeFormat = localStorage.getItem("timeFormat") || "12h";
    const timezone =
      localStorage.getItem("timezone") || "Asia/Kolkata";

    return new Intl.DateTimeFormat("en-IN", {
      day: "numeric",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hourCycle: timeFormat === "24h" ? "h23" : "h12",
      timeZone: timezone,
    }).format(date);
  };

  return (
    <div className="tasks-page">
      <div className="tasks-header">
        <div>
          <h1>My Tasks</h1>
          <p>Manage your preparation and follow-up activities.</p>
        </div>

        <button
          className="primary-button"
          onClick={() => {
            setShowForm((previous) => !previous);
            setMessage("");
            setError("");
          }}
        >
          {showForm ? <X size={17} /> : <Plus size={17} />}
          {showForm ? "Close Form" : "Add Task"}
        </button>
      </div>

      {message && (
        <div className="settings-message">{message}</div>
      )}

      {error && <div className="auth-error">{error}</div>}

      {/* Task Summary */}
      <div className="task-summary-grid">
        <div className="task-summary-card">
          <ClipboardList size={20} />
          <div>
            <h3>{tasks.length}</h3>
            <p>Total Tasks</p>
          </div>
        </div>

        <div className="task-summary-card">
          <Clock size={20} />
          <div>
            <h3>{countStatus("PENDING")}</h3>
            <p>Pending</p>
          </div>
        </div>

        <div className="task-summary-card">
          <ClipboardList size={20} />
          <div>
            <h3>{countStatus("IN_PROGRESS")}</h3>
            <p>In Progress</p>
          </div>
        </div>

        <div className="task-summary-card">
          <CheckCircle size={20} />
          <div>
            <h3>{countStatus("COMPLETED")}</h3>
            <p>Completed</p>
          </div>
        </div>
      </div>

      {/* Create Task Form */}
      {showForm && (
        <div className="task-form-panel">
          <h2>Create New Task</h2>

          <form onSubmit={handleCreateTask}>
            <div className="event-form-grid">
              <div className="form-field">
                <label>Associated Event *</label>

                <select
                  name="eventId"
                  value={formData.eventId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select an event</option>

                  {events
                    .filter((event) => event.status === "UPCOMING")
                    .map((event) => (
                      <option key={event.id} value={event.id}>
                        {event.title}
                      </option>
                    ))}
                </select>
              </div>

              <div className="form-field">
                <label>Task Type *</label>

                <select
                  name="actionType"
                  value={formData.actionType}
                  onChange={handleChange}
                  required
                >
                  {actionTypes.map((type) => (
                    <option key={type} value={type}>
                      {formatLabel(type)}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-field">
                <label>Task Title *</label>

                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="Enter task title"
                  maxLength={150}
                  required
                />
              </div>

              <div className="form-field">
                <label>Due Date and Time</label>

                <TimeInput
                  value={formData.dueAt}
                  includeDate={true}
                  dateLabel="Due Date"
                  onChange={(value) =>
                    setFormData((previous) => ({
                      ...previous,
                      dueAt: value,
                    }))
                  }
                />
              </div>

              <div className="form-field">
                <label>Description</label>

                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="Enter task details..."
                  rows={3}
                  maxLength={1000}
                />
              </div>
            </div>

            <div className="event-form-actions">
              <button
                type="button"
                className="secondary-button"
                onClick={() => {
                  setShowForm(false);
                  setFormData(initialForm);
                }}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="primary-button"
                disabled={saving}
              >
                {saving ? "Creating..." : "Create Task"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Search and Filters */}
      <div className="tasks-toolbar">
        <input
          type="text"
          placeholder="Search tasks or events..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />

        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
        >
          <option value="ALL">All Statuses</option>

          {statuses.map((status) => (
            <option key={status} value={status}>
              {formatLabel(status)}
            </option>
          ))}

          <option value="CANCELLED">Cancelled</option>
        </select>

        <select
          value={typeFilter}
          onChange={(event) => setTypeFilter(event.target.value)}
        >
          <option value="ALL">All Task Types</option>

          {actionTypes.map((type) => (
            <option key={type} value={type}>
              {formatLabel(type)}
            </option>
          ))}
        </select>
      </div>

      {/* Task List */}
      <div className="tasks-list">
        {loading ? (
          <div className="tasks-empty">
            Loading tasks...
          </div>
        ) : filteredTasks.length === 0 ? (
          <div className="tasks-empty">
            <ClipboardList size={32} />
            <h3>No tasks found</h3>
            <p>Create a task or change your filters.</p>
          </div>
        ) : (
          filteredTasks.map((task) => (
            <div className="task-card" key={task.id}>
              <div className="task-card-icon">
                <ClipboardList size={20} />
              </div>

              <div className="task-card-content">
                <h3>{task.title}</h3>

                <p>{task.eventTitle || "Associated event"}</p>

                {task.description && (
                  <p>{task.description}</p>
                )}

                <span className="task-type">
                  {formatLabel(task.actionType)}
                </span>

                {task.dueAt && (
                  <div className="task-due-date">
                    <Clock size={14} />
                    {formatDueDateTime(task.dueAt)}
                  </div>
                )}
              </div>

              <div className="task-card-actions">
                <select
                  value={task.status}
                  disabled={
                    task.status === "CANCELLED" ||
                    task.status === "COMPLETED"
                  }
                  onChange={(event) =>
                    handleStatusChange(task.id, event.target.value)
                  }
                  aria-label={`Status for ${task.title}`}
                >
                  {statuses.map((status) => (
                    <option key={status} value={status}>
                      {formatLabel(status)}
                    </option>
                  ))}

                  {task.status === "CANCELLED" && (
                    <option value="CANCELLED">Cancelled</option>
                  )}
                </select>

                {task.status !== "CANCELLED" &&
                  task.status !== "COMPLETED" && (
                    <button
                      className="event-cancel-button"
                      onClick={() => handleCancelTask(task.id)}
                    >
                      <Ban size={15} />
                      Cancel
                    </button>
                  )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
