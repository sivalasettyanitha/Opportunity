
package com.opportunity360.action.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.opportunity360.action.dto.ActionStatusRequest;
import com.opportunity360.action.dto.EventActionRequest;
import com.opportunity360.action.dto.EventActionResponse;
import com.opportunity360.action.entity.EventAction;
import com.opportunity360.action.service.EventActionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/actions")
public class EventActionController {

    private final EventActionService actionService;

    public EventActionController(EventActionService actionService) {
        this.actionService = actionService;
    }

    // Create an action for an event.
    @PostMapping("/events/{eventId}")
    public ResponseEntity<?> createAction(
            @PathVariable Long eventId,
            @Valid @RequestBody EventActionRequest request,
            Authentication authentication) {

        try {
            EventAction action = actionService.createAction(
                    eventId,
                    authentication.getName(),
                    request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EventActionResponse.fromAction(action));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Get all actions belonging to the logged-in user.
    @GetMapping
    public ResponseEntity<List<EventActionResponse>> getAllActions(
            Authentication authentication) {

        List<EventActionResponse> actions =
                actionService.getAllActions(authentication.getName())
                        .stream()
                        .map(EventActionResponse::fromAction)
                        .toList();

        return ResponseEntity.ok(actions);
    }

    // Get actions for a particular event.
    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEventActions(
            @PathVariable Long eventId,
            Authentication authentication) {

        try {
            List<EventActionResponse> actions =
                    actionService.getEventActions(
                            eventId,
                            authentication.getName())
                            .stream()
                            .map(EventActionResponse::fromAction)
                            .toList();

            return ResponseEntity.ok(actions);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get actions for a particular occurrence.
    @GetMapping("/occurrences/{occurrenceId}")
    public ResponseEntity<?> getOccurrenceActions(
            @PathVariable Long occurrenceId,
            Authentication authentication) {

        try {
            List<EventActionResponse> actions =
                    actionService.getOccurrenceActions(
                            occurrenceId,
                            authentication.getName())
                            .stream()
                            .map(EventActionResponse::fromAction)
                            .toList();

            return ResponseEntity.ok(actions);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update action details.
    @PutMapping("/{actionId}")
    public ResponseEntity<?> updateAction(
            @PathVariable Long actionId,
            @Valid @RequestBody EventActionRequest request,
            Authentication authentication) {

        try {
            EventAction action = actionService.updateAction(
                    actionId,
                    authentication.getName(),
                    request);

            return ResponseEntity.ok(
                    EventActionResponse.fromAction(action));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Update action status.
    @PatchMapping("/{actionId}/status")
    public ResponseEntity<?> updateActionStatus(
            @PathVariable Long actionId,
            @Valid @RequestBody ActionStatusRequest request,
            Authentication authentication) {

        try {
            EventAction action = actionService.updateActionStatus(
                    actionId,
                    authentication.getName(),
                    request.getStatus());

            return ResponseEntity.ok(
                    EventActionResponse.fromAction(action));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Cancel an action.
    @PatchMapping("/{actionId}/cancel")
    public ResponseEntity<?> cancelAction(
            @PathVariable Long actionId,
            Authentication authentication) {

        try {
            EventAction action = actionService.cancelAction(
                    actionId,
                    authentication.getName());

            return ResponseEntity.ok(
                    EventActionResponse.fromAction(action));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }
}
