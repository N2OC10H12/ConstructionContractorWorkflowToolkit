package com.glassgang.pmworkflow.project.dto;

import com.glassgang.pmworkflow.project.entity.ComputedStatus;

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

    // later: ComputedStatus status

    // getters/setters
}