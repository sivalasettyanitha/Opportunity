
package com.opportunity360.opportunity.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OpportunityNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(max = 2000, message = "Note cannot exceed 2000 characters")
    private String content;

    public OpportunityNoteRequest() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
