
package com.opportunity360.reminder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.opportunity360.reminder.dto.ReminderRequest;
import com.opportunity360.reminder.dto.ReminderResponse;
import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.service.ReminderService;

import jakarta.validation.Valid;

@RestController
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    // Create a reminder for an event.
    @PostMapping("/api/events/{eventId}/reminders")
    public ResponseEntity<?> createReminder(
            @PathVariable Long eventId,
            @Valid @RequestBody ReminderRequest request,
            Authentication authentication) {

        try {
            Reminder reminder = reminderService.createReminder(
                    eventId,
                    authentication.getName(),
                    request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ReminderResponse.fromReminder(reminder));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Get all reminders belonging to the logged-in user.
    @GetMapping("/api/reminders")
    public ResponseEntity<List<ReminderResponse>> getAllReminders(
            Authentication authentication) {

        List<ReminderResponse> reminders =
                reminderService.getAllReminders(authentication.getName())
                        .stream()
                        .map(ReminderResponse::fromReminder)
                        .toList();

        return ResponseEntity.ok(reminders);
    }

    // Cancel a reminder.
    @PatchMapping("/api/reminders/{reminderId}/cancel")
    public ResponseEntity<?> cancelReminder(
            @PathVariable Long reminderId,
            Authentication authentication) {

        try {
            Reminder reminder = reminderService.cancelReminder(
                    reminderId,
                    authentication.getName());

            return ResponseEntity.ok(
                    ReminderResponse.fromReminder(reminder));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Get reminders linked to a particular event occurrence.
    @GetMapping("/api/event-occurrences/{occurrenceId}/reminders")
    public ResponseEntity<?> getOccurrenceReminders(
            @PathVariable Long occurrenceId,
            Authentication authentication) {

        try {
            List<ReminderResponse> reminders =
                    reminderService.getOccurrenceReminders(
                            occurrenceId,
                            authentication.getName())
                            .stream()
                            .map(ReminderResponse::fromReminder)
                            .toList();

            return ResponseEntity.ok(reminders);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
