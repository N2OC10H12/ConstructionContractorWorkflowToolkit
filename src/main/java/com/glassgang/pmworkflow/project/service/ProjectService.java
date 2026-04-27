package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.project.dto.ProjectDetailsResponse;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.repository.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        Project project = projectRepository.findWithStepsById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.getSteps().forEach(step -> step.getSubsteps().size());

        return projectMapper.toDetails(project);
    }
}