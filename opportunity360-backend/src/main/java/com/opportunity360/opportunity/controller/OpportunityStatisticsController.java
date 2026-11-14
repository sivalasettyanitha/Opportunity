
package com.opportunity360.opportunity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opportunity360.opportunity.dto.OpportunityStatisticsResponse;
import com.opportunity360.opportunity.service.OpportunityStatisticsService;

@RestController
@RequestMapping("/api/opportunities/statistics")
public class OpportunityStatisticsController {

    private final OpportunityStatisticsService statisticsService;

    public OpportunityStatisticsController(
            OpportunityStatisticsService statisticsService) {

        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<OpportunityStatisticsResponse> getStatistics(
            Authentication authentication) {

        OpportunityStatisticsResponse statistics =
                statisticsService.getStatistics(
                        authentication.getName());

        return ResponseEntity.ok(statistics);
    }
}
