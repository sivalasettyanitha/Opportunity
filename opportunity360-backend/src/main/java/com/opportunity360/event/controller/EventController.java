
package com.opportunity360.event.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.opportunity360.event.dto.EventRequest;
import com.opportunity360.event.dto.EventResponse;
import com.opportunity360.event.entity.Event;
import com.opportunity360.event.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // 1. Create a new event
    @PostMapping
    public ResponseEntity<?> createEvent(
            @Valid @RequestBody EventRequest request,
            Authentication authentication) {

        try {
            Event event = eventService.createEvent(
                    authentication.getName(), request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EventResponse.fromEvent(event));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // 2. Get all events of the logged-in user
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(
            Authentication authentication) {

        List<EventResponse> events = eventService
                .getAllEvents(authentication.getName())
                .stream()
                .map(EventResponse::fromEvent)
                .toList();

        return ResponseEntity.ok(events);
    }

    // 3. Get one event by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            Event event = eventService.getEventById(
                    id, authentication.getName());

            return ResponseEntity.ok(
                    EventResponse.fromEvent(event));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // 4. Update an event
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequest request,
            Authentication authentication) {

        try {
            Event event = eventService.updateEvent(
                    id, authentication.getName(), request);

            return ResponseEntity.ok(
                    EventResponse.fromEvent(event));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // 5. Cancel an event
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelEvent(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            Event event = eventService.cancelEvent(
                    id, authentication.getName());

            return ResponseEntity.ok(
                    EventResponse.fromEvent(event));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
