
import api from "./axiosConfig";

// Retrieve notification preferences.
export const getNotificationPreferences = async () => {
  const response = await api.get("/notifications/preferences");

  return response.data;
};

// Update notification preferences.
export const updateNotificationPreferences = async (
  preferences
) => {
  const response = await api.put(
    "/notifications/preferences",
    {
      emailEnabled: preferences.emailEnabled,
      inAppEnabled: preferences.inAppEnabled,
    }
  );

  return response.data;
};
