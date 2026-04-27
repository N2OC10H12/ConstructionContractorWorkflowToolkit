package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.project.dto.CreateProjectRequest;
import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
import com.glassgang.pmworkflow.workflow.entity.WorkflowTemplate;
import com.glassgang.pmworkflow.workflow.repository.WorkflowTemplateRepository;
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
    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final AppUserRepository appUserRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMapper projectMapper,
                          WorkflowTemplateRepository workflowTemplateRepository,
                          AppUserRepository appUserRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.workflowTemplateRepository = workflowTemplateRepository;
        this.appUserRepository = appUserRepository;
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

    @Transactional
    public ProjectDetailsResponse createProject(CreateProjectRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }

        if (request.getOwnerUserId() == null) {
            throw new BadRequestException("Owner user id is required");
        }

        AppUser owner = appUserRepository.findById(request.getOwnerUserId())
                .orElseThrow(() -> new NotFoundException("Owner user not found"));

        WorkflowTemplate template = workflowTemplateRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new NotFoundException("Default workflow template not found"));

        throw new RuntimeException("Not implemented yet");
    }
}