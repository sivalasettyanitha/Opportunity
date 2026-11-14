
package com.opportunity360.opportunity.notes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.opportunity360.opportunity.notes.dto.OpportunityNoteRequest;
import com.opportunity360.opportunity.notes.dto.OpportunityNoteResponse;
import com.opportunity360.opportunity.notes.service.OpportunityNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/opportunities/{opportunityId}/notes")
public class OpportunityNoteController {

    private final OpportunityNoteService noteService;

    public OpportunityNoteController(OpportunityNoteService noteService) {
        this.noteService = noteService;
    }

    // Add a note
    @PostMapping
    public ResponseEntity<?> createNote(
            @PathVariable Long opportunityId,
            @Valid @RequestBody OpportunityNoteRequest request,
            Authentication authentication) {

        try {
            OpportunityNoteResponse response = noteService.createNote(
                    opportunityId,
                    authentication.getName(),
                    request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all notes for an opportunity
    @GetMapping
    public ResponseEntity<?> getNotes(
            @PathVariable Long opportunityId,
            Authentication authentication) {

        try {
            List<OpportunityNoteResponse> notes = noteService.getNotes(
                    opportunityId,
                    authentication.getName());

            return ResponseEntity.ok(notes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Update a note
    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(
            @PathVariable Long opportunityId,
            @PathVariable Long noteId,
            @Valid @RequestBody OpportunityNoteRequest request,
            Authentication authentication) {

        try {
            OpportunityNoteResponse response = noteService.updateNote(
                    opportunityId,
                    noteId,
                    authentication.getName(),
                    request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Delete a note
    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(
            @PathVariable Long opportunityId,
            @PathVariable Long noteId,
            Authentication authentication) {

        try {
            noteService.deleteNote(
                    opportunityId,
                    noteId,
                    authentication.getName());

            return ResponseEntity.ok("Note deleted successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
