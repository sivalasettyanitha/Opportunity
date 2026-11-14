
package com.opportunity360.opportunity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.opportunity360.opportunity.dto.OpportunityRequest;
import com.opportunity360.opportunity.dto.OpportunityResponse;
import com.opportunity360.opportunity.entity.OpportunityStatus;
import com.opportunity360.opportunity.service.OpportunityService;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(
            OpportunityService opportunityService) {

        this.opportunityService = opportunityService;
    }

    // Create an opportunity
    @PostMapping
    public ResponseEntity<?> createOpportunity(
            @Valid @RequestBody OpportunityRequest request,
            Authentication authentication) {

        try {
            OpportunityResponse response =
                    opportunityService.createOpportunity(
                            authentication.getName(), request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get all opportunities
    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAllOpportunities(
            Authentication authentication) {

        List<OpportunityResponse> opportunities =
                opportunityService.getAllOpportunities(
                        authentication.getName());

        return ResponseEntity.ok(opportunities);
    }

    // Get opportunity by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getOpportunityById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            OpportunityResponse response =
                    opportunityService.getOpportunityById(
                            id, authentication.getName());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Update opportunity details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityRequest request,
            Authentication authentication) {

        try {
            OpportunityResponse response =
                    opportunityService.updateOpportunity(
                            id, authentication.getName(), request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Update opportunity status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOpportunityStatus(
            @PathVariable Long id,
            @RequestParam OpportunityStatus status,
            Authentication authentication) {

        try {
            OpportunityResponse response =
                    opportunityService.updateOpportunityStatus(
                            id, authentication.getName(), status);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete opportunity
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOpportunity(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            opportunityService.deleteOpportunity(
                    id, authentication.getName());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Opportunity deleted successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Common error response
    private ResponseEntity<Map<String, String>> errorResponse(
            String message, HttpStatus status) {

        Map<String, String> error = new HashMap<>();
        error.put("message", message);

        return ResponseEntity.status(status).body(error);
    }
}
