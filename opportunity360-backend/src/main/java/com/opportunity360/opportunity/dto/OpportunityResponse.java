
package com.opportunity360.opportunity.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.opportunity360.opportunity.entity.Opportunity;
import com.opportunity360.opportunity.entity.OpportunityStatus;

public class OpportunityResponse {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String employmentType;
    private String applicationUrl;
    private LocalDate deadline;
    private OpportunityStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OpportunityResponse() {
    }

    public static OpportunityResponse fromOpportunity(
            Opportunity opportunity) {

        OpportunityResponse response = new OpportunityResponse();

        response.setId(opportunity.getId());
        response.setTitle(opportunity.getTitle());
        response.setCompany(opportunity.getCompany());
        response.setLocation(opportunity.getLocation());
        response.setDescription(opportunity.getDescription());
        response.setEmploymentType(opportunity.getEmploymentType());
        response.setApplicationUrl(opportunity.getApplicationUrl());
        response.setDeadline(opportunity.getDeadline());
        response.setStatus(opportunity.getStatus());
        response.setCreatedAt(opportunity.getCreatedAt());
        response.setUpdatedAt(opportunity.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public OpportunityStatus getStatus() {
        return status;
    }

    public void setStatus(OpportunityStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
