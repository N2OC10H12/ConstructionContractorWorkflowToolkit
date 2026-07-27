package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ComputedStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
public class ProjectStepSummaryResponse {

    private UUID id;
    private String name;
    private Integer orderIndex;
    private LocalDate deadline;
    private ComputedStatus status;

    private List<ProjectSubstepResponse> substeps;
}