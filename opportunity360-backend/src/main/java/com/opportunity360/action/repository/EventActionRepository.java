
package com.opportunity360.action.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity360.action.entity.EventAction;
import com.opportunity360.action.entity.ActionStatus;
import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventOccurrence;

@Repository
public interface EventActionRepository
        extends JpaRepository<EventAction, Long> {

    List<EventAction> findAllByEvent_User_EmailOrderByDueAtAsc(
            String email);

    Optional<EventAction> findByIdAndEvent_User_Email(
            Long id, String email);

    List<EventAction> findAllByEvent_IdAndEvent_User_EmailOrderByDueAtAsc(
            Long eventId, String email);

    List<EventAction>
            findAllByOccurrence_IdAndEvent_User_EmailOrderByDueAtAsc(
                    Long occurrenceId, String email);

    List<EventAction> findAllByEvent_User_EmailAndStatus(
            String email, ActionStatus status);

    List<EventAction> findAllByEvent(Event event);

    List<EventAction> findAllByOccurrence(EventOccurrence occurrence);

    // Find unfinished actions belonging to an event.
    List<EventAction> findAllByEvent_IdAndStatusIn(
            Long eventId, List<ActionStatus> statuses);

    // Find unfinished actions belonging to one occurrence.
    List<EventAction> findAllByOccurrence_IdAndStatusIn(
            Long occurrenceId, List<ActionStatus> statuses);
}
