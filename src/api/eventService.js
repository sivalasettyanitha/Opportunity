
import api from "./axiosConfig";

// Get all events belonging to the logged-in user.
export const getAllEvents = async () => {
  const response = await api.get("/events");
  return response.data;
};

// Get a particular event by ID.
export const getEventById = async (eventId) => {
  const response = await api.get(`/events/${eventId}`);
  return response.data;
};

// Create a new event.
export const createEvent = async (eventData) => {
  const response = await api.post("/events", eventData);
  return response.data;
};

// Update an existing event.
export const updateEvent = async (eventId, eventData) => {
  const response = await api.put(
    `/events/${eventId}`,
    eventData
  );

  return response.data;
};

// Cancel an event.
export const cancelEvent = async (eventId) => {
  const response = await api.patch(
    `/events/${eventId}/cancel`
  );

  return response.data;
};
