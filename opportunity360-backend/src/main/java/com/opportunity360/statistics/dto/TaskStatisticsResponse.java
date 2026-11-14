
package com.opportunity360.statistics.dto;

public class TaskStatisticsResponse {

    private long totalActions;
    private long pendingActions;
    private long inProgressActions;
    private long completedActions;
    private long cancelledActions;
    private long overdueActions;

    public TaskStatisticsResponse() {
    }

    public TaskStatisticsResponse(
            long totalActions,
            long pendingActions,
            long inProgressActions,
            long completedActions,
            long cancelledActions,
            long overdueActions) {

        this.totalActions = totalActions;
        this.pendingActions = pendingActions;
        this.inProgressActions = inProgressActions;
        this.completedActions = completedActions;
        this.cancelledActions = cancelledActions;
        this.overdueActions = overdueActions;
    }

    public long getTotalActions() {
        return totalActions;
    }

    public void setTotalActions(long totalActions) {
        this.totalActions = totalActions;
    }

    public long getPendingActions() {
        return pendingActions;
    }

    public void setPendingActions(long pendingActions) {
        this.pendingActions = pendingActions;
    }

    public long getInProgressActions() {
        return inProgressActions;
    }

    public void setInProgressActions(long inProgressActions) {
        this.inProgressActions = inProgressActions;
    }

    public long getCompletedActions() {
        return completedActions;
    }

    public void setCompletedActions(long completedActions) {
        this.completedActions = completedActions;
    }

    public long getCancelledActions() {
        return cancelledActions;
    }

    public void setCancelledActions(long cancelledActions) {
        this.cancelledActions = cancelledActions;
    }

    public long getOverdueActions() {
        return overdueActions;
    }

    public void setOverdueActions(long overdueActions) {
        this.overdueActions = overdueActions;
    }
}
