
package com.opportunity360.event.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.dto.EventRequest;
import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.entity.RecurrenceType;
import com.opportunity360.event.repository.EventRepository;
import com.opportunity360.reminder.service.ReminderService;
import com.opportunity360.action.service.EventActionService;
import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RecurrenceCalculator recurrenceCalculator;
    private final EventOccurrenceService occurrenceService;
    private final ReminderService reminderService;
    private final EventActionService actionService;

    public EventService(
            EventRepository eventRepository,
            UserRepository userRepository,
            RecurrenceCalculator recurrenceCalculator,
            EventOccurrenceService occurrenceService,
            ReminderService reminderService,
            EventActionService actionService) {

        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.recurrenceCalculator = recurrenceCalculator;
        this.occurrenceService = occurrenceService;
        this.reminderService = reminderService;
        this.actionService = actionService;
    }

    // Create an event.
    @Transactional
    public Event createEvent(String email, EventRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        validateRecurrence(request);

        Event event = new Event();

        event.setUser(user);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setTimezone(getTimezone(request));
        event.setStatus(EventStatus.UPCOMING);

        event.setRecurrenceType(getRecurrenceType(request));
        event.setRecurrenceEndDate(request.getRecurrenceEndDate());

        event.setNextOccurrenceDate(
                recurrenceCalculator.calculateNextOccurrence(
                        event, event.getEventDate()).orElse(null));

        Event savedEvent = eventRepository.save(event);

        occurrenceService.createOccurrence(
                savedEvent, savedEvent.getEventDate());

        return savedEvent;
    }

    // Get all events belonging to a user.
    @Transactional(readOnly = true)
    public List<Event> getAllEvents(String email) {

        return eventRepository
                .findAllByUser_EmailOrderByEventDateAscEventTimeAsc(email);
    }

    // Get a particular event.
    @Transactional(readOnly = true)
    public Event getEventById(Long id, String email) {

        return eventRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));
    }

    // Update an event.
    @Transactional
    public Event updateEvent(
            Long id, String email, EventRequest request) {

        Event event = getEventById(id, email);

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "A cancelled event cannot be updated");
        }

        validateRecurrence(request);

        // Cancel old scheduled reminders.
        reminderService.cancelScheduledRemindersForEvent(id);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setTimezone(getTimezone(request));

        event.setRecurrenceType(getRecurrenceType(request));
        event.setRecurrenceEndDate(request.getRecurrenceEndDate());

        event.setNextOccurrenceDate(
                recurrenceCalculator.calculateNextOccurrence(
                        event, event.getEventDate()).orElse(null));

        Event updatedEvent = eventRepository.save(event);

        // Reset upcoming occurrence records.
        occurrenceService.resetUpcomingOccurrences(updatedEvent);

        return updatedEvent;
    }

    // Cancel an entire event.
    @Transactional
    public Event cancelEvent(Long id, String email) {

        Event event = getEventById(id, email);

        event.setStatus(EventStatus.CANCELLED);
        event.setNextOccurrenceDate(null);

        // Cancel all scheduled reminders.
        reminderService.cancelScheduledRemindersForEvent(id);

        // Cancel unfinished actions linked directly to the event.
        actionService.cancelUnfinishedActionsForEvent(id);

        // Cancel upcoming occurrences and their associated tasks.
        occurrenceService.cancelUpcomingOccurrences(event);

        return eventRepository.save(event);
    }

    private String getTimezone(EventRequest request) {

        return request.getTimezone() == null
                || request.getTimezone().isBlank()
                ? "Asia/Kolkata"
                : request.getTimezone();
    }

    private RecurrenceType getRecurrenceType(EventRequest request) {

        return request.getRecurrenceType() == null
                ? RecurrenceType.NONE
                : request.getRecurrenceType();
    }

    private void validateRecurrence(EventRequest request) {

        RecurrenceType recurrenceType = getRecurrenceType(request);

        if (recurrenceType == RecurrenceType.NONE
                && request.getRecurrenceEndDate() != null) {

            throw new IllegalArgumentException(
                    "Recurrence end date is not required for a one-time event");
        }

        if (recurrenceType != RecurrenceType.NONE
                && request.getRecurrenceEndDate() == null) {

            throw new IllegalArgumentException(
                    "Recurrence end date is required for recurring events");
        }

        if (recurrenceType != RecurrenceType.NONE
                && request.getRecurrenceEndDate()
                        .isBefore(request.getEventDate())) {

            throw new IllegalArgumentException(
                    "Recurrence end date cannot be before event date");
        }
    }
}
