
package com.opportunity360.notification.dto;

import java.time.LocalDateTime;

import com.opportunity360.notification.entity.InAppNotification;

public class NotificationResponse {

    private Long id;
    private Long reminderId;
    private String title;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public static NotificationResponse fromNotification(
            InAppNotification notification) {

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());

        if (notification.getReminder() != null) {
            response.setReminderId(
                    notification.getReminder().getId()
            );
        }

        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReminderId() {
        return reminderId;
    }

    public void setReminderId(Long reminderId) {
        this.reminderId = reminderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
