
package com.opportunity360.action.dto;

import com.opportunity360.action.entity.ActionStatus;

import jakarta.validation.constraints.NotNull;

public class ActionStatusRequest {

    @NotNull(message = "Action status is required")
    private ActionStatus status;

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }
}
