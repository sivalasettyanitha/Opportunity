
package com.opportunity360.notification.dto;

import java.time.LocalDateTime;

import com.opportunity360.notification.entity.NotificationDelivery;

public class NotificationDeliveryResponse {

    private Long id;
    private Long reminderId;
    private String eventTitle;
    private String channel;
    private String status;
    private String recipient;
    private int attempts;
    private LocalDateTime lastAttemptAt;
    private LocalDateTime sentAt;
    private String errorMessage;
    private LocalDateTime createdAt;

    public static NotificationDeliveryResponse fromDelivery(
            NotificationDelivery delivery) {

        NotificationDeliveryResponse response =
                new NotificationDeliveryResponse();

        response.setId(delivery.getId());
        response.setReminderId(delivery.getReminder().getId());
        response.setEventTitle(
                delivery.getReminder().getEvent().getTitle());

        response.setChannel(delivery.getChannel().name());
        response.setStatus(delivery.getStatus().name());
        response.setRecipient(delivery.getRecipient());
        response.setAttempts(delivery.getAttempts());
        response.setLastAttemptAt(delivery.getLastAttemptAt());
        response.setSentAt(delivery.getSentAt());
        response.setErrorMessage(delivery.getErrorMessage());
        response.setCreatedAt(delivery.getCreatedAt());

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

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public LocalDateTime getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(LocalDateTime lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
