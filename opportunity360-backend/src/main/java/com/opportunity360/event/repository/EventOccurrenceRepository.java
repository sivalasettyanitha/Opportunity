
package com.opportunity360.event.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.opportunity360.event.entity.EventCategory;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;

@Repository
public interface EventOccurrenceRepository
        extends JpaRepository<EventOccurrence, Long> {

    List<EventOccurrence> findAllByEvent_IdOrderByOccurrenceDateAsc(
            Long eventId);

    Optional<EventOccurrence> findByIdAndEvent_User_Email(
            Long id, String email);

    boolean existsByEvent_IdAndOccurrenceDate(
            Long eventId, LocalDate occurrenceDate);

    List<EventOccurrence> findAllByEvent_User_EmailOrderByOccurrenceDateAsc(
            String email);

    List<EventOccurrence>
    findAllByEvent_IdAndStatusAndOccurrenceDateGreaterThanEqual(
            Long eventId, EventStatus status, LocalDate date);

    List<EventOccurrence>
    findAllByEvent_User_EmailAndOccurrenceDateBetweenOrderByOccurrenceDateAsc(
            String email, LocalDate startDate, LocalDate endDate);

    // Paginated calendar search with optional filters.
    @Query("""
            SELECT o
            FROM EventOccurrence o
            JOIN o.event e
            WHERE e.user.email = :email
            AND o.occurrenceDate BETWEEN :startDate AND :endDate
            AND (:category IS NULL OR e.category = :category)
            AND (:status IS NULL OR o.status = :status)
            AND (
                :search IS NULL
                OR :search = ''
                OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(e.description, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
            )
            """)
    Page<EventOccurrence> searchCalendarEvents(
            @Param("email") String email,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("category") EventCategory category,
            @Param("status") EventStatus status,
            @Param("search") String search,
            Pageable pageable);
}
