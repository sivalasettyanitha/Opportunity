
export const getTimePreferences = () => {
  return {
    timeFormat: localStorage.getItem("timeFormat") || "12h",
    timezone: localStorage.getItem("timezone") || "Asia/Kolkata",
    dateFormat: localStorage.getItem("dateFormat") || "DD/MM/YYYY",
  };
};

// Convert HH:mm into the selected 12-hour or 24-hour format.
export const formatTime = (timeValue) => {
  if (!timeValue) return "Time not specified";

  const { timeFormat } = getTimePreferences();

  const match = String(timeValue).match(
    /^(\d{1,2}):(\d{2})(?::\d{2})?$/
  );

  if (!match) return String(timeValue);

  let hours = Number(match[1]);
  const minutes = match[2];

  if (timeFormat === "24h") {
    return `${String(hours).padStart(2, "0")}:${minutes}`;
  }

  const period = hours >= 12 ? "PM" : "AM";
  hours = hours % 12 || 12;

  return `${String(hours).padStart(2, "0")}:${minutes} ${period}`;
};

// Format a date using the user's selected date preference.
export const formatDate = (dateValue) => {
  if (!dateValue) return "Date not specified";

  const datePart = String(dateValue).split("T")[0];
  const match = datePart.match(/^(\d{4})-(\d{2})-(\d{2})$/);

  if (!match) return String(dateValue);

  const [, year, month, day] = match;
  const { dateFormat } = getTimePreferences();

  if (dateFormat === "MM/DD/YYYY") {
    return `${month}/${day}/${year}`;
  }

  if (dateFormat === "YYYY-MM-DD") {
    return `${year}-${month}-${day}`;
  }

  return `${day}/${month}/${year}`;
};

// Format a complete date and time using saved preferences.
export const formatDateTime = (dateTimeValue) => {
  if (!dateTimeValue) return "Date and time unavailable";

  const date = new Date(dateTimeValue);

  if (Number.isNaN(date.getTime())) {
    return "Date and time unavailable";
  }

  const { timeFormat, timezone } = getTimePreferences();

  return new Intl.DateTimeFormat("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hourCycle: timeFormat === "24h" ? "h23" : "h12",
    timeZone: timezone,
  }).format(date);
};
