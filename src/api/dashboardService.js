
import api from "./axiosConfig";

// Retrieve dashboard information for the logged-in user.
export const getDashboardData = async () => {
  const response = await api.get("/dashboard");

  return response.data;
};
