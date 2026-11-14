
package com.opportunity360.action.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.action.dto.EventActionRequest;
import com.opportunity360.action.entity.ActionStatus;
import com.opportunity360.action.entity.EventAction;
import com.opportunity360.action.repository.EventActionRepository;
import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventOccurrenceRepository;
import com.opportunity360.event.repository.EventRepository;

@Service
public class EventActionService {

    private final EventActionRepository actionRepository;
    private final EventRepository eventRepository;
    private final EventOccurrenceRepository occurrenceRepository;

    public EventActionService(
            EventActionRepository actionRepository,
            EventRepository eventRepository,
            EventOccurrenceRepository occurrenceRepository) {

        this.actionRepository = actionRepository;
        this.eventRepository = eventRepository;
        this.occurrenceRepository = occurrenceRepository;
    }

    // Create an action for an event.
    @Transactional
    public EventAction createAction(
            Long eventId, String email, EventActionRequest request) {

        Event event = eventRepository
                .findByIdAndUser_Email(eventId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot add an action to a cancelled event");
        }

        validateRequest(request);

        EventAction action = new EventAction();

        action.setEvent(event);
        action.setTitle(request.getTitle());
        action.setDescription(request.getDescription());
        action.setActionType(request.getActionType());
        action.setDueAt(request.getDueAt());
        action.setStatus(ActionStatus.PENDING);

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
                        "Cannot add an action to a completed or cancelled occurrence");
            }

            action.setOccurrence(occurrence);
        }

        return actionRepository.save(action);
    }

    // Get all actions belonging to a user.
    @Transactional(readOnly = true)
    public List<EventAction> getAllActions(String email) {

        return actionRepository
                .findAllByEvent_User_EmailOrderByDueAtAsc(email);
    }

    // Get actions belonging to a particular event.
    @Transactional(readOnly = true)
    public List<EventAction> getEventActions(
            Long eventId, String email) {

        eventRepository.findByIdAndUser_Email(eventId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Event not found"));

        return actionRepository
                .findAllByEvent_IdAndEvent_User_EmailOrderByDueAtAsc(
                        eventId, email);
    }

    // Get actions belonging to a particular occurrence.
    @Transactional(readOnly = true)
    public List<EventAction> getOccurrenceActions(
            Long occurrenceId, String email) {

        occurrenceRepository
                .findByIdAndEvent_User_Email(occurrenceId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Event occurrence not found"));

        return actionRepository
                .findAllByOccurrence_IdAndEvent_User_EmailOrderByDueAtAsc(
                        occurrenceId, email);
    }

    // Update action details.
    @Transactional
    public EventAction updateAction(
            Long actionId, String email, EventActionRequest request) {

        EventAction action = getOwnedAction(actionId, email);

        if (action.getStatus() == ActionStatus.COMPLETED
                || action.getStatus() == ActionStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Completed or cancelled actions cannot be edited");
        }

        validateRequest(request);

        action.setTitle(request.getTitle());
        action.setDescription(request.getDescription());
        action.setActionType(request.getActionType());
        action.setDueAt(request.getDueAt());

        if (request.getOccurrenceId() == null) {
            action.setOccurrence(null);
        } else {

            EventOccurrence occurrence = occurrenceRepository
                    .findByIdAndEvent_User_Email(
                            request.getOccurrenceId(), email)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Event occurrence not found"));

            if (!occurrence.getEvent().getId()
                    .equals(action.getEvent().getId())) {
                throw new IllegalArgumentException(
                        "Occurrence does not belong to this event");
            }

            if (occurrence.getStatus() != EventStatus.UPCOMING) {
                throw new IllegalArgumentException(
                        "Cannot link an action to a completed or cancelled occurrence");
            }

            action.setOccurrence(occurrence);
        }

        return actionRepository.save(action);
    }

    // Update action status.
    @Transactional
    public EventAction updateActionStatus(
            Long actionId, String email, ActionStatus newStatus) {

        EventAction action = getOwnedAction(actionId, email);

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Action status is required");
        }

        if (action.getStatus() == ActionStatus.COMPLETED
                || action.getStatus() == ActionStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Completed or cancelled actions cannot be changed");
        }

        action.setStatus(newStatus);

        return actionRepository.save(action);
    }

    // Cancel an action.
    @Transactional
    public EventAction cancelAction(Long actionId, String email) {

        EventAction action = getOwnedAction(actionId, email);

        if (action.getStatus() == ActionStatus.COMPLETED) {
            throw new IllegalArgumentException(
                    "A completed action cannot be cancelled");
        }

        action.setStatus(ActionStatus.CANCELLED);

        return actionRepository.save(action);
    }

    // Cancel unfinished actions for an entire event.
    @Transactional
    public void cancelUnfinishedActionsForEvent(Long eventId) {

        List<ActionStatus> unfinishedStatuses = List.of(
                ActionStatus.PENDING,
                ActionStatus.IN_PROGRESS
        );

        List<EventAction> actions =
                actionRepository.findAllByEvent_IdAndStatusIn(
                        eventId, unfinishedStatuses);

        for (EventAction action : actions) {
            action.setStatus(ActionStatus.CANCELLED);
        }

        actionRepository.saveAll(actions);
    }

    // Cancel unfinished actions for one occurrence.
    @Transactional
    public void cancelUnfinishedActionsForOccurrence(
            Long occurrenceId) {

        List<ActionStatus> unfinishedStatuses = List.of(
                ActionStatus.PENDING,
                ActionStatus.IN_PROGRESS
        );

        List<EventAction> actions =
                actionRepository.findAllByOccurrence_IdAndStatusIn(
                        occurrenceId, unfinishedStatuses);

        for (EventAction action : actions) {
            action.setStatus(ActionStatus.CANCELLED);
        }

        actionRepository.saveAll(actions);
    }

    // Verify action ownership.
    private EventAction getOwnedAction(Long actionId, String email) {

        return actionRepository
                .findByIdAndEvent_User_Email(actionId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Action not found"));
    }

    // Validate incoming action details.
    private void validateRequest(EventActionRequest request) {

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "Action title is required");
        }

        if (request.getTitle().length() > 150) {
            throw new IllegalArgumentException(
                    "Action title cannot exceed 150 characters");
        }

        if (request.getDescription() != null
                && request.getDescription().length() > 1000) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 1000 characters");
        }

        if (request.getActionType() == null) {
            throw new IllegalArgumentException(
                    "Action type is required");
        }
    }
}
