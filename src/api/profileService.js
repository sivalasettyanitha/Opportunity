
import api from "./axiosConfig";

// Update the logged-in user's profile name.
export const updateProfile = async (name) => {
  const response = await api.put("/auth/profile", {
    name: name,
  });

  return response.data;
};

// Change the logged-in user's password.
export const changePassword = async (
  currentPassword,
  newPassword
) => {
  const response = await api.put("/auth/change-password", {
    currentPassword,
    newPassword,
  });

  return response.data;
};
