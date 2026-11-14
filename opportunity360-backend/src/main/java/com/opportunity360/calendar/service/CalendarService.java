
package com.opportunity360.calendar.service;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.calendar.dto.CalendarEventResponse;
import com.opportunity360.event.entity.EventCategory;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.repository.EventOccurrenceRepository;

@Service
public class CalendarService {

    private final EventOccurrenceRepository occurrenceRepository;

    public CalendarService(
            EventOccurrenceRepository occurrenceRepository) {
        this.occurrenceRepository = occurrenceRepository;
    }

    @Transactional(readOnly = true)
    public Page<CalendarEventResponse> getCalendarEvents(
            String email,
            LocalDate startDate,
            LocalDate endDate,
            EventCategory category,
            EventStatus status,
            String search,
            int page,
            int size) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100");
        }

        String searchTerm = search == null || search.isBlank()
                ? null
                : search.trim().toLowerCase(Locale.ROOT);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "occurrenceDate"));

        return occurrenceRepository.searchCalendarEvents(
                email,
                startDate,
                endDate,
                category,
                status,
                searchTerm,
                pageable
        ).map(CalendarEventResponse::fromOccurrence);
    }
}
