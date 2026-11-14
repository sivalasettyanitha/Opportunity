
package com.opportunity360.reminder.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;

public class ReminderResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;

    private Long occurrenceId;
    private LocalDate occurrenceDate;

    private LocalDateTime reminderAt;
    private ReminderStatus status;
    private LocalDateTime createdAt;

    public static ReminderResponse fromReminder(Reminder reminder) {

        ReminderResponse response = new ReminderResponse();

        response.setId(reminder.getId());
        response.setEventId(reminder.getEvent().getId());
        response.setEventTitle(reminder.getEvent().getTitle());

        if (reminder.getOccurrence() != null) {
            response.setOccurrenceId(
                    reminder.getOccurrence().getId());

            response.setOccurrenceDate(
                    reminder.getOccurrence().getOccurrenceDate());
        }

        response.setReminderAt(reminder.getReminderAt());
        response.setStatus(reminder.getStatus());
        response.setCreatedAt(reminder.getCreatedAt());

        return response;
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

    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    public LocalDate getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(LocalDate occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public LocalDateTime getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(LocalDateTime reminderAt) {
        this.reminderAt = reminderAt;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
