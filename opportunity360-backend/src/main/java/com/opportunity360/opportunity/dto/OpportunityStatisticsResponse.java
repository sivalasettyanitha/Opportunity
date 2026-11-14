
package com.opportunity360.opportunity.dto;

public class OpportunityStatisticsResponse {

    private long totalOpportunities;
    private long saved;
    private long applied;
    private long inProgress;
    private long selected;
    private long rejected;
    private long closed;

    public OpportunityStatisticsResponse() {
    }

    public OpportunityStatisticsResponse(
            long totalOpportunities,
            long saved,
            long applied,
            long inProgress,
            long selected,
            long rejected,
            long closed) {

        this.totalOpportunities = totalOpportunities;
        this.saved = saved;
        this.applied = applied;
        this.inProgress = inProgress;
        this.selected = selected;
        this.rejected = rejected;
        this.closed = closed;
    }

    public long getTotalOpportunities() {
        return totalOpportunities;
    }

    public void setTotalOpportunities(long totalOpportunities) {
        this.totalOpportunities = totalOpportunities;
    }

    public long getSaved() {
        return saved;
    }

    public void setSaved(long saved) {
        this.saved = saved;
    }

    public long getApplied() {
        return applied;
    }

    public void setApplied(long applied) {
        this.applied = applied;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getSelected() {
        return selected;
    }

    public void setSelected(long selected) {
        this.selected = selected;
    }

    public long getRejected() {
        return rejected;
    }

    public void setRejected(long rejected) {
        this.rejected = rejected;
    }

    public long getClosed() {
        return closed;
    }

    public void setClosed(long closed) {
        this.closed = closed;
    }
}
