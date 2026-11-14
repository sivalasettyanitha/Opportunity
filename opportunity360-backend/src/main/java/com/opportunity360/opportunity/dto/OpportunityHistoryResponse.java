
package com.opportunity360.opportunity.dto;

import java.time.LocalDateTime;

import com.opportunity360.opportunity.entity.OpportunityStatus;
import com.opportunity360.opportunity.history.OpportunityStatusHistory;

public class OpportunityHistoryResponse {

    private Long id;
    private Long opportunityId;
    private String opportunityTitle;
    private OpportunityStatus previousStatus;
    private OpportunityStatus newStatus;
    private LocalDateTime changedAt;

    public OpportunityHistoryResponse() {
    }

    public static OpportunityHistoryResponse fromHistory(
            OpportunityStatusHistory history) {

        OpportunityHistoryResponse response =
                new OpportunityHistoryResponse();

        response.setId(history.getId());
        response.setOpportunityId(
                history.getOpportunity().getId());

        response.setOpportunityTitle(
                history.getOpportunity().getTitle());

        response.setPreviousStatus(history.getPreviousStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedAt(history.getChangedAt());

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

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    public OpportunityStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(
            OpportunityStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public OpportunityStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OpportunityStatus newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
