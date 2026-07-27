package com.company.ConstructionContractorWorkflowToolkit.audit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProjectAuditLogResponse {

    private String action;
    private String targetType;
    private UUID targetId;
    private UUID actorUserId;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
    private String actorUsername;
}