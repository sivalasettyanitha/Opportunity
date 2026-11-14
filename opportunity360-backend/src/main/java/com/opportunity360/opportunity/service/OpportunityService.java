
package com.opportunity360.opportunity.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.opportunity.dto.OpportunityRequest;
import com.opportunity360.opportunity.dto.OpportunityResponse;
import com.opportunity360.opportunity.entity.Opportunity;
import com.opportunity360.opportunity.entity.OpportunityStatus;
import com.opportunity360.opportunity.history.OpportunityStatusHistory;
import com.opportunity360.opportunity.history.repository.OpportunityStatusHistoryRepository;
import com.opportunity360.opportunity.notes.repository.OpportunityNoteRepository;
import com.opportunity360.opportunity.repository.OpportunityRepository;
import com.opportunity360.user.entity.User;
import com.opportunity360.user.repository.UserRepository;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final OpportunityStatusHistoryRepository historyRepository;
    private final OpportunityNoteRepository noteRepository;

    public OpportunityService(
            OpportunityRepository opportunityRepository,
            UserRepository userRepository,
            OpportunityStatusHistoryRepository historyRepository,
            OpportunityNoteRepository noteRepository) {

        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.noteRepository = noteRepository;
    }

    // Create a new opportunity
    @Transactional
    public OpportunityResponse createOpportunity(
            String email,
            OpportunityRequest request) {

        User user = getUser(email);

        Opportunity opportunity = new Opportunity();

        opportunity.setUser(user);
        opportunity.setTitle(request.getTitle());
        opportunity.setCompany(request.getCompany());
        opportunity.setLocation(request.getLocation());
        opportunity.setDescription(request.getDescription());
        opportunity.setEmploymentType(request.getEmploymentType());
        opportunity.setApplicationUrl(request.getApplicationUrl());
        opportunity.setDeadline(request.getDeadline());
        opportunity.setStatus(OpportunityStatus.SAVED);

        Opportunity savedOpportunity =
                opportunityRepository.save(opportunity);

        // Record the initial application status
        saveStatusHistory(
                savedOpportunity,
                null,
                OpportunityStatus.SAVED
        );

        return OpportunityResponse.fromOpportunity(savedOpportunity);
    }

    // Get all opportunities belonging to the logged-in user
    @Transactional(readOnly = true)
    public List<OpportunityResponse> getAllOpportunities(String email) {

        return opportunityRepository
                .findAllByUser_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(OpportunityResponse::fromOpportunity)
                .toList();
    }

    // Get one opportunity
    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunityById(
            Long opportunityId,
            String email) {

        Opportunity opportunity =
                getOwnedOpportunity(opportunityId, email);

        return OpportunityResponse.fromOpportunity(opportunity);
    }

    // Update opportunity details
    @Transactional
    public OpportunityResponse updateOpportunity(
            Long opportunityId,
            String email,
            OpportunityRequest request) {

        Opportunity opportunity =
                getOwnedOpportunity(opportunityId, email);

        opportunity.setTitle(request.getTitle());
        opportunity.setCompany(request.getCompany());
        opportunity.setLocation(request.getLocation());
        opportunity.setDescription(request.getDescription());
        opportunity.setEmploymentType(request.getEmploymentType());
        opportunity.setApplicationUrl(request.getApplicationUrl());
        opportunity.setDeadline(request.getDeadline());

        Opportunity updatedOpportunity =
                opportunityRepository.save(opportunity);

        return OpportunityResponse.fromOpportunity(updatedOpportunity);
    }

    // Update application status and record status history
    @Transactional
    public OpportunityResponse updateOpportunityStatus(
            Long opportunityId,
            String email,
            OpportunityStatus newStatus) {

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Opportunity status is required"
            );
        }

        Opportunity opportunity =
                getOwnedOpportunity(opportunityId, email);

        OpportunityStatus previousStatus = opportunity.getStatus();

        if (previousStatus != newStatus) {

            opportunity.setStatus(newStatus);

            Opportunity updatedOpportunity =
                    opportunityRepository.save(opportunity);

            saveStatusHistory(
                    updatedOpportunity,
                    previousStatus,
                    newStatus
            );
        }

        return OpportunityResponse.fromOpportunity(opportunity);
    }

    // Delete an opportunity and its related records safely
    @Transactional
    public void deleteOpportunity(
            Long opportunityId,
            String email) {

        Opportunity opportunity =
                getOwnedOpportunity(opportunityId, email);

        // Delete application notes first
        noteRepository.deleteAllByOpportunity_Id(opportunityId);

        // Delete application status history
        historyRepository.deleteAllByOpportunity_Id(opportunityId);

        // Delete the opportunity
        opportunityRepository.delete(opportunity);
    }

    // Save application status history
    private void saveStatusHistory(
            Opportunity opportunity,
            OpportunityStatus previousStatus,
            OpportunityStatus newStatus) {

        OpportunityStatusHistory history =
                new OpportunityStatusHistory();

        history.setOpportunity(opportunity);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);

        historyRepository.save(history);
    }

    // Find user by email
    private User getUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));
    }

    // Verify opportunity ownership
    private Opportunity getOwnedOpportunity(
            Long opportunityId,
            String email) {

        return opportunityRepository
                .findByIdAndUser_Email(opportunityId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Opportunity not found"
                        ));
    }
}
