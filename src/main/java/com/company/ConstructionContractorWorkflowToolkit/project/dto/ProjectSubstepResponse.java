package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ComputedStatus;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectSubstepResponse {

    private ComputedStatus status;
    private UUID id;
    private String name;
    private Integer orderIndex;
    private Boolean isDone;
    private boolean hasNotes;
    private boolean hasFiles;
}