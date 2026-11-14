
package com.opportunity360.opportunity.notes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity360.opportunity.notes.OpportunityNote;

public interface OpportunityNoteRepository
        extends JpaRepository<OpportunityNote, Long> {

    List<OpportunityNote> findAllByOpportunity_IdOrderByCreatedAtDesc(
            Long opportunityId);

    Optional<OpportunityNote> findByIdAndOpportunity_Id(
            Long id, Long opportunityId);

    void deleteAllByOpportunity_Id(Long opportunityId);
}
