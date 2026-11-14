
package com.opportunity360.event.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventOccurrenceRepository;
import com.opportunity360.reminder.service.ReminderService;
import com.opportunity360.action.service.EventActionService;

@Service
public class EventOccurrenceService {

    private final EventOccurrenceRepository occurrenceRepository;
    private final ReminderService reminderService;
    private final EventActionService actionService;

    public EventOccurrenceService(
            EventOccurrenceRepository occurrenceRepository,
            ReminderService reminderService,
            EventActionService actionService) {

        this.occurrenceRepository = occurrenceRepository;
        this.reminderService = reminderService;
        this.actionService = actionService;
    }

    // Create an occurrence without creating duplicates.
    @Transactional
    public EventOccurrence createOccurrence(
            Event event, LocalDate occurrenceDate) {

        return occurrenceRepository
                .findAllByEvent_IdOrderByOccurrenceDateAsc(event.getId())
                .stream()
                .filter(occurrence ->
                        occurrence.getOccurrenceDate().equals(occurrenceDate))
                .findFirst()
                .orElseGet(() -> {
                    EventOccurrence occurrence = new EventOccurrence();
                    occurrence.setEvent(event);
                    occurrence.setOccurrenceDate(occurrenceDate);
                    occurrence.setStatus(EventStatus.UPCOMING);

                    return occurrenceRepository.save(occurrence);
                });
    }

    // Get all occurrences of an event.
    @Transactional(readOnly = true)
    public List<EventOccurrence> getEventOccurrences(Long eventId) {
        return occurrenceRepository
                .findAllByEvent_IdOrderByOccurrenceDateAsc(eventId);
    }

    // Get all occurrences belonging to a user.
    @Transactional(readOnly = true)
    public List<EventOccurrence> getUserOccurrences(String email) {
        return occurrenceRepository
                .findAllByEvent_User_EmailOrderByOccurrenceDateAsc(email);
    }

    // Mark an occurrence as completed.
    @Transactional
    public EventOccurrence completeOccurrence(
            Long occurrenceId, String email) {

        EventOccurrence occurrence =
                getOwnedOccurrence(occurrenceId, email);

        if (occurrence.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "A cancelled occurrence cannot be completed");
        }

        occurrence.setStatus(EventStatus.COMPLETED);

        reminderService.cancelScheduledRemindersForOccurrence(
                occurrenceId);

        actionService.cancelUnfinishedActionsForOccurrence(
                occurrenceId);

        return occurrenceRepository.save(occurrence);
    }

    // Cancel an individual occurrence.
    @Transactional
    public EventOccurrence cancelOccurrence(
            Long occurrenceId, String email) {

        EventOccurrence occurrence =
                getOwnedOccurrence(occurrenceId, email);

        if (occurrence.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "A completed occurrence cannot be cancelled");
        }

        occurrence.setStatus(EventStatus.CANCELLED);

        reminderService.cancelScheduledRemindersForOccurrence(
                occurrenceId);

        actionService.cancelUnfinishedActionsForOccurrence(
                occurrenceId);

        return occurrenceRepository.save(occurrence);
    }

    // Cancel all upcoming occurrences of an event.
    @Transactional
    public void cancelUpcomingOccurrences(Event event) {

        List<EventOccurrence> occurrences =
                occurrenceRepository
                        .findAllByEvent_IdOrderByOccurrenceDateAsc(
                                event.getId());

        for (EventOccurrence occurrence : occurrences) {

            if (occurrence.getStatus() == EventStatus.UPCOMING) {

                occurrence.setStatus(EventStatus.CANCELLED);

                reminderService.cancelScheduledRemindersForOccurrence(
                        occurrence.getId());

                actionService.cancelUnfinishedActionsForOccurrence(
                        occurrence.getId());
            }
        }

        occurrenceRepository.saveAll(occurrences);
    }

    // Reset upcoming occurrences after an event is edited.
    @Transactional
    public void resetUpcomingOccurrences(Event event) {

        cancelUpcomingOccurrences(event);

        createOccurrence(event, event.getEventDate());
    }

    // Verify occurrence ownership.
    private EventOccurrence getOwnedOccurrence(
            Long occurrenceId, String email) {

        return occurrenceRepository
                .findByIdAndEvent_User_Email(occurrenceId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Occurrence not found"));
    }
}
