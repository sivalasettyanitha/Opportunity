
package com.opportunity360.dashboard.dto;

import java.util.List;

public class DashboardResponse {

    private long upcomingEvents;
    private long scheduledReminders;
    private long pendingActions;
    private long unreadNotifications;

    private List<DashboardEventItem> recentEvents;
    private List<DashboardReminderItem> upcomingReminders;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long upcomingEvents,
            long scheduledReminders,
            long pendingActions,
            long unreadNotifications,
            List<DashboardEventItem> recentEvents,
            List<DashboardReminderItem> upcomingReminders) {

        this.upcomingEvents = upcomingEvents;
        this.scheduledReminders = scheduledReminders;
        this.pendingActions = pendingActions;
        this.unreadNotifications = unreadNotifications;
        this.recentEvents = recentEvents;
        this.upcomingReminders = upcomingReminders;
    }

    public long getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(long upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public long getScheduledReminders() {
        return scheduledReminders;
    }

    public void setScheduledReminders(long scheduledReminders) {
        this.scheduledReminders = scheduledReminders;
    }

    public long getPendingActions() {
        return pendingActions;
    }

    public void setPendingActions(long pendingActions) {
        this.pendingActions = pendingActions;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public List<DashboardEventItem> getRecentEvents() {
        return recentEvents;
    }

    public void setRecentEvents(List<DashboardEventItem> recentEvents) {
        this.recentEvents = recentEvents;
    }

    public List<DashboardReminderItem> getUpcomingReminders() {
        return upcomingReminders;
    }

    public void setUpcomingReminders(
            List<DashboardReminderItem> upcomingReminders) {
        this.upcomingReminders = upcomingReminders;
    }
}
