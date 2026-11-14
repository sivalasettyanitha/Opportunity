
package com.opportunity360.opportunity.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity360.opportunity.history.OpportunityStatusHistory;

public interface OpportunityStatusHistoryRepository
        extends JpaRepository<OpportunityStatusHistory, Long> {

    List<OpportunityStatusHistory>
    findAllByOpportunity_IdOrderByChangedAtDesc(Long opportunityId);

    void deleteAllByOpportunity_Id(Long opportunityId);
}
