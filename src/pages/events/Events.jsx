
import { useEffect, useState } from "react";
import { Plus, CalendarDays, Clock, X, Ban } from "lucide-react";

import {
  getAllEvents,
  createEvent,
  cancelEvent,
} from "../../api/eventService";

import { formatTime, formatDate } from "../../utils/dateTime";
import TimeInput from "../../components/TimeInput";

const initialFormData = {
  title: "",
  description: "",
  category: "PERSONAL",
  eventDate: "",
  eventTime: "",
  timezone: "Asia/Kolkata",
  recurrenceType: "NONE",
  recurrenceEndDate: "",
};

const categories = [
  "PERSONAL",
  "CAREER",
  "HEALTH",
  "PAYMENT",
  "TRAVEL",
  "DAILY_TASK",
  "OTHER",
];

const recurrenceOptions = [
  "NONE",
  "DAILY",
  "WEEKLY",
  "MONTHLY",
  "YEARLY",
];

export default function Events() {
  const [events, setEvents] = useState([]);
  const [formData, setFormData] = useState(initialFormData);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await getAllEvents();
      setEvents(Array.isArray(response) ? response : []);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load events. Please try again."
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

  const handleSubmit = async (event) => {
    event.preventDefault();

    setMessage("");
    setError("");

    try {
      setSaving(true);

      const eventData = {
        ...formData,
        eventTime: formData.eventTime || null,
        recurrenceEndDate:
          formData.recurrenceType === "NONE"
            ? null
            : formData.recurrenceEndDate || null,
      };

      await createEvent(eventData);

      setMessage("Event created successfully!");
      setFormData(initialFormData);
      setShowForm(false);

      await loadEvents();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to create event. Please check the details."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleCancelEvent = async (eventId) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this event?"
    );

    if (!confirmed) return;

    setMessage("");
    setError("");

    try {
      await cancelEvent(eventId);

      setMessage("Event cancelled successfully!");
      await loadEvents();
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to cancel this event."
      );
    }
  };

  const formatCategory = (category) =>
    category
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  return (
    <div className="events-page">
      <div className="events-page-header">
        <div>
          <h1>My Events</h1>
          <p>Organize and manage all your important events.</p>
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
          {showForm ? "Close Form" : "Create Event"}
        </button>
      </div>

      {message && (
        <div className="settings-message">{message}</div>
      )}

      {error && <div className="auth-error">{error}</div>}

      {/* Create Event Form */}
      {showForm && (
        <div className="event-form-panel">
          <h2>Create New Event</h2>

          <form onSubmit={handleSubmit}>
            <div className="event-form-grid">
              <div className="form-field">
                <label>Event Title *</label>
                <input
                  type="text"
                  name="title"
                  placeholder="Enter event title"
                  value={formData.title}
                  onChange={handleChange}
                  maxLength={150}
                  required
                />
              </div>

              <div className="form-field">
                <label>Category *</label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                  required
                >
                  {categories.map((category) => (
                    <option key={category} value={category}>
                      {formatCategory(category)}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-field">
                <label>Event Date *</label>
                <input
                  type="date"
                  name="eventDate"
                  value={formData.eventDate}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-field">
                <label>Event Time</label>
                <TimeInput
                  value={formData.eventTime}
                  onChange={(value) =>
                    setFormData((previous) => ({
                      ...previous,
                      eventTime: value,
                    }))
                  }
                />
              </div>

              <div className="form-field">
                <label>Timezone</label>
                <select
                  name="timezone"
                  value={formData.timezone}
                  onChange={handleChange}
                >
                  <option value="Asia/Kolkata">
                    India Standard Time
                  </option>
                  <option value="Asia/Dubai">
                    Gulf Standard Time
                  </option>
                  <option value="Europe/London">
                    London Time
                  </option>
                  <option value="America/New_York">
                    Eastern Time
                  </option>
                </select>
              </div>

              <div className="form-field">
                <label>Recurrence</label>
                <select
                  name="recurrenceType"
                  value={formData.recurrenceType}
                  onChange={handleChange}
                >
                  {recurrenceOptions.map((option) => (
                    <option key={option} value={option}>
                      {formatCategory(option)}
                    </option>
                  ))}
                </select>
              </div>

              {formData.recurrenceType !== "NONE" && (
                <div className="form-field">
                  <label>Recurrence End Date</label>
                  <input
                    type="date"
                    name="recurrenceEndDate"
                    value={formData.recurrenceEndDate}
                    onChange={handleChange}
                    min={formData.eventDate || undefined}
                  />
                </div>
              )}

              <div className="form-field">
                <label>Description</label>
                <textarea
                  name="description"
                  placeholder="Add event details..."
                  value={formData.description}
                  onChange={handleChange}
                  maxLength={1000}
                  rows={3}
                />
              </div>
            </div>

            <div className="event-form-actions">
              <button
                type="button"
                className="secondary-button"
                onClick={() => {
                  setShowForm(false);
                  setFormData(initialFormData);
                }}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="primary-button"
                disabled={saving}
              >
                {saving ? "Creating..." : "Create Event"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Events List */}
      <div className="events-list">
        {loading ? (
          <div className="events-empty-state">
            Loading your events...
          </div>
        ) : events.length === 0 ? (
          <div className="events-empty-state">
            <CalendarDays size={35} />
            <h3>No events available</h3>
            <p>Create your first event to get started.</p>
          </div>
        ) : (
          events.map((event) => (
            <div className="event-card" key={event.id}>
              <div className="event-card-details">
                <div className="event-card-icon">
                  <CalendarDays size={21} />
                </div>

                <div>
                  <h3>{event.title}</h3>

                  <p>{formatCategory(event.category)}</p>

                  {event.description && (
                    <p>{event.description}</p>
                  )}

                  <div className="event-card-meta">
                    <span>
                      <CalendarDays size={14} />
                      {formatDate(event.eventDate)}
                    </span>

                    {event.eventTime && (
                      <span>
                        <Clock size={14} />
                        {formatTime(event.eventTime)}
                      </span>
                    )}
                  </div>

                  {event.recurrenceType &&
                    event.recurrenceType !== "NONE" && (
                      <span className="event-recurrence">
                        {formatCategory(event.recurrenceType)}
                      </span>
                    )}
                </div>
              </div>

              <div className="event-card-actions">
                <span
                  className={`event-status ${
                    event.status?.toLowerCase()
                  }`}
                >
                  {formatCategory(event.status)}
                </span>

                {event.status === "UPCOMING" && (
                  <button
                    className="event-cancel-button"
                    onClick={() => handleCancelEvent(event.id)}
                    title="Cancel event"
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
