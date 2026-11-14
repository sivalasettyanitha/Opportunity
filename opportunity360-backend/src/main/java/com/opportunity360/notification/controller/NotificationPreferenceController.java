
package com.opportunity360.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.notification.dto.NotificationPreferenceRequest;
import com.opportunity360.notification.dto.NotificationPreferenceResponse;
import com.opportunity360.notification.service.NotificationPreferenceService;

@RestController
@RequestMapping("/api/notifications/preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    public NotificationPreferenceController(
            NotificationPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    // Get notification preferences.
    @GetMapping
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                preferenceService.getPreferences(email));
    }

    // Update notification preferences.
    @PutMapping
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @RequestBody NotificationPreferenceRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                preferenceService.updatePreferences(email, request));
    }
}
