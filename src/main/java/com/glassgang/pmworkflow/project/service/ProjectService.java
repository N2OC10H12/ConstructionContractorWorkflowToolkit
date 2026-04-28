package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.common.exception.BadRequestException;
import com.glassgang.pmworkflow.project.dto.CreateProjectRequest;
import com.glassgang.pmworkflow.project.dto.UpdateStepDeadlineRequest;
import com.glassgang.pmworkflow.project.repository.ProjectStepRepository;
import com.glassgang.pmworkflow.project.repository.ProjectSubstepRepository;
import com.glassgang.pmworkflow.user.entity.AppUser;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
import com.glassgang.pmworkflow.workflow.entity.WorkflowTemplate;
import com.glassgang.pmworkflow.workflow.repository.WorkflowTemplateRepository;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import com.glassgang.pmworkflow.project.dto.ProjectDetailsResponse;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.repository.ProjectRepository;
import com.glassgang.pmworkflow.project.dto.ProjectSummaryResponse;
import com.glassgang.pmworkflow.project.entity.ProjectStep;
import com.glassgang.pmworkflow.workflow.entity.WorkflowTemplateStep;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import com.glassgang.pmworkflow.workflow.entity.WorkflowTemplateSubstep;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ProjectRepository projectRepository;
    private final ProjectStepRepository projectStepRepository;
    private final ProjectSubstepRepository projectSubstepRepository;
    private final ProjectMapper projectMapper;
    private final WorkflowTemplateRepository workflowTemplateRepository;
    private final AppUserRepository appUserRepository;
    private final ProjectAccessService projectAccessService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMapper projectMapper,
                          WorkflowTemplateRepository workflowTemplateRepository,
                          AppUserRepository appUserRepository,
                          ProjectStepRepository projectStepRepository,
                          ProjectSubstepRepository projectSubstepRepository,
                          ProjectAccessService projectAccessService) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.workflowTemplateRepository = workflowTemplateRepository;
        this.appUserRepository = appUserRepository;
        this.projectStepRepository = projectStepRepository;
        this.projectSubstepRepository = projectSubstepRepository;
        this.projectAccessService = projectAccessService;
    }

    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProject(UUID projectId) {
        Project project = projectRepository.findWithStepsById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        projectAccessService.requireProjectViewAccess(project);
        project.getSteps().forEach(step -> step.getSubsteps().size());

        return projectMapper.toDetails(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getProjects() {

        AppUser user = (AppUser) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        List<Project> projects;

        if ("ADMIN".equalsIgnoreCase(user.getRole()) ||
                "SUPERVISOR".equalsIgnoreCase(user.getRole())) {

            projects = projectRepository.findAll();

        } else if ("PM".equalsIgnoreCase(user.getRole())) {

            projects = projectRepository.findByOwner_Id(user.getId());

        } else {
            throw new com.glassgang.pmworkflow.common.exception.ForbiddenException("Unknown role");
        }

        return projects.stream()
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

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName(request.getName().trim());
        project.setOwner(owner);
        project.setPlanningDeadline(request.getPlanningDeadline());
        project.setProjectDeadline(request.getProjectDeadline());
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        List<ProjectStep> projectSteps = template.getSteps().stream()
                .sorted(Comparator.comparing(WorkflowTemplateStep::getOrderIndex))
                .map(templateStep -> {
                    ProjectStep step = new ProjectStep();
                    step.setId(UUID.randomUUID());
                    step.setProject(savedProject);
                    step.setTemplateStepId(templateStep.getId());
                    step.setName(templateStep.getName());
                    step.setOrderIndex(templateStep.getOrderIndex());
                    step.setCreatedAt(LocalDateTime.now());
                    return step;
                })
                .toList();

        projectStepRepository.saveAll(projectSteps);

        Map<UUID, ProjectStep> stepMap = projectSteps.stream()
                .collect(Collectors.toMap(ProjectStep::getTemplateStepId, s -> s));

        List<ProjectSubstep> projectSubsteps = template.getSteps().stream()
                .flatMap(templateStep -> templateStep.getSubsteps().stream()
                        .sorted(Comparator.comparing(WorkflowTemplateSubstep::getOrderIndex))
                        .map(templateSubstep -> {
                            ProjectSubstep substep = new ProjectSubstep();
                            substep.setId(UUID.randomUUID());
                            substep.setStep(stepMap.get(templateStep.getId()));
                            substep.setTemplateSubstepId(templateSubstep.getId());
                            substep.setName(templateSubstep.getName());
                            substep.setOrderIndex(templateSubstep.getOrderIndex());
                            substep.setIsDone(false);
                            substep.setCreatedAt(LocalDateTime.now());
                            return substep;
                        })
                )
                .toList();

        projectSubstepRepository.saveAll(projectSubsteps);

        entityManager.flush();
        entityManager.clear();

        return getProject(savedProject.getId());
    }

    @Transactional
    public ProjectDetailsResponse completeSubstep(UUID substepId) {
        ProjectSubstep substep = projectSubstepRepository.findById(substepId)
                .orElseThrow(() -> new NotFoundException("Substep not found"));

        Project project = substep.getStep().getProject();

        projectAccessService.requireProjectEditAccess(project);

        substep.setIsDone(true);

        projectSubstepRepository.save(substep);

        UUID projectId = project.getId();

        entityManager.flush();
        entityManager.clear();

        return getProject(projectId);
    }

    @Transactional
    public ProjectDetailsResponse updateStepDeadline(UUID stepId, UpdateStepDeadlineRequest request) {
        ProjectStep step = projectStepRepository.findById(stepId)
                .orElseThrow(() -> new NotFoundException("Project step not found"));

        Project project = step.getProject();

        projectAccessService.requireProjectEditAccess(project);

        if (request.getDeadline() != null &&
                project.getProjectDeadline() != null &&
                request.getDeadline().isAfter(project.getProjectDeadline())) {
            throw new BadRequestException("Step deadline cannot be after project deadline");
        }

        List<ProjectStep> steps = projectStepRepository.findByProject(project).stream()
                .sorted(Comparator.comparing(ProjectStep::getOrderIndex))
                .toList();

        ProjectStep previousStep = steps.stream()
                .filter(s -> s.getOrderIndex() < step.getOrderIndex())
                .max(Comparator.comparing(ProjectStep::getOrderIndex))
                .orElse(null);

        ProjectStep nextStep = steps.stream()
                .filter(s -> s.getOrderIndex() > step.getOrderIndex())
                .min(Comparator.comparing(ProjectStep::getOrderIndex))
                .orElse(null);

        if (request.getDeadline() != null &&
                previousStep != null &&
                previousStep.getDeadline() != null &&
                request.getDeadline().isBefore(previousStep.getDeadline())) {
            throw new BadRequestException("Step deadline cannot be before previous step deadline");
        }

        if (request.getDeadline() != null &&
                nextStep != null &&
                nextStep.getDeadline() != null &&
                request.getDeadline().isAfter(nextStep.getDeadline())) {
            throw new BadRequestException("Step deadline cannot be after next step deadline");
        }

        step.setDeadline(request.getDeadline());

        projectStepRepository.save(step);

        UUID projectId = project.getId();

        entityManager.flush();
        entityManager.clear();

        return getProject(projectId);
    }
}