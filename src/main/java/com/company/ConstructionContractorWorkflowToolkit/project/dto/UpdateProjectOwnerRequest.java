package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import java.util.UUID;

public class UpdateProjectOwnerRequest {

    private UUID ownerUserId;

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
    }
}