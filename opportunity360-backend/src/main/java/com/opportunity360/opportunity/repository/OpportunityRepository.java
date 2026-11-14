
package com.opportunity360.opportunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opportunity360.opportunity.entity.Opportunity;
import com.opportunity360.opportunity.entity.OpportunityStatus;

public interface OpportunityRepository
        extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findAllByUser_EmailOrderByCreatedAtDesc(
            String email);

    Optional<Opportunity> findByIdAndUser_Email(
            Long id, String email);

    List<Opportunity> findAllByUser_EmailAndStatus(
            String email, OpportunityStatus status);

    long countByUser_Email(String email);

    long countByUser_EmailAndStatus(
            String email, OpportunityStatus status);

}
