
package com.opportunity360.event.dto;

import java.time.LocalDate;

import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.entity.EventStatus;

public class EventOccurrenceResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDate occurrenceDate;
    private EventStatus status;

    public static EventOccurrenceResponse fromOccurrence(
            EventOccurrence occurrence) {

        EventOccurrenceResponse response =
                new EventOccurrenceResponse();

        response.setId(occurrence.getId());
        response.setEventId(occurrence.getEvent().getId());
        response.setEventTitle(occurrence.getEvent().getTitle());
        response.setOccurrenceDate(occurrence.getOccurrenceDate());
        response.setStatus(occurrence.getStatus());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public LocalDate getOccurrenceDate() {
        return occurrenceDate;
    }

    public void setOccurrenceDate(LocalDate occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
