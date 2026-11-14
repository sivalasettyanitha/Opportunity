
package com.opportunity360.opportunity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.opportunity.dto.OpportunityHistoryResponse;
import com.opportunity360.opportunity.service.OpportunityHistoryService;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityHistoryController {

    private final OpportunityHistoryService historyService;

    public OpportunityHistoryController(
            OpportunityHistoryService historyService) {

        this.historyService = historyService;
    }

    // Get status history for a particular opportunity
    @GetMapping("/{opportunityId}/history")
    public ResponseEntity<?> getOpportunityHistory(
            @PathVariable Long opportunityId,
            Authentication authentication) {

        try {
            List<OpportunityHistoryResponse> history =
                    historyService.getOpportunityHistory(
                            opportunityId,
                            authentication.getName()
                    );

            return ResponseEntity.ok(history);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
