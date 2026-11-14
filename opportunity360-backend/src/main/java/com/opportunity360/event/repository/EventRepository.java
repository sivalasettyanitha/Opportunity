
package com.opportunity360.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.entity.RecurrenceType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByUser_EmailOrderByEventDateAscEventTimeAsc(
            String email);

    Optional<Event> findByIdAndUser_Email(
            Long id, String email);

    // Find recurring events that still need future occurrences generated.
    List<Event> findAllByRecurrenceTypeInAndStatus(
            List<RecurrenceType> recurrenceTypes,
            EventStatus status);
}
