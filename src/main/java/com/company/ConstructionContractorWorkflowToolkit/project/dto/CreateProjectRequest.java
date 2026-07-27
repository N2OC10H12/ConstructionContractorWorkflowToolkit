package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateProjectRequest {

    private String name;
    private UUID ownerUserId;
    private LocalDate planningDeadline;
    private LocalDate projectDeadline;
}