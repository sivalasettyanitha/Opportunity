
import { useEffect, useState } from "react";

const hours = Array.from({ length: 12 }, (_, index) =>
  String(index + 1)
);

const minutes = Array.from({ length: 60 }, (_, index) =>
  String(index).padStart(2, "0")
);

export default function TimeInput({
  value = "",
  onChange,
  includeDate = false,
  dateLabel = "Date",
}) {
  const parts = value ? value.split("T") : [];
  const dateValue = includeDate ? parts[0] || "" : "";
  const timeValue = includeDate ? parts[1] || "" : value;

  const timeParts = timeValue ? timeValue.split(":") : [];
  const storedHours = timeParts[0]
    ? Number(timeParts[0])
    : null;

  const [selectedHour, setSelectedHour] = useState(
    storedHours === null
      ? ""
      : String(storedHours % 12 || 12)
  );

  const [selectedMinute, setSelectedMinute] = useState(
    timeParts[1] || ""
  );

  const [selectedPeriod, setSelectedPeriod] = useState(
    storedHours === null
      ? ""
      : storedHours >= 12
        ? "PM"
        : "AM"
  );

  useEffect(() => {
    const currentParts = value ? value.split("T") : [];
    const currentTime = includeDate
      ? currentParts[1] || ""
      : value;

    const currentTimeParts = currentTime
      ? currentTime.split(":")
      : [];

    const currentHours = currentTimeParts[0]
      ? Number(currentTimeParts[0])
      : null;

    setSelectedHour(
      currentHours === null
        ? ""
        : String(currentHours % 12 || 12)
    );

    setSelectedMinute(currentTimeParts[1] || "");

    setSelectedPeriod(
      currentHours === null
        ? ""
        : currentHours >= 12
          ? "PM"
          : "AM"
    );
  }, [value, includeDate]);

  const buildTime = (hour, minute, period) => {
    if (!hour || !minute || !period) return "";

    let convertedHour = Number(hour);

    if (period === "AM" && convertedHour === 12) {
      convertedHour = 0;
    } else if (period === "PM" && convertedHour !== 12) {
      convertedHour += 12;
    }

    return `${String(convertedHour).padStart(2, "0")}:${minute}`;
  };

  const updateTime = (hour, minute, period) => {
    const formattedTime = buildTime(hour, minute, period);

    if (!formattedTime) return;

    if (includeDate) {
      if (dateValue) {
        onChange(`${dateValue}T${formattedTime}`);
      }
    } else {
      onChange(formattedTime);
    }
  };

  const handleHourChange = (newHour) => {
    setSelectedHour(newHour);
    updateTime(newHour, selectedMinute, selectedPeriod);
  };

  const handleMinuteChange = (newMinute) => {
    setSelectedMinute(newMinute);
    updateTime(selectedHour, newMinute, selectedPeriod);
  };

  const handlePeriodChange = (newPeriod) => {
    setSelectedPeriod(newPeriod);
    updateTime(selectedHour, selectedMinute, newPeriod);
  };

  const handleDateChange = (newDate) => {
    if (!newDate) {
      onChange("");
      return;
    }

    const formattedTime = buildTime(
      selectedHour,
      selectedMinute,
      selectedPeriod
    );

    if (formattedTime) {
      onChange(`${newDate}T${formattedTime}`);
    } else {
      onChange(`${newDate}T00:00`);
    }
  };

  return (
    <div className="time-input-component">
      {includeDate && (
        <div className="form-field">
          <label>{dateLabel}</label>

          <input
            type="date"
            value={dateValue}
            onChange={(event) =>
              handleDateChange(event.target.value)
            }
          />
        </div>
      )}

      <div className="time-picker-controls">
        <div className="form-field">
          <label>Hour</label>

          <select
            value={selectedHour}
            onChange={(event) =>
              handleHourChange(event.target.value)
            }
          >
            <option value="">HH</option>

            {hours.map((hour) => (
              <option key={hour} value={hour}>
                {hour}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label>Minute</label>

          <select
            value={selectedMinute}
            onChange={(event) =>
              handleMinuteChange(event.target.value)
            }
          >
            <option value="">MM</option>

            {minutes.map((minute) => (
              <option key={minute} value={minute}>
                {minute}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label>AM / PM</label>

          <select
            value={selectedPeriod}
            onChange={(event) =>
              handlePeriodChange(event.target.value)
            }
          >
            <option value="">Select</option>
            <option value="AM">AM</option>
            <option value="PM">PM</option>
          </select>
        </div>
      </div>
    </div>
  );
}
