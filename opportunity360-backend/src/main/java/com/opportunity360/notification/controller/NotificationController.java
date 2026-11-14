
package com.opportunity360.notification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.opportunity360.notification.dto.NotificationResponse;
import com.opportunity360.notification.entity.InAppNotification;
import com.opportunity360.notification.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // 1. Get all notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            Authentication authentication) {

        List<NotificationResponse> notifications =
                notificationService
                        .getAllNotifications(authentication.getName())
                        .stream()
                        .map(NotificationResponse::fromNotification)
                        .toList();

        return ResponseEntity.ok(notifications);
    }

    // 2. Get unread notification count
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        long count = notificationService.getUnreadCount(
                authentication.getName()
        );

        return ResponseEntity.ok(count);
    }

    // 3. Mark notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        try {

            InAppNotification notification =
                    notificationService.markAsRead(
                            notificationId,
                            authentication.getName()
                    );

            return ResponseEntity.ok(
                    NotificationResponse.fromNotification(notification)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
