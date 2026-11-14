
package com.opportunity360.reminder.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class ReminderRequest {

    @NotNull(message = "Reminder date and time are required")
    private LocalDateTime reminderAt;

    // Optional ID of the event occurrence.
    private Long occurrenceId;

    public LocalDateTime getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(LocalDateTime reminderAt) {
        this.reminderAt = reminderAt;
    }

    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }
}
