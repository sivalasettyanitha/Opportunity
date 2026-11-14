
import { useEffect, useState } from "react";
import {
  Bell,
  Mail,
  Smartphone,
  Globe,
  ShieldCheck,
  CalendarDays,
  Save,
  Clock,
} from "lucide-react";

import {
  getNotificationPreferences,
  updateNotificationPreferences,
} from "../../api/settingsService";

const defaultSettings = {
  emailNotifications: true,
  inAppNotifications: true,
  reminderNotifications: true,
  dailySummary: false,
  timezone: "Asia/Kolkata",
  dateFormat: "DD/MM/YYYY",
  timeFormat: "12h",
  defaultReminder: "30",
};

export default function Settings() {
  const [settings, setSettings] = useState(defaultSettings);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const loadPreferences = async () => {
      const savedTimeFormat = localStorage.getItem("timeFormat");
      const savedTimezone = localStorage.getItem("timezone");
      const savedDateFormat = localStorage.getItem("dateFormat");
      const savedDefaultReminder =
        localStorage.getItem("defaultReminder");

      setSettings((previous) => ({
        ...previous,
        timeFormat: savedTimeFormat || "12h",
        timezone: savedTimezone || "Asia/Kolkata",
        dateFormat: savedDateFormat || "DD/MM/YYYY",
        defaultReminder: savedDefaultReminder || "30",
        reminderNotifications:
          localStorage.getItem("reminderNotifications") !== "false",
        dailySummary:
          localStorage.getItem("dailySummary") === "true",
      }));

      try {
        const response = await getNotificationPreferences();

        setSettings((previous) => ({
          ...previous,
          emailNotifications: response.emailEnabled,
          inAppNotifications: response.inAppEnabled,
        }));
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Unable to load notification preferences."
        );
      } finally {
        setLoading(false);
      }
    };

    loadPreferences();
  }, []);

  const handleToggle = (key) => {
    setSettings((previous) => ({
      ...previous,
      [key]: !previous[key],
    }));

    setMessage("");
    setError("");
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setSettings((previous) => ({
      ...previous,
      [name]: value,
    }));

    setMessage("");
    setError("");
  };

  const handleSave = async () => {
    setMessage("");
    setError("");

    try {
      setSaving(true);

      await updateNotificationPreferences({
        emailEnabled: settings.emailNotifications,
        inAppEnabled: settings.inAppNotifications,
      });

      localStorage.setItem("timeFormat", settings.timeFormat);
      localStorage.setItem("timezone", settings.timezone);
      localStorage.setItem("dateFormat", settings.dateFormat);

      localStorage.setItem(
        "defaultReminder",
        settings.defaultReminder
      );

      localStorage.setItem(
        "reminderNotifications",
        String(settings.reminderNotifications)
      );

      localStorage.setItem(
        "dailySummary",
        String(settings.dailySummary)
      );

      window.dispatchEvent(new Event("preferencesUpdated"));

      setMessage("Your preferences have been saved successfully.");
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to save notification preferences."
      );
    } finally {
      setSaving(false);
    }
  };

  const renderToggle = (key) => (
    <button
      type="button"
      className={`settings-toggle ${
        settings[key] ? "active" : ""
      }`}
      onClick={() => handleToggle(key)}
      role="switch"
      aria-checked={settings[key]}
      aria-label={`Toggle ${key}`}
    >
      <span />
    </button>
  );

  // Generate preview using the currently selected preferences.
  const getPreview = () => {
    const now = new Date();

    const dateParts = new Intl.DateTimeFormat("en-IN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      timeZone: settings.timezone,
    }).formatToParts(now);

    const day = dateParts.find(
      (part) => part.type === "day"
    )?.value;

    const month = dateParts.find(
      (part) => part.type === "month"
    )?.value;

    const year = dateParts.find(
      (part) => part.type === "year"
    )?.value;

    let date;

    if (settings.dateFormat === "MM/DD/YYYY") {
      date = `${month}/${day}/${year}`;
    } else if (settings.dateFormat === "YYYY-MM-DD") {
      date = `${year}-${month}-${day}`;
    } else {
      date = `${day}/${month}/${year}`;
    }

    const time = new Intl.DateTimeFormat("en-IN", {
      hour: "2-digit",
      minute: "2-digit",
      hourCycle:
        settings.timeFormat === "24h" ? "h23" : "h12",
      timeZone: settings.timezone,
    }).format(now);

    return { date, time };
  };

  const preview = getPreview();

  return (
    <div className="settings-page">
      <div className="settings-header">
        <div>
          <h1>Settings</h1>
          <p>Customize your Opportunity360 experience.</p>
        </div>

        <div className="settings-header-icon">
          <ShieldCheck size={23} />
        </div>
      </div>

      {/* Notification Preferences */}
      <section className="settings-panel">
        <div className="settings-panel-heading">
          <div className="settings-panel-icon purple">
            <Bell size={20} />
          </div>

          <div>
            <h2>Notification Preferences</h2>
            <p>Choose how you want to receive notifications.</p>
          </div>
        </div>

        {loading ? (
          <p>Loading notification preferences...</p>
        ) : (
          <>
            <div className="settings-option">
              <div className="settings-option-info">
                <Mail size={19} />
                <div>
                  <h3>Email Notifications</h3>
                  <p>Receive reminders and updates through email.</p>
                </div>
              </div>
              {renderToggle("emailNotifications")}
            </div>

            <div className="settings-option">
              <div className="settings-option-info">
                <Bell size={19} />
                <div>
                  <h3>In-App Notifications</h3>
                  <p>Receive notifications inside Opportunity360.</p>
                </div>
              </div>
              {renderToggle("inAppNotifications")}
            </div>

            <div className="settings-option">
              <div className="settings-option-info">
                <Smartphone size={19} />
                <div>
                  <h3>Reminder Notifications</h3>
                  <p>Enable or disable reminder alerts in this interface.</p>
                </div>
              </div>
              {renderToggle("reminderNotifications")}
            </div>

            <div className="settings-option">
              <div className="settings-option-info">
                <CalendarDays size={19} />
                <div>
                  <h3>Daily Summary</h3>
                  <p>Choose whether you want a daily activity summary.</p>
                </div>
              </div>
              {renderToggle("dailySummary")}
            </div>
          </>
        )}
      </section>

      {/* General Preferences */}
      <section className="settings-panel">
        <div className="settings-panel-heading">
          <div className="settings-panel-icon blue">
            <Globe size={20} />
          </div>

          <div>
            <h2>General Preferences</h2>
            <p>Customize your date and time preferences.</p>
          </div>
        </div>

        <div className="settings-form-grid">
          <div className="settings-form-field">
            <label htmlFor="timezone">Timezone</label>

            <select
              id="timezone"
              name="timezone"
              value={settings.timezone}
              onChange={handleChange}
            >
              <option value="Asia/Kolkata">
                India Standard Time (IST)
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
              <option value="America/Los_Angeles">
                Pacific Time
              </option>
            </select>
          </div>

          <div className="settings-form-field">
            <label htmlFor="dateFormat">Date Format</label>

            <select
              id="dateFormat"
              name="dateFormat"
              value={settings.dateFormat}
              onChange={handleChange}
            >
              <option value="DD/MM/YYYY">DD/MM/YYYY</option>
              <option value="MM/DD/YYYY">MM/DD/YYYY</option>
              <option value="YYYY-MM-DD">YYYY-MM-DD</option>
            </select>
          </div>

          <div className="settings-form-field">
            <label htmlFor="timeFormat">
              <Clock size={15} /> Time Format
            </label>

            <select
              id="timeFormat"
              name="timeFormat"
              value={settings.timeFormat}
              onChange={handleChange}
            >
              <option value="12h">
                12-hour format (02:30 PM)
              </option>
              <option value="24h">
                24-hour format (14:30)
              </option>
            </select>
          </div>

          <div className="settings-form-field">
            <label htmlFor="defaultReminder">
              Default Reminder
            </label>

            <select
              id="defaultReminder"
              name="defaultReminder"
              value={settings.defaultReminder}
              onChange={handleChange}
            >
              <option value="5">5 minutes before</option>
              <option value="15">15 minutes before</option>
              <option value="30">30 minutes before</option>
              <option value="60">1 hour before</option>
              <option value="1440">1 day before</option>
            </select>
          </div>
        </div>

        {/* Live Preference Preview */}
        <div className="security-information">
          <Clock size={21} />

          <div>
            <h3>Format Preview</h3>
            <p>Timezone: {settings.timezone}</p>
            <p>
              Date: {preview.date} | Time: {preview.time}
            </p>
            <p>
              Date Format: {settings.dateFormat} | Time Format:{" "}
              {settings.timeFormat === "12h" ? "12-hour" : "24-hour"}
            </p>
          </div>
        </div>
      </section>

      {/* Account Security */}
      <section className="settings-panel">
        <div className="settings-panel-heading">
          <div className="settings-panel-icon green">
            <ShieldCheck size={20} />
          </div>

          <div>
            <h2>Account Security</h2>
            <p>Your account protection information.</p>
          </div>
        </div>

        <div className="security-information">
          <ShieldCheck size={21} />

          <div>
            <h3>Your account is protected</h3>
            <p>
              Opportunity360 uses password encryption and JWT-based
              authentication to protect your account.
            </p>
          </div>
        </div>
      </section>

      {message && (
        <div className="settings-message">{message}</div>
      )}

      {error && (
        <div className="auth-error">{error}</div>
      )}

      <div className="settings-save-section">
        <button
          type="button"
          className="primary-button"
          onClick={handleSave}
          disabled={loading || saving}
        >
          <Save size={16} />
          {saving ? "Saving..." : "Save Settings"}
        </button>
      </div>
    </div>
  );
}
