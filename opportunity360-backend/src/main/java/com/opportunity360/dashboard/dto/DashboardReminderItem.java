
package com.opportunity360.dashboard.dto;

import java.time.LocalDateTime;

import com.opportunity360.reminder.entity.Reminder;

public class DashboardReminderItem {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime reminderAt;
    private String status;

    public DashboardReminderItem() {
    }

    public static DashboardReminderItem fromReminder(Reminder reminder) {

        DashboardReminderItem item = new DashboardReminderItem();

        item.setId(reminder.getId());
        item.setEventId(reminder.getEvent().getId());
        item.setEventTitle(reminder.getEvent().getTitle());
        item.setReminderAt(reminder.getReminderAt());
        item.setStatus(reminder.getStatus().name());

        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDateTime getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(LocalDateTime reminderAt) {
        this.reminderAt = reminderAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
