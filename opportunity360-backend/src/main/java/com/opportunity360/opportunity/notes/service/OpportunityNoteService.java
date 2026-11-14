
package com.opportunity360.opportunity.notes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.opportunity.entity.Opportunity;
import com.opportunity360.opportunity.repository.OpportunityRepository;
import com.opportunity360.opportunity.notes.OpportunityNote;
import com.opportunity360.opportunity.notes.dto.OpportunityNoteRequest;
import com.opportunity360.opportunity.notes.dto.OpportunityNoteResponse;
import com.opportunity360.opportunity.notes.repository.OpportunityNoteRepository;

@Service
public class OpportunityNoteService {

    private final OpportunityNoteRepository noteRepository;
    private final OpportunityRepository opportunityRepository;

    public OpportunityNoteService(
            OpportunityNoteRepository noteRepository,
            OpportunityRepository opportunityRepository) {

        this.noteRepository = noteRepository;
        this.opportunityRepository = opportunityRepository;
    }

    // Add a note to an opportunity
    @Transactional
    public OpportunityNoteResponse createNote(
            Long opportunityId,
            String email,
            OpportunityNoteRequest request) {

        Opportunity opportunity = getOwnedOpportunity(opportunityId, email);

        OpportunityNote note = new OpportunityNote();

        note.setOpportunity(opportunity);
        note.setContent(request.getContent().trim());

        if (note.getContent().isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be empty");
        }

        OpportunityNote savedNote = noteRepository.save(note);

        return OpportunityNoteResponse.fromNote(savedNote);
    }

    // Get all notes for an opportunity
    @Transactional(readOnly = true)
    public List<OpportunityNoteResponse> getNotes(
            Long opportunityId,
            String email) {

        getOwnedOpportunity(opportunityId, email);

        return noteRepository
                .findAllByOpportunity_IdOrderByCreatedAtDesc(opportunityId)
                .stream()
                .map(OpportunityNoteResponse::fromNote)
                .toList();
    }

    // Update an existing note
    @Transactional
    public OpportunityNoteResponse updateNote(
            Long opportunityId,
            Long noteId,
            String email,
            OpportunityNoteRequest request) {

        getOwnedOpportunity(opportunityId, email);

        OpportunityNote note = noteRepository
                .findByIdAndOpportunity_Id(noteId, opportunityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Note not found"));

        String content = request.getContent().trim();

        if (content.isEmpty()) {
            throw new IllegalArgumentException("Note content cannot be empty");
        }

        note.setContent(content);

        OpportunityNote updatedNote = noteRepository.save(note);

        return OpportunityNoteResponse.fromNote(updatedNote);
    }

    // Delete a note
    @Transactional
    public void deleteNote(
            Long opportunityId,
            Long noteId,
            String email) {

        getOwnedOpportunity(opportunityId, email);

        OpportunityNote note = noteRepository
                .findByIdAndOpportunity_Id(noteId, opportunityId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Note not found"));

        noteRepository.delete(note);
    }

    // Verify that the opportunity belongs to the logged-in user
    private Opportunity getOwnedOpportunity(
            Long opportunityId,
            String email) {

        return opportunityRepository
                .findByIdAndUser_Email(opportunityId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Opportunity not found"));
    }
}
