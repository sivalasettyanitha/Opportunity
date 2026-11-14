
import api from "./axiosConfig";

// Get all opportunities
export const getAllOpportunities = async () => {
  const response = await api.get("/opportunities");
  return response.data;
};

// Get opportunity by ID
export const getOpportunityById = async (id) => {
  const response = await api.get(`/opportunities/${id}`);
  return response.data;
};

// Create a new opportunity
export const createOpportunity = async (opportunityData) => {
  const response = await api.post(
    "/opportunities",
    opportunityData
  );
  return response.data;
};

// Update opportunity details
export const updateOpportunity = async (id, opportunityData) => {
  const response = await api.put(
    `/opportunities/${id}`,
    opportunityData
  );
  return response.data;
};

// Update opportunity application status
export const updateOpportunityStatus = async (id, status) => {
  const response = await api.patch(
    `/opportunities/${id}/status`,
    null,
    {
      params: { status },
    }
  );

  return response.data;
};

// Delete opportunity
export const deleteOpportunity = async (id) => {
  const response = await api.delete(`/opportunities/${id}`);
  return response.data;
};

// Get opportunity statistics
export const getOpportunityStatistics = async () => {
  const response = await api.get("/opportunities/statistics");
  return response.data;
};
