
package com.opportunity360.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.opportunity360.event.entity.Event;

public class DashboardEventItem {

    private Long id;
    private String title;
    private String category;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String status;

    public DashboardEventItem() {
    }

    public static DashboardEventItem fromEvent(Event event) {

        DashboardEventItem item = new DashboardEventItem();

        item.setId(event.getId());
        item.setTitle(event.getTitle());
        item.setCategory(event.getCategory().name());
        item.setEventDate(event.getEventDate());
        item.setEventTime(event.getEventTime());
        item.setStatus(event.getStatus().name());

        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
