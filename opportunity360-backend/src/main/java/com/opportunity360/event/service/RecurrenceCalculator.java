
package com.opportunity360.event.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.opportunity360.event.entity.Event;
import com.opportunity360.event.entity.RecurrenceType;

@Component
public class RecurrenceCalculator {

    // Calculate the next occurrence of an event.
    public Optional<LocalDate> calculateNextOccurrence(
            Event event,
            LocalDate afterDate) {

        LocalDate startDate = event.getEventDate();

        RecurrenceType recurrenceType = event.getRecurrenceType();

        if (recurrenceType == null
                || recurrenceType == RecurrenceType.NONE) {
            return Optional.empty();
        }

        if (afterDate.isBefore(startDate)) {
            return isWithinEndDate(event, startDate);
        }

        LocalDate nextDate;

        switch (recurrenceType) {

            case DAILY:
                long days = ChronoUnit.DAYS.between(
                        startDate, afterDate);

                nextDate = startDate.plusDays(days + 1);
                break;

            case WEEKLY:
                long weeks = ChronoUnit.WEEKS.between(
                        startDate, afterDate);

                nextDate = startDate.plusWeeks(weeks + 1);
                break;

            case MONTHLY:
                nextDate = calculateNextMonthlyDate(
                        startDate, afterDate);
                break;

            case YEARLY:
                nextDate = calculateNextYearlyDate(
                        startDate, afterDate);
                break;

            default:
                return Optional.empty();
        }

        return isWithinEndDate(event, nextDate);
    }

    // Calculate monthly recurrence without losing the original day.
    private LocalDate calculateNextMonthlyDate(
            LocalDate startDate,
            LocalDate afterDate) {

        long months = ChronoUnit.MONTHS.between(
                YearMonth.from(startDate),
                YearMonth.from(afterDate));

        long occurrenceNumber = Math.max(1, months);

        LocalDate candidate;

        while (true) {

            YearMonth targetMonth = YearMonth
                    .from(startDate)
                    .plusMonths(occurrenceNumber);

            int day = Math.min(
                    startDate.getDayOfMonth(),
                    targetMonth.lengthOfMonth());

            candidate = targetMonth.atDay(day);

            if (candidate.isAfter(afterDate)) {
                return candidate;
            }

            occurrenceNumber++;
        }
    }

    // Calculate yearly recurrence, including leap-year dates.
    private LocalDate calculateNextYearlyDate(
            LocalDate startDate,
            LocalDate afterDate) {

        long years = ChronoUnit.YEARS.between(
                startDate, afterDate);

        long occurrenceNumber = Math.max(1, years);

        LocalDate candidate;

        while (true) {

            int targetYear = startDate.getYear()
                    + (int) occurrenceNumber;

            YearMonth targetMonth = YearMonth.of(
                    targetYear,
                    startDate.getMonth());

            int day = Math.min(
                    startDate.getDayOfMonth(),
                    targetMonth.lengthOfMonth());

            candidate = targetMonth.atDay(day);

            if (candidate.isAfter(afterDate)) {
                return candidate;
            }

            occurrenceNumber++;
        }
    }

    // Check whether the next occurrence is within the recurrence end date.
    private Optional<LocalDate> isWithinEndDate(
            Event event,
            LocalDate nextDate) {

        LocalDate endDate = event.getRecurrenceEndDate();

        if (endDate != null && nextDate.isAfter(endDate)) {
            return Optional.empty();
        }

        return Optional.of(nextDate);
    }
}
