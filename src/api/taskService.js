
import api from "./axiosConfig";

// Retrieve all tasks belonging to the logged-in user.
export const getAllTasks = async () => {
  const response = await api.get("/actions");
  return response.data;
};

// Retrieve tasks associated with a particular event.
export const getEventTasks = async (eventId) => {
  const response = await api.get(`/actions/events/${eventId}`);
  return response.data;
};

// Retrieve tasks associated with a particular event occurrence.
export const getOccurrenceTasks = async (occurrenceId) => {
  const response = await api.get(
    `/actions/occurrences/${occurrenceId}`
  );

  return response.data;
};

// Create a task for an event.
export const createTask = async (eventId, taskData) => {
  const response = await api.post(
    `/actions/events/${eventId}`,
    taskData
  );

  return response.data;
};

// Update an existing task.
export const updateTask = async (taskId, taskData) => {
  const response = await api.put(
    `/actions/${taskId}`,
    taskData
  );

  return response.data;
};

// Update task status.
export const updateTaskStatus = async (taskId, status) => {
  const response = await api.patch(
    `/actions/${taskId}/status`,
    { status }
  );

  return response.data;
};

// Cancel a task.
export const cancelTask = async (taskId) => {
  const response = await api.patch(
    `/actions/${taskId}/cancel`
  );

  return response.data;
};
