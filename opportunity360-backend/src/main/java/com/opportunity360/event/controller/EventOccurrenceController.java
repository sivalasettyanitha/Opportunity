
package com.opportunity360.event.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.opportunity360.event.dto.EventOccurrenceResponse;
import com.opportunity360.event.entity.EventOccurrence;
import com.opportunity360.event.service.EventOccurrenceService;

@RestController
@RequestMapping("/api/event-occurrences")
public class EventOccurrenceController {

    private final EventOccurrenceService occurrenceService;

    public EventOccurrenceController(
            EventOccurrenceService occurrenceService) {

        this.occurrenceService = occurrenceService;
    }

    // Get all occurrences belonging to the logged-in user.
    @GetMapping
    public ResponseEntity<List<EventOccurrenceResponse>> getOccurrences(
            Authentication authentication) {

        List<EventOccurrenceResponse> occurrences =
                occurrenceService
                        .getUserOccurrences(authentication.getName())
                        .stream()
                        .map(EventOccurrenceResponse::fromOccurrence)
                        .toList();

        return ResponseEntity.ok(occurrences);
    }

    // Mark an occurrence as completed.
    @PatchMapping("/{occurrenceId}/complete")
    public ResponseEntity<?> completeOccurrence(
            @PathVariable Long occurrenceId,
            Authentication authentication) {

        try {
            EventOccurrence occurrence =
                    occurrenceService.completeOccurrence(
                            occurrenceId,
                            authentication.getName());

            return ResponseEntity.ok(
                    EventOccurrenceResponse.fromOccurrence(occurrence));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }

    // Cancel an individual occurrence.
    @PatchMapping("/{occurrenceId}/cancel")
    public ResponseEntity<?> cancelOccurrence(
            @PathVariable Long occurrenceId,
            Authentication authentication) {

        try {
            EventOccurrence occurrence =
                    occurrenceService.cancelOccurrence(
                            occurrenceId,
                            authentication.getName());

            return ResponseEntity.ok(
                    EventOccurrenceResponse.fromOccurrence(occurrence));

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                    .body(exception.getMessage());
        }
    }
}
