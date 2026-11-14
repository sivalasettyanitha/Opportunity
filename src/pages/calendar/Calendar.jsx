
import { useEffect, useMemo, useState } from "react";
import {
  ChevronLeft,
  ChevronRight,
  CalendarDays,
  Clock,
} from "lucide-react";

import { getCalendarEvents } from "../../api/calendarService";
import {
  formatTime,
  formatDate,
} from "../../utils/dateTime";

const pad = (value) => String(value).padStart(2, "0");

const toDateString = (date) =>
  `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;

export default function Calendar() {
  const today = new Date();

  const [currentDate, setCurrentDate] = useState(
    new Date(today.getFullYear(), today.getMonth(), 1)
  );

  const [selectedDate, setSelectedDate] = useState(
    toDateString(today)
  );

  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const monthName = currentDate.toLocaleString("en-IN", {
    month: "long",
    year: "numeric",
  });

  // Fetch calendar events whenever the displayed month changes.
  useEffect(() => {
    const loadCalendarEvents = async () => {
      try {
        setLoading(true);
        setError("");

        const startDate = toDateString(
          new Date(year, month, 1)
        );

        const endDate = toDateString(
          new Date(year, month + 1, 0)
        );

        const response = await getCalendarEvents({
          startDate,
          endDate,
          page: 0,
          size: 100,
        });

        // Spring Boot returns a paginated Page response.
        const eventList = Array.isArray(response)
          ? response
          : response.content || [];

        setEvents(eventList);
      } catch (error) {
        setEvents([]);
        setError(
          error.response?.data?.message ||
            "Unable to load calendar events."
        );
      } finally {
        setLoading(false);
      }
    };

    loadCalendarEvents();
  }, [year, month]);

  const calendarDays = useMemo(() => {
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);

    // Monday-first calendar layout.
    const startingDay = (firstDay.getDay() + 6) % 7;

    const days = [];

    for (let i = 0; i < startingDay; i++) {
      days.push(null);
    }

    for (let day = 1; day <= lastDay.getDate(); day++) {
      days.push(day);
    }

    return days;
  }, [year, month]);

  const selectedEvents = events.filter(
    (event) => event.occurrenceDate === selectedDate
  );

  const getEventsForDay = (day) => {
    if (!day) return [];

    const dateString = toDateString(
      new Date(year, month, day)
    );

    return events.filter(
      (event) => event.occurrenceDate === dateString
    );
  };

  const goToPreviousMonth = () => {
    setCurrentDate(new Date(year, month - 1, 1));
  };

  const goToNextMonth = () => {
    setCurrentDate(new Date(year, month + 1, 1));
  };

  const goToToday = () => {
    const now = new Date();

    setCurrentDate(
      new Date(now.getFullYear(), now.getMonth(), 1)
    );

    setSelectedDate(toDateString(now));
  };

  const formatCategory = (category) =>
    category
      ?.replaceAll("_", " ")
      .toLowerCase()
      .replace(/\b\w/g, (letter) => letter.toUpperCase());

  return (
    <div className="calendar-page">
      <div className="calendar-page-header">
        <div>
          <h1>Calendar</h1>
          <p>View and organize your upcoming events.</p>
        </div>

        <button
          className="secondary-button"
          onClick={goToToday}
        >
          Today
        </button>
      </div>

      <div className="calendar-layout">
        <section className="calendar-panel">
          <div className="calendar-month-header">
            <h2>{monthName}</h2>

            <div className="calendar-navigation">
              <button
                onClick={goToPreviousMonth}
                aria-label="Previous month"
              >
                <ChevronLeft size={19} />
              </button>

              <button
                onClick={goToNextMonth}
                aria-label="Next month"
              >
                <ChevronRight size={19} />
              </button>
            </div>
          </div>

          <div className="calendar-grid">
            {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map(
              (day) => (
                <div className="calendar-weekday" key={day}>
                  {day}
                </div>
              )
            )}

            {calendarDays.map((day, index) => {
              const dateString = day
                ? toDateString(new Date(year, month, day))
                : "";

              const dayEvents = getEventsForDay(day);

              const isToday =
                day !== null &&
                dateString === toDateString(today);

              const isSelected =
                day !== null &&
                dateString === selectedDate;

              return (
                <button
                  key={`${year}-${month}-${index}`}
                  type="button"
                  className={[
                    "calendar-day",
                    !day ? "empty" : "",
                    isToday ? "today" : "",
                    isSelected ? "selected" : "",
                  ].join(" ")}
                  disabled={!day}
                  onClick={() => setSelectedDate(dateString)}
                >
                  {day && (
                    <>
                      <span>{day}</span>

                      {dayEvents.length > 0 && (
                        <span className="calendar-event-indicator">
                          {dayEvents.length > 3
                            ? "3+"
                            : "•".repeat(dayEvents.length)}
                        </span>
                      )}
                    </>
                  )}
                </button>
              );
            })}
          </div>

          {loading && (
            <p className="calendar-loading">
              Loading calendar events...
            </p>
          )}

          {error && (
            <div className="auth-error">{error}</div>
          )}
        </section>

        <section className="calendar-events-panel">
          <div className="calendar-events-heading">
            <div>
              <h2>Selected Date</h2>
              <p>{formatDate(selectedDate)}</p>
            </div>

            <CalendarDays size={22} />
          </div>

          {!loading && selectedEvents.length === 0 ? (
            <div className="calendar-no-events">
              <CalendarDays size={30} />
              <h3>No events for this date</h3>
              <p>Select another date to view its events.</p>
            </div>
          ) : (
            <div className="calendar-event-list">
              {selectedEvents.map((event) => (
                <div
                  className="calendar-event-card"
                  key={event.occurrenceId}
                >
                  <div className="calendar-event-card-top">
                    <h3>{event.title}</h3>

                    <span
                      className={`event-status ${
                        event.status?.toLowerCase()
                      }`}
                    >
                      {formatCategory(event.status)}
                    </span>
                  </div>

                  <p className="calendar-event-category">
                    {formatCategory(event.category)}
                  </p>

                  {event.description && (
                    <p className="calendar-event-description">
                      {event.description}
                    </p>
                  )}

                  {event.eventTime && (
                    <div className="calendar-event-time">
                      <Clock size={14} />
                      {formatTime(event.eventTime)}
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
}
