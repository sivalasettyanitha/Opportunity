
package com.opportunity360.reminder.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventOccurrenceRepository;
import com.opportunity360.event.repository.EventRepository;
import com.opportunity360.reminder.dto.ReminderRequest;
import com.opportunity360.reminder.entity.Reminder;
import com.opportunity360.reminder.entity.ReminderStatus;
import com.opportunity360.reminder.repository.ReminderRepository;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final EventRepository eventRepository;
    private final EventOccurrenceRepository occurrenceRepository;

    public ReminderService(
            ReminderRepository reminderRepository,
            EventRepository eventRepository,
            EventOccurrenceRepository occurrenceRepository) {

        this.reminderRepository = reminderRepository;
        this.eventRepository = eventRepository;
        this.occurrenceRepository = occurrenceRepository;
    }

    @Transactional
    public Reminder createReminder(
            Long eventId, String email, ReminderRequest request) {

        Event event = eventRepository
                .findByIdAndUser_Email(eventId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot create a reminder for a cancelled event");
        }

        if (request.getReminderAt() == null) {
            throw new IllegalArgumentException(
                    "Reminder date and time are required");
        }

        ZoneId eventZone = ZoneId.of(event.getTimezone());
        LocalDateTime currentTime = LocalDateTime.now(eventZone);

        if (!request.getReminderAt().isAfter(currentTime)) {
            throw new IllegalArgumentException(
                    "Reminder date and time must be in the future");
        }

        Reminder reminder = new Reminder();

        reminder.setEvent(event);
        reminder.setReminderAt(request.getReminderAt());
        reminder.setStatus(ReminderStatus.SCHEDULED);

        if (request.getOccurrenceId() != null) {

            EventOccurrence occurrence = occurrenceRepository
                    .findByIdAndEvent_User_Email(
                            request.getOccurrenceId(), email)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Event occurrence not found"));

            if (!occurrence.getEvent().getId().equals(eventId)) {
                throw new IllegalArgumentException(
                        "Occurrence does not belong to this event");
            }

            if (occurrence.getStatus() != EventStatus.UPCOMING) {
                throw new IllegalArgumentException(
                        "Cannot create a reminder for a completed or cancelled occurrence");
            }

            LocalDateTime occurrenceDateTime =
                    occurrence.getEvent().getEventTime() == null
                    ? occurrence.getOccurrenceDate().atStartOfDay()
                    : LocalDateTime.of(
                            occurrence.getOccurrenceDate(),
                            occurrence.getEvent().getEventTime());

            if (!request.getReminderAt().isBefore(occurrenceDateTime)) {
                throw new IllegalArgumentException(
                        "Reminder must be scheduled before the event occurrence");
            }

            reminder.setOccurrence(occurrence);
        }

        return reminderRepository.save(reminder);
    }

    @Transactional(readOnly = true)
    public List<Reminder> getAllReminders(String email) {

        return reminderRepository
                .findAllByEvent_User_EmailOrderByReminderAtAsc(email);
    }

    @Transactional(readOnly = true)
    public List<Reminder> getOccurrenceReminders(
            Long occurrenceId, String email) {

        occurrenceRepository
                .findByIdAndEvent_User_Email(occurrenceId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event occurrence not found"));

        return reminderRepository
                .findAllByOccurrence_IdAndEvent_User_EmailOrderByReminderAtAsc(
                        occurrenceId, email);
    }

    @Transactional
    public Reminder cancelReminder(Long reminderId, String email) {

        Reminder reminder = reminderRepository
                .findByIdAndEvent_User_Email(reminderId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reminder not found"));

        if (reminder.getStatus() == ReminderStatus.SENT
                || reminder.getStatus() == ReminderStatus.FAILED) {

            throw new IllegalArgumentException(
                    "Sent or failed reminders cannot be cancelled");
        }

        reminder.setStatus(ReminderStatus.CANCELLED);

        return reminderRepository.save(reminder);
    }

    // Cancel all scheduled reminders belonging to an event.
    @Transactional
    public void cancelScheduledRemindersForEvent(Long eventId) {

        List<Reminder> reminders =
                reminderRepository.findAllByEvent_IdAndStatus(
                        eventId, ReminderStatus.SCHEDULED);

        for (Reminder reminder : reminders) {
            reminder.setStatus(ReminderStatus.CANCELLED);
        }

        reminderRepository.saveAll(reminders);
    }

    // Cancel scheduled reminders for one occurrence only.
    @Transactional
    public void cancelScheduledRemindersForOccurrence(
            Long occurrenceId) {

        List<Reminder> reminders =
                reminderRepository.findAllByOccurrence_IdAndStatus(
                        occurrenceId, ReminderStatus.SCHEDULED);

        for (Reminder reminder : reminders) {
            reminder.setStatus(ReminderStatus.CANCELLED);
        }

        reminderRepository.saveAll(reminders);
    }
}
