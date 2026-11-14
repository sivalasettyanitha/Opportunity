
import { useEffect, useState } from "react";
import {
  Plus,
  Bell,
  Clock,
  CalendarDays,
  X,
  Ban,
} from "lucide-react";

import {
  getAllReminders,
  createReminder,
  cancelReminder,
} from "../../api/reminderService";

import { getAllEvents } from "../../api/eventService";
import TimeInput from "../../components/TimeInput";
import { formatDateTime } from "../../utils/dateTime";

export default function Reminders() {
  const [reminders, setReminders] = useState([]);
  const [events, setEvents] = useState([]);

  const [showForm, setShowForm] = useState(false);

  const [formData, setFormData] = useState({
    eventId: "",
    reminderAt: "",
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  useEffect(() => {
    loadPageData();
  }, []);

  const loadPageData = async () => {
    try {
      setLoading(true);
      setError("");

      const [reminderResponse, eventResponse] = await Promise.all([
        getAllReminders(),
        getAllEvents(),
      ]);

      setReminders(
        Array.isArray(reminderResponse) ? reminderResponse : []
      );

      setEvents(
        Array.isArray(eventResponse) ? eventResponse : []
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load reminders."
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

  const handleCreateReminder = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");

    if (!formData.eventId) {
      setError("Please select an event.");
      return;
    }

    if (!formData.reminderAt) {
      setError("Please select a reminder date and time.");
      return;
    }

    try {
      setSaving(true);

      await createReminder(
        Number(formData.eventId),
        {
          reminderAt: formData.reminderAt,
        }
      );

      setMessage("Reminder created successfully!");

      setFormData({
        eventId: "",
        reminderAt: "",
      });

      setShowForm(false);

      await loadPageData();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to create reminder. Please check the selected event and time."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleCancelReminder = async (reminderId) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this reminder?"
    );

    if (!confirmed) return;

    setMessage("");
    setError("");

    try {
      await cancelReminder(reminderId);

      setMessage("Reminder cancelled successfully!");

      await loadPageData();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to cancel reminder."
      );
    }
  };

  const filteredReminders = reminders.filter((reminder) => {
    const title = reminder.eventTitle || "";

    const matchesSearch = title
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchesStatus =
      statusFilter === "ALL" ||
      reminder.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const countStatus = (status) =>
    reminders.filter(
      (reminder) => reminder.status === status
    ).length;

  const formatStatus = (status) =>
    status
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  return (
    <div className="reminders-page">
      <div className="reminders-header">
        <div>
          <h1>Reminders</h1>
          <p>Manage your scheduled alerts and notifications.</p>
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
          {showForm ? "Close Form" : "Create Reminder"}
        </button>
      </div>

      {message && (
        <div className="settings-message">{message}</div>
      )}

      {error && <div className="auth-error">{error}</div>}

      {/* Summary Cards */}
      <div className="reminder-summary-grid">
        <div className="reminder-summary-card">
          <Bell size={20} />
          <div>
            <h3>{reminders.length}</h3>
            <p>Total Reminders</p>
          </div>
        </div>

        <div className="reminder-summary-card">
          <Clock size={20} />
          <div>
            <h3>{countStatus("SCHEDULED")}</h3>
            <p>Scheduled</p>
          </div>
        </div>

        <div className="reminder-summary-card">
          <CalendarDays size={20} />
          <div>
            <h3>{countStatus("SENT")}</h3>
            <p>Sent</p>
          </div>
        </div>

        <div className="reminder-summary-card">
          <Bell size={20} />
          <div>
            <h3>{countStatus("FAILED")}</h3>
            <p>Failed</p>
          </div>
        </div>
      </div>

      {/* Create Reminder Form */}
      {showForm && (
        <div className="reminder-form-panel">
          <h2>Create New Reminder</h2>

          <form onSubmit={handleCreateReminder}>
            <div className="event-form-grid">
              <div className="form-field">
                <label>Select Event *</label>

                <select
                  name="eventId"
                  value={formData.eventId}
                  onChange={handleChange}
                  required
                >
                  <option value="">Choose an event</option>

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
                <label>Reminder Date and Time *</label>

                <TimeInput
                  value={formData.reminderAt}
                  includeDate={true}
                  dateLabel="Reminder Date"
                  onChange={(value) =>
                    setFormData((previous) => ({
                      ...previous,
                      reminderAt: value,
                    }))
                  }
                />
              </div>
            </div>

            <div className="event-form-actions">
              <button
                type="button"
                className="secondary-button"
                onClick={() => setShowForm(false)}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="primary-button"
                disabled={saving}
              >
                {saving ? "Creating..." : "Create Reminder"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Search and Filter */}
      <div className="reminders-toolbar">
        <input
          type="text"
          placeholder="Search reminders by event..."
          value={search}
          onChange={(event) => setSearch(event.target.value)}
        />

        <select
          value={statusFilter}
          onChange={(event) => setStatusFilter(event.target.value)}
        >
          <option value="ALL">All Statuses</option>
          <option value="SCHEDULED">Scheduled</option>
          <option value="SENT">Sent</option>
          <option value="FAILED">Failed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>

      {/* Reminder List */}
      <div className="reminders-list">
        {loading ? (
          <div className="reminders-empty">
            Loading reminders...
          </div>
        ) : filteredReminders.length === 0 ? (
          <div className="reminders-empty">
            <Bell size={32} />
            <h3>No reminders found</h3>
            <p>Create a reminder or change your search filters.</p>
          </div>
        ) : (
          filteredReminders.map((reminder) => (
            <div className="reminder-card" key={reminder.id}>
              <div className="reminder-card-icon">
                <Bell size={20} />
              </div>

              <div className="reminder-card-content">
                <h3>{reminder.eventTitle || "Event Reminder"}</h3>

                <div className="reminder-card-time">
                  <Clock size={14} />
                  {formatDateTime(reminder.reminderAt)}
                </div>
              </div>

              <div className="reminder-card-actions">
                <span
                  className={`reminder-status ${
                    reminder.status?.toLowerCase()
                  }`}
                >
                  {formatStatus(reminder.status)}
                </span>

                {reminder.status === "SCHEDULED" && (
                  <button
                    className="event-cancel-button"
                    onClick={() =>
                      handleCancelReminder(reminder.id)
                    }
                    title="Cancel reminder"
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
