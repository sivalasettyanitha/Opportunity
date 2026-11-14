
package com.opportunity360.opportunity.notes.dto;

import java.time.LocalDateTime;

import com.opportunity360.opportunity.notes.OpportunityNote;

public class OpportunityNoteResponse {

    private Long id;
    private Long opportunityId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OpportunityNoteResponse() {
    }

    public static OpportunityNoteResponse fromNote(OpportunityNote note) {

        OpportunityNoteResponse response = new OpportunityNoteResponse();

        response.setId(note.getId());
        response.setOpportunityId(note.getOpportunity().getId());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
