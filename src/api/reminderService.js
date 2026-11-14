
import api from "./axiosConfig";

// Retrieve all reminders belonging to the logged-in user.
export const getAllReminders = async () => {
  const response = await api.get("/reminders");
  return response.data;
};

// Create a reminder for an event.
export const createReminder = async (eventId, reminderData) => {
  const response = await api.post(
    `/events/${eventId}/reminders`,
    reminderData
  );

  return response.data;
};

// Cancel a scheduled reminder.
export const cancelReminder = async (reminderId) => {
  const response = await api.patch(
    `/reminders/${reminderId}/cancel`
  );

  return response.data;
};

// Retrieve reminders associated with an event occurrence.
export const getOccurrenceReminders = async (occurrenceId) => {
  const response = await api.get(
    `/event-occurrences/${occurrenceId}/reminders`
  );

  return response.data;
};
