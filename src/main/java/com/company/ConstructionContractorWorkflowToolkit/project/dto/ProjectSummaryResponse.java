package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ComputedStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
public class ProjectSummaryResponse {

    private UUID id;
    private String name;
    private ComputedStatus status;
    private LocalDate projectDeadline;

    private List<ProjectStepSummaryResponse> steps;

    private ProjectOwnerResponse owner;
}