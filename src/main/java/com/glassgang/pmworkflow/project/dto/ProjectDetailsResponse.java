package com.glassgang.pmworkflow.project.dto;

import com.glassgang.pmworkflow.project.entity.ComputedStatus;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDetailsResponse {

    private ComputedStatus status;
    private UUID id;
    private String name;

    private List<ProjectStepResponse> steps;

    // later: ComputedStatus status

    // getters/setters
}