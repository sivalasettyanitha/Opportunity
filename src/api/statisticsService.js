
import api from "./axiosConfig";

// Retrieve event statistics.
export const getEventStatistics = async () => {
  const response = await api.get("/statistics/events");

  return response.data;
};

// Retrieve task statistics.
export const getTaskStatistics = async () => {
  const response = await api.get("/statistics/tasks");

  return response.data;
};

// Retrieve both event and task statistics.
export const getAllStatistics = async () => {
  const [eventStatistics, taskStatistics] = await Promise.all([
    getEventStatistics(),
    getTaskStatistics(),
  ]);

  return {
    eventStatistics,
    taskStatistics,
  };
};
