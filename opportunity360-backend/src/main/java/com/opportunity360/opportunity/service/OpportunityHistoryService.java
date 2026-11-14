
package com.opportunity360.opportunity.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.opportunity.dto.OpportunityHistoryResponse;
import com.opportunity360.opportunity.history.repository.OpportunityStatusHistoryRepository;
import com.opportunity360.opportunity.repository.OpportunityRepository;

@Service
public class OpportunityHistoryService {

    private final OpportunityStatusHistoryRepository historyRepository;
    private final OpportunityRepository opportunityRepository;

    public OpportunityHistoryService(
            OpportunityStatusHistoryRepository historyRepository,
            OpportunityRepository opportunityRepository) {

        this.historyRepository = historyRepository;
        this.opportunityRepository = opportunityRepository;
    }

    // Get status history for an opportunity owned by the user
    @Transactional(readOnly = true)
    public List<OpportunityHistoryResponse> getOpportunityHistory(
            Long opportunityId, String email) {

        // Verify that the opportunity belongs to this user
        opportunityRepository
                .findByIdAndUser_Email(opportunityId, email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Opportunity not found"));

        // Retrieve history, newest first
        return historyRepository
                .findAllByOpportunity_IdOrderByChangedAtDesc(
                        opportunityId)
                .stream()
                .map(OpportunityHistoryResponse::fromHistory)
                .toList();
    }
}
