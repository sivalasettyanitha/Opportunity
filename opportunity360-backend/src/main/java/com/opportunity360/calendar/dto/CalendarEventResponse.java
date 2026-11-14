
package com.opportunity360.calendar.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.opportunity360.event.entity.EventOccurrence;

public class CalendarEventResponse {

    private Long occurrenceId;
    private Long eventId;
    private String title;
    private String description;
    private String category;
    private LocalDate occurrenceDate;
    private LocalTime eventTime;
    private String timezone;
    private String status;
    private String recurrenceType;

    public CalendarEventResponse() {
    }

    public static CalendarEventResponse fromOccurrence(
            EventOccurrence occurrence) {

        CalendarEventResponse response =
                new CalendarEventResponse();

        response.setOccurrenceId(occurrence.getId());
        response.setEventId(occurrence.getEvent().getId());
        response.setTitle(occurrence.getEvent().getTitle());
        response.setDescription(
                occurrence.getEvent().getDescription());
        response.setCategory(
                occurrence.getEvent().getCategory().name());
        response.setOccurrenceDate(
                occurrence.getOccurrenceDate());
        response.setEventTime(
                occurrence.getEvent().getEventTime());
        response.setTimezone(
                occurrence.getEvent().getTimezone());
        response.setStatus(occurrence.getStatus().name());
        response.setRecurrenceType(
                occurrence.getEvent().getRecurrenceType().name());

        return response;
    }

    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(LocalDate occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
}
