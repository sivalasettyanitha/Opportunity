
import api from "./axiosConfig";

// Add a note to an opportunity
export const createOpportunityNote = async (opportunityId, content) => {
    const response = await api.post(
        `/opportunities/${opportunityId}/notes`,
        { content }
    );

    return response.data;
};

// Get all notes for an opportunity
export const getOpportunityNotes = async (opportunityId) => {
    const response = await api.get(
        `/opportunities/${opportunityId}/notes`
    );

    return response.data;
};

// Update an existing note
export const updateOpportunityNote = async (
    opportunityId,
    noteId,
    content
) => {
    const response = await api.put(
        `/opportunities/${opportunityId}/notes/${noteId}`,
        { content }
    );

    return response.data;
};

// Delete a note
export const deleteOpportunityNote = async (
    opportunityId,
    noteId
) => {
    const response = await api.delete(
        `/opportunities/${opportunityId}/notes/${noteId}`
    );

    return response.data;
};
