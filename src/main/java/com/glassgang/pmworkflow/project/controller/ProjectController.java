package com.glassgang.pmworkflow.project.controller;

import com.glassgang.pmworkflow.project.dto.ProjectDetailsResponse;
import com.glassgang.pmworkflow.project.dto.CreateProjectRequest;
import com.glassgang.pmworkflow.project.dto.UpdateStepDeadlineRequest;
import com.glassgang.pmworkflow.project.service.ProjectService;
import com.glassgang.pmworkflow.project.dto.ProjectSummaryResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public ProjectDetailsResponse getProject(@PathVariable UUID id) {
        return projectService.getProject(id);
    }

    @GetMapping
    public List<ProjectSummaryResponse> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public ProjectDetailsResponse createProject(@RequestBody CreateProjectRequest request) {
        return projectService.createProject(request);
    }

    @PatchMapping("/substeps/{id}/complete")
    public ProjectDetailsResponse completeSubstep(@PathVariable UUID id) {
        return projectService.completeSubstep(id);
    }

    @PatchMapping("/steps/{id}/deadline")
    public ProjectDetailsResponse updateStepDeadline(
            @PathVariable UUID id,
            @RequestBody UpdateStepDeadlineRequest request
    ) {
        return projectService.updateStepDeadline(id, request);
    }

}