package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.project.dto.*;
import com.glassgang.pmworkflow.project.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    private final ProjectStatusService statusService;

    public ProjectMapper(ProjectStatusService statusService) {
        this.statusService = statusService;
    }

    public ProjectDetailsResponse toDetails(Project project) {

        ProjectDetailsResponse dto = new ProjectDetailsResponse();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setStatus(statusService.computeProjectStatus(project));

        List<ProjectStepResponse> steps = project.getSteps().stream()
                .map(this::toStep)
                .collect(Collectors.toList());

        dto.setSteps(steps);

        return dto;
    }

    private ProjectStepResponse toStep(ProjectStep step) {

        ProjectStepResponse dto = new ProjectStepResponse();
        dto.setId(step.getId());
        dto.setName(step.getName());
        dto.setOrderIndex(step.getOrderIndex());

        List<ProjectSubstepResponse> substeps = step.getSubsteps().stream()
                .map(this::toSubstep)
                .collect(Collectors.toList());

        dto.setSubsteps(substeps);
        dto.setStatus(statusService.computeStepStatus(step));

        return dto;
    }

    private ProjectSubstepResponse toSubstep(ProjectSubstep substep) {

        ProjectSubstepResponse dto = new ProjectSubstepResponse();
        dto.setId(substep.getId());
        dto.setName(substep.getName());
        dto.setOrderIndex(substep.getOrderIndex());
        dto.setIsDone(substep.getIsDone());
        dto.setStatus(statusService.computeSubstepStatus(substep));

        return dto;
    }
}