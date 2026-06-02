package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.project.dto.*;
import com.glassgang.pmworkflow.project.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    private final ProjectStatusService statusService;

    public ProjectMapper(ProjectStatusService statusService) {
        this.statusService = statusService;
    }

    public ProjectDetailsResponse toDetails(
            Project project,
            Set<UUID> noteSubstepIds,
            Set<UUID> fileSubstepIds) {
        ProjectDetailsResponse dto = new ProjectDetailsResponse();

        dto.setId(project.getId());
        dto.setName(project.getName());

        ProjectOwnerResponse ownerDto = new ProjectOwnerResponse();
        ownerDto.setId(project.getOwner().getId());
        ownerDto.setUsername(project.getOwner().getUsername());
        ownerDto.setRole(project.getOwner().getRole());
        dto.setOwner(ownerDto);
        
        dto.setStatus(statusService.computeProjectStatus(project));
        dto.setPlanningComplete(statusService.isPlanningComplete(project));
        dto.setProjectDeadline(project.getProjectDeadline());
        dto.setPlanningDeadline(project.getPlanningDeadline());
        dto.setCreatedAt(project.getCreatedAt());

        List<ProjectStepResponse> steps = project.getSteps().stream()
                .sorted((a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()))
                .map(step -> toStep(step, noteSubstepIds, fileSubstepIds))
                .collect(Collectors.toList());

        dto.setSteps(steps);

        return dto;
    }

    private ProjectStepResponse toStep(
            ProjectStep step,
            Set<UUID> noteSubstepIds,
            Set<UUID> fileSubstepIds) {
        ProjectStepResponse dto = new ProjectStepResponse();

        dto.setId(step.getId());
        dto.setName(step.getName());
        dto.setOrderIndex(step.getOrderIndex());
        dto.setDeadline(step.getDeadline());

        List<ProjectSubstepResponse> substeps = step.getSubsteps().stream()
                .sorted((a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()))
                .map(substep -> toSubstep(substep, noteSubstepIds, fileSubstepIds))
                .collect(Collectors.toList());

        dto.setSubsteps(substeps);
        dto.setStatus(statusService.computeStepStatus(step));

        return dto;
    }

    private ProjectSubstepResponse toSubstep(
            ProjectSubstep substep,
            Set<UUID> noteSubstepIds,
            Set<UUID> fileSubstepIds) {
        ProjectSubstepResponse dto = new ProjectSubstepResponse();

        dto.setId(substep.getId());
        dto.setName(substep.getName());
        dto.setOrderIndex(substep.getOrderIndex());
        dto.setIsDone(substep.getIsDone());
        dto.setStatus(statusService.computeSubstepStatus(substep));

        dto.setHasNotes(noteSubstepIds.contains(substep.getId()));
        dto.setHasFiles(fileSubstepIds.contains(substep.getId()));

        return dto;
    }

    public ProjectSummaryResponse toSummary(Project project) {
        ProjectSummaryResponse dto = new ProjectSummaryResponse();

        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setProjectDeadline(project.getProjectDeadline());
        dto.setStatus(statusService.computeProjectStatus(project));

        List<ProjectStepSummaryResponse> steps = project.getSteps().stream()
                .sorted((a, b) -> Integer.compare(a.getOrderIndex(), b.getOrderIndex()))
                .map(step -> {
                    ProjectStepSummaryResponse stepDto = new ProjectStepSummaryResponse();

                    stepDto.setId(step.getId());
                    stepDto.setName(step.getName());
                    stepDto.setOrderIndex(step.getOrderIndex());
                    stepDto.setDeadline(step.getDeadline());
                    stepDto.setStatus(statusService.computeStepStatus(step));

                    return stepDto;
                })
                .toList();

        dto.setSteps(steps);

        return dto;
    }
}