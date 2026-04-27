package com.glassgang.pmworkflow.project.controller;

import com.glassgang.pmworkflow.project.dto.ProjectDetailsResponse;
import com.glassgang.pmworkflow.project.entity.Project;
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

}