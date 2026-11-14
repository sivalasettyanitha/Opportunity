
import api from "./axiosConfig";

// Retrieve calendar events for a date range.
export const getCalendarEvents = async ({
  startDate,
  endDate,
  category,
  status,
  search,
  page = 0,
  size = 100,
}) => {
  const response = await api.get("/calendar", {
    params: {
      startDate,
      endDate,
      category: category || undefined,
      status: status || undefined,
      search: search || undefined,
      page,
      size,
    },
  });

  return response.data;
};
