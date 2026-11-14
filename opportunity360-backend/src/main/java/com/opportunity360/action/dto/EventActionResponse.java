
package com.opportunity360.action.dto;

import java.time.LocalDateTime;

import com.opportunity360.action.entity.ActionStatus;
import com.opportunity360.action.entity.ActionType;
import com.opportunity360.action.entity.EventAction;

public class EventActionResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;

    private Long occurrenceId;

    private String title;
    private String description;

    private ActionType actionType;
    private ActionStatus status;

    private LocalDateTime dueAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventActionResponse fromAction(EventAction action) {

        EventActionResponse response = new EventActionResponse();

        response.setId(action.getId());
        response.setEventId(action.getEvent().getId());
        response.setEventTitle(action.getEvent().getTitle());

        if (action.getOccurrence() != null) {
            response.setOccurrenceId(
                    action.getOccurrence().getId());
        }

        response.setTitle(action.getTitle());
        response.setDescription(action.getDescription());
        response.setActionType(action.getActionType());
        response.setStatus(action.getStatus());
        response.setDueAt(action.getDueAt());
        response.setCreatedAt(action.getCreatedAt());
        response.setUpdatedAt(action.getUpdatedAt());

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

    public Long getOccurrenceId() {
        return occurrenceId;
    }

    public void setOccurrenceId(Long occurrenceId) {
        this.occurrenceId = occurrenceId;
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

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
