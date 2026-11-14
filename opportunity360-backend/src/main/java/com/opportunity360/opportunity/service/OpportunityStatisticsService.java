
package com.opportunity360.opportunity.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity360.opportunity.dto.OpportunityStatisticsResponse;
import com.opportunity360.opportunity.entity.OpportunityStatus;
import com.opportunity360.opportunity.repository.OpportunityRepository;

@Service
public class OpportunityStatisticsService {

    private final OpportunityRepository opportunityRepository;

    public OpportunityStatisticsService(
            OpportunityRepository opportunityRepository) {

        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public OpportunityStatisticsResponse getStatistics(String email) {

        long total = opportunityRepository.countByUser_Email(email);

        long saved = countByStatus(email, OpportunityStatus.SAVED);
        long applied = countByStatus(email, OpportunityStatus.APPLIED);
        long inProgress = countByStatus(email, OpportunityStatus.IN_PROGRESS);
        long selected = countByStatus(email, OpportunityStatus.SELECTED);
        long rejected = countByStatus(email, OpportunityStatus.REJECTED);
        long closed = countByStatus(email, OpportunityStatus.CLOSED);

        return new OpportunityStatisticsResponse(
                total,
                saved,
                applied,
                inProgress,
                selected,
                rejected,
                closed
        );
    }

    private long countByStatus(
            String email, OpportunityStatus status) {

        return opportunityRepository
                .countByUser_EmailAndStatus(email, status);
    }
}
