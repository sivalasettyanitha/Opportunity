
package com.opportunity360.statistics.dto;

import java.util.Map;

public class EventStatisticsResponse {

    private long totalEvents;
    private long upcomingEvents;
    private long completedEvents;
    private long cancelledEvents;
    private Map<String, Long> categoryCounts;

    public EventStatisticsResponse() {
    }

    public EventStatisticsResponse(
            long totalEvents,
            long upcomingEvents,
            long completedEvents,
            long cancelledEvents,
            Map<String, Long> categoryCounts) {

        this.totalEvents = totalEvents;
        this.upcomingEvents = upcomingEvents;
        this.completedEvents = completedEvents;
        this.cancelledEvents = cancelledEvents;
        this.categoryCounts = categoryCounts;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public long getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(long upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    public long getCompletedEvents() {
        return completedEvents;
    }

    public void setCompletedEvents(long completedEvents) {
        this.completedEvents = completedEvents;
    }

    public long getCancelledEvents() {
        return cancelledEvents;
    }

    public void setCancelledEvents(long cancelledEvents) {
        this.cancelledEvents = cancelledEvents;
    }

    public Map<String, Long> getCategoryCounts() {
        return categoryCounts;
    }

    public void setCategoryCounts(Map<String, Long> categoryCounts) {
        this.categoryCounts = categoryCounts;
    }
}
