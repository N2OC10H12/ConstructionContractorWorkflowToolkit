package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.project.dto.ProjectDetailsResponse;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.repository.ProjectRepository;
import com.glassgang.pmworkflow.project.dto.ProjectSummaryResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        project.getSteps().forEach(step -> step.getSubsteps().size());

        return projectMapper.toDetails(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getProjects() {
        return projectRepository.findAll().stream()
                .peek(project -> project.getSteps().forEach(step -> step.getSubsteps().size()))
                .map(projectMapper::toSummary)
                .toList();
    }
}