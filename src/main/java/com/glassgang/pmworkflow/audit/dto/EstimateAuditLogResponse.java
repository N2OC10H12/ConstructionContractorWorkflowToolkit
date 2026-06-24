package com.glassgang.pmworkflow.audit.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EstimateAuditLogResponse {

    private String action;

    private String targetType;

    private UUID targetId;

    private UUID actorUserId;

    private String actorUsername;

    private String oldValue;

    private String newValue;

    private String message;

    private LocalDateTime createdAt;
}