
package com.opportunity360.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.notification.dto.NotificationDeliveryResponse;
import com.opportunity360.notification.service.NotificationDeliveryService;

@RestController
@RequestMapping("/api/notifications/deliveries")
public class NotificationDeliveryController {

    private final NotificationDeliveryService deliveryService;

    public NotificationDeliveryController(
            NotificationDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // View email delivery history.
    @GetMapping
    public ResponseEntity<List<NotificationDeliveryResponse>>
            getDeliveryHistory(Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                deliveryService.getDeliveryHistory(email));
    }

    // Manually retry a failed email.
    @PostMapping("/{deliveryId}/retry")
    public ResponseEntity<Map<String, Object>> retryDelivery(
            @PathVariable Long deliveryId,
            Authentication authentication) {

        try {

            boolean sent = deliveryService.retryFailedDelivery(
                    deliveryId,
                    authentication.getName());

            if (sent) {
                return ResponseEntity.ok(Map.of(
                        "message", "Email sent successfully",
                        "status", "SENT"
                ));
            }

            return ResponseEntity.status(502).body(Map.of(
                    "message", "Email delivery failed. Please try again later.",
                    "status", "FAILED"
            ));

        } catch (IllegalArgumentException exception) {

            return ResponseEntity.badRequest().body(Map.of(
                    "message", exception.getMessage()
            ));
        }
    }
}
