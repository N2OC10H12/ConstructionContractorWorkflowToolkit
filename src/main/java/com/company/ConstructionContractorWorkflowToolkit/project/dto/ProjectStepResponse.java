package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ComputedStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectStepResponse {

    private ComputedStatus status;
    private UUID id;
    private String name;
    private Integer orderIndex;
    private LocalDate deadline;

    private List<ProjectSubstepResponse> substeps;

    // later: ComputedStatus status

    // getters/setters
}