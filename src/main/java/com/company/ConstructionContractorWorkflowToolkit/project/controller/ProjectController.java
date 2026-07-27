package com.company.ConstructionContractorWorkflowToolkit.project.controller;

import com.company.ConstructionContractorWorkflowToolkit.project.dto.RenameProjectRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.UpdateProjectOwnerRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectDetailsResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.CreateProjectRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.UpdateStepDeadlineRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.service.ProjectService;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectSummaryResponse;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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
            @RequestBody UpdateStepDeadlineRequest request) {
        return projectService.updateStepDeadline(id, request);
    }

    @PatchMapping("/{id}/rename")
    public ProjectDetailsResponse renameProject(
            @PathVariable UUID id,
            @RequestBody RenameProjectRequest request) {
        return projectService.renameProject(id, request);
    }

    @PatchMapping("/{id}/owner")
    public ProjectDetailsResponse updateProjectOwner(
            @PathVariable UUID id,
            @RequestBody UpdateProjectOwnerRequest request) {
        return projectService.updateProjectOwner(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}