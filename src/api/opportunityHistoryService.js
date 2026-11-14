
import api from "./axiosConfig";

// Get application status history for an opportunity
export const getOpportunityHistory = async (opportunityId) => {
  const response = await api.get(
    `/opportunities/${opportunityId}/history`
  );

  return response.data;
};
