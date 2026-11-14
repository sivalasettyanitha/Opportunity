
package com.opportunity360.event.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;
import com.opportunity360.event.entity.RecurrenceType;
import com.opportunity360.event.repository.EventRepository;

@Component
public class RecurringEventScheduler {

    private static final int DAYS_TO_GENERATE = 90;
    private static final int MAX_OCCURRENCES_PER_EVENT = 500;

    private final EventRepository eventRepository;
    private final RecurrenceCalculator recurrenceCalculator;
    private final EventOccurrenceService occurrenceService;

    public RecurringEventScheduler(
            EventRepository eventRepository,
            RecurrenceCalculator recurrenceCalculator,
            EventOccurrenceService occurrenceService) {

        this.eventRepository = eventRepository;
        this.recurrenceCalculator = recurrenceCalculator;
        this.occurrenceService = occurrenceService;
    }

    // Runs every day at midnight India time.
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void generateFutureOccurrences() {

        List<RecurrenceType> recurrenceTypes = List.of(
                RecurrenceType.DAILY,
                RecurrenceType.WEEKLY,
                RecurrenceType.MONTHLY,
                RecurrenceType.YEARLY
        );

        List<Event> recurringEvents =
                eventRepository.findAllByRecurrenceTypeInAndStatus(
                        recurrenceTypes,
                        EventStatus.UPCOMING
                );

        for (Event event : recurringEvents) {

            try {
                // Get the current date in this event's timezone.
                ZoneId eventZone = ZoneId.of(event.getTimezone());

                LocalDate today = LocalDate.now(eventZone);

                LocalDate generationLimit =
                        today.plusDays(DAYS_TO_GENERATE);

                LocalDate nextDate = event.getNextOccurrenceDate();

                int processedCount = 0;

                while (nextDate != null
                        && !nextDate.isAfter(generationLimit)
                        && processedCount < MAX_OCCURRENCES_PER_EVENT) {

                    // Save the occurrence without creating duplicates.
                    EventOccurrence occurrence =
                            occurrenceService.createOccurrence(
                                    event, nextDate);

                    // Calculate the next scheduled occurrence.
                    nextDate = recurrenceCalculator
                            .calculateNextOccurrence(event, nextDate)
                            .orElse(null);

                    event.setNextOccurrenceDate(nextDate);

                    processedCount++;
                }

                eventRepository.save(event);

            } catch (DateTimeException exception) {

                // Skip events with invalid timezone configuration.
                System.err.println(
                        "Invalid timezone for event ID "
                                + event.getId() + ": "
                                + event.getTimezone());
            }
        }
    }
}
