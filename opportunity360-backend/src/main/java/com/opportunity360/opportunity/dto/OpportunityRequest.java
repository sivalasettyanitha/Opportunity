
package com.opportunity360.opportunity.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OpportunityRequest {

    @NotBlank(message = "Job title is required")
    @Size(max = 150)
    private String title;

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String company;

    @Size(max = 150)
    private String location;

    @Size(max = 1000)
    private String description;

    @Size(max = 50)
    private String employmentType;

    @Size(max = 500)
    private String applicationUrl;

    private LocalDate deadline;

    public OpportunityRequest() {
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
}
