
package com.opportunity360.notification.dto;

import java.time.LocalDateTime;

import com.opportunity360.notification.entity.NotificationPreference;

public class NotificationPreferenceResponse {

    private Long id;
    private boolean emailEnabled;
    private boolean inAppEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NotificationPreferenceResponse fromPreference(
            NotificationPreference preference) {

        NotificationPreferenceResponse response =
                new NotificationPreferenceResponse();

        response.setId(preference.getId());
        response.setEmailEnabled(preference.isEmailEnabled());
        response.setInAppEnabled(preference.isInAppEnabled());
        response.setCreatedAt(preference.getCreatedAt());
        response.setUpdatedAt(preference.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public void setInAppEnabled(boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
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
