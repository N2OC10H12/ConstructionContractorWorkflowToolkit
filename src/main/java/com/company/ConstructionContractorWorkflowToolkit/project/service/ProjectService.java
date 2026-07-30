package com.company.ConstructionContractorWorkflowToolkit.project.service;

import com.company.ConstructionContractorWorkflowToolkit.common.exception.BadRequestException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.ForbiddenException;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import com.company.ConstructionContractorWorkflowToolkit.common.util.CurrentUserUtil;
import com.company.ConstructionContractorWorkflowToolkit.audit.service.ProjectAuditService;
import com.company.ConstructionContractorWorkflowToolkit.file.repository.SubstepFileRepository;
import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import com.company.ConstructionContractorWorkflowToolkit.file.service.StoredFileService;
import com.company.ConstructionContractorWorkflowToolkit.note.repository.SubstepNoteRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.CreateProjectRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectDetailsResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectOwnerResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectStepSummaryResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectSubstepResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.ProjectSummaryResponse;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.RenameProjectRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.UpdateProjectOwnerRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.dto.UpdateStepDeadlineRequest;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ComputedStatus;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.Project;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectStep;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectStepRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectSubstepRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.projection.ProjectSummaryFlatRow;
import com.company.ConstructionContractorWorkflowToolkit.user.entity.AppUser;
import com.company.ConstructionContractorWorkflowToolkit.user.entity.Role;
import com.company.ConstructionContractorWorkflowToolkit.user.repository.AppUserRepository;
import com.company.ConstructionContractorWorkflowToolkit.workflow.entity.WorkflowTemplate;
import com.company.ConstructionContractorWorkflowToolkit.workflow.entity.WorkflowTemplateStep;
import com.company.ConstructionContractorWorkflowToolkit.workflow.entity.WorkflowTemplateSubstep;
import com.company.ConstructionContractorWorkflowToolkit.workflow.repository.WorkflowTemplateRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.ArrayList;

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
    private final ProjectAuditService auditService;
    private final ProjectStatusService projectStatusService;
    private final SubstepNoteRepository substepNoteRepository;
    private final SubstepFileRepository substepFileRepository;
    private final CurrentUserUtil currentUserUtil;
    private final StoredFileService storedFileService;

    public ProjectService(ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            WorkflowTemplateRepository workflowTemplateRepository,
            AppUserRepository appUserRepository,
            ProjectStepRepository projectStepRepository,
            ProjectSubstepRepository projectSubstepRepository,
            ProjectAccessService projectAccessService,
            ProjectAuditService auditService,
            ProjectStatusService projectStatusService,
            SubstepNoteRepository substepNoteRepository,
            SubstepFileRepository substepFileRepository,
            StoredFileService storedFileService,
            CurrentUserUtil currentUserUtil) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.workflowTemplateRepository = workflowTemplateRepository;
        this.appUserRepository = appUserRepository;
        this.projectStepRepository = projectStepRepository;
        this.projectSubstepRepository = projectSubstepRepository;
        this.projectAccessService = projectAccessService;
        this.auditService = auditService;
        this.projectStatusService = projectStatusService;
        this.substepNoteRepository = substepNoteRepository;
        this.substepFileRepository = substepFileRepository;
        this.currentUserUtil = currentUserUtil;
        this.storedFileService = storedFileService;
    }

    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProject(UUID projectId) {

        entityManager.clear();

        Project project = projectRepository.findFreshById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        projectAccessService.requireProjectViewAccess(project);

        List<ProjectStep> steps = projectStepRepository.findByProject(project);
        project.setSteps(steps);

        steps.forEach(step -> step.getSubsteps().size());

        List<UUID> substepIds = steps.stream()
                .flatMap(step -> step.getSubsteps().stream())
                .map(ProjectSubstep::getId)
                .toList();

        Set<UUID> noteSubstepIds = new HashSet<>(
                substepNoteRepository.findSubstepIdsWithNotes(substepIds));

        Set<UUID> fileSubstepIds = new HashSet<>(
                substepFileRepository.findSubstepIdsWithFiles(substepIds));

        return projectMapper.toDetails(project, noteSubstepIds, fileSubstepIds);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getProjects() {

        Role role = currentUserUtil.getCurrentRole();
        UUID currentUserId = currentUserUtil.getCurrentUserId();

        List<ProjectSummaryFlatRow> rows;

        if (role.canViewAllProjects()) {
            rows = projectRepository.findAllProjectSummaryFlatRows();
        } else if (role.isPm()) {
            rows = projectRepository.findProjectSummaryFlatRowsByOwnerId(currentUserId);
        } else {
            throw new ForbiddenException("No access to projects");
        }

        return buildProjectSummariesFromFlatRows(rows);
    }

    @Transactional
    public ProjectDetailsResponse createProject(CreateProjectRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }

        Role role = currentUserUtil.getCurrentRole();
        AppUser currentUser = currentUserUtil.getCurrentUser();

        AppUser owner;

        if (role.isAdmin() || role.isPmManager()) {
            if (request.getOwnerUserId() != null) {
                owner = appUserRepository.findById(request.getOwnerUserId())
                        .orElseThrow(() -> new NotFoundException("Owner user not found"));

                validateProjectOwnerRole(owner);
            } else {
                owner = currentUser;
            }
        } else if (role.isPm()) {
            owner = currentUser;
        } else {
            throw new ForbiddenException("No access to create project");
        }

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

        auditService.log(
                savedProject.getId(),
                "PROJECT_CREATED",
                "PROJECT",
                savedProject.getId(),
                null,
                "name=" + savedProject.getName());

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
                        }))
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

        Boolean oldValue = substep.getIsDone();

        substep.setIsDone(true);

        projectSubstepRepository.save(substep);

        auditService.log(
                project.getId(),
                "SUBSTEP_COMPLETED",
                "SUBSTEP",
                substep.getId(),
                "isDone=" + oldValue,
                "isDone=" + substep.getIsDone());

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

        var oldDeadline = step.getDeadline();

        step.setDeadline(request.getDeadline());

        projectStepRepository.save(step);

        auditService.log(
                project.getId(),
                "STEP_DEADLINE_UPDATED",
                "STEP",
                step.getId(),
                "deadline=" + oldDeadline,
                "deadline=" + step.getDeadline());

        UUID projectId = project.getId();

        entityManager.flush();
        entityManager.clear();

        return getProject(projectId);
    }

    @Transactional
    public ProjectDetailsResponse renameProject(UUID projectId, RenameProjectRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        projectAccessService.requireProjectEditAccess(project);

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }

        String oldName = project.getName();
        String newName = request.getName().trim();

        if (oldName.equals(newName)) {
            return getProject(projectId);
        }

        project.setName(newName);
        projectRepository.save(project);

        auditService.log(
                project.getId(),
                "PROJECT_RENAMED",
                "PROJECT",
                project.getId(),
                "name=" + oldName,
                "name=" + newName);

        entityManager.flush();
        entityManager.clear();

        return getProject(projectId);
    }

    @Transactional
    public ProjectDetailsResponse updateProjectOwner(UUID projectId, UpdateProjectOwnerRequest request) {

        projectAccessService.requireProjectManagementAccess();

        if (request.getOwnerUserId() == null) {
            throw new BadRequestException("Owner user is required");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        AppUser newOwner = appUserRepository.findById(request.getOwnerUserId())
                .orElseThrow(() -> new NotFoundException("Owner user not found"));

        validateProjectOwnerRole(newOwner);

        AppUser oldOwner = project.getOwner();

        if (oldOwner != null && oldOwner.getId().equals(newOwner.getId())) {
            return getProject(projectId);
        }

        project.setOwner(newOwner);

        projectRepository.save(project);

        auditService.log(
                project.getId(),
                "PROJECT_OWNER_CHANGED",
                "PROJECT",
                project.getId(),
                oldOwner != null
                        ? "ownerUserId=" + oldOwner.getId() + ", username=" + oldOwner.getUsername()
                        : null,
                "ownerUserId=" + newOwner.getId() + ", username=" + newOwner.getUsername());

        entityManager.flush();
        entityManager.clear();

        return getProject(projectId);
    }

    @Transactional
    public void deleteProject(UUID projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        projectAccessService.requireProjectEditAccess(project);

        /*
         * Capture the reusable storage records before deleting the Project.
         * The database cascade will remove Project Substep attachment rows,
         * but StoredFile rows and physical provider objects are intentionally
         * not cascade-deleted.
         */
        List<StoredFile> projectStoredFiles = substepFileRepository.findStoredFilesByProjectId(projectId);

        projectRepository.delete(project);

        /*
         * Force the Project → Step → Substep → SubstepFile database cascade
         * before checking whether each StoredFile still has references.
         */
        entityManager.flush();

        for (StoredFile storedFile : projectStoredFiles) {
            long remainingReferences = substepFileRepository
                    .countByStoredFile_StoredFileId(
                            storedFile.getStoredFileId());

            if (remainingReferences == 0) {
                storedFileService.deleteUnreferenced(storedFile);
            }
        }

        entityManager.clear();
    }

    private List<ProjectSummaryResponse> buildProjectSummariesFromFlatRows(List<ProjectSummaryFlatRow> rows) {

        Map<UUID, ProjectSummaryResponse> projectMap = new LinkedHashMap<>();
        Map<UUID, Map<UUID, ProjectStepSummaryResponse>> projectStepsMap = new LinkedHashMap<>();

        for (ProjectSummaryFlatRow row : rows) {

            ProjectSummaryResponse projectDto = projectMap.computeIfAbsent(row.getProjectId(), projectId -> {
                ProjectSummaryResponse dto = new ProjectSummaryResponse();
                dto.setId(row.getProjectId());
                dto.setName(row.getProjectName());
                dto.setProjectDeadline(row.getProjectDeadline());
                dto.setSteps(new ArrayList<>());

                ProjectOwnerResponse ownerDto = new ProjectOwnerResponse();
                ownerDto.setId(row.getOwnerId());
                ownerDto.setUsername(row.getOwnerUsername());
                ownerDto.setRole(row.getOwnerRole());
                dto.setOwner(ownerDto);

                return dto;
            });

            if (row.getStepId() == null) {
                continue;
            }

            Map<UUID, ProjectStepSummaryResponse> stepsById = projectStepsMap.computeIfAbsent(row.getProjectId(),
                    projectId -> new LinkedHashMap<>());

            ProjectStepSummaryResponse stepDto = stepsById.computeIfAbsent(row.getStepId(), stepId -> {
                ProjectStepSummaryResponse dto = new ProjectStepSummaryResponse();
                dto.setId(row.getStepId());
                dto.setName(row.getStepName());
                dto.setOrderIndex(row.getStepOrderIndex());
                dto.setDeadline(row.getStepDeadline());
                dto.setSubsteps(new ArrayList<>());
                projectDto.getSteps().add(dto);
                return dto;
            });

            if (row.getSubstepId() != null) {
                ProjectSubstepResponse substepDto = new ProjectSubstepResponse();
                substepDto.setId(row.getSubstepId());
                substepDto.setName(row.getSubstepName());
                substepDto.setOrderIndex(row.getSubstepOrderIndex());
                substepDto.setIsDone(row.getSubstepIsDone());
                substepDto.setStatus(projectStatusService.computeSubstepStatusFromDone(row.getSubstepIsDone()));

                stepDto.getSubsteps().add(substepDto);
            }
        }

        for (ProjectSummaryResponse projectDto : projectMap.values()) {

            List<ComputedStatus> stepStatuses = new ArrayList<>();

            for (ProjectStepSummaryResponse stepDto : projectDto.getSteps()) {
                long totalSubsteps = stepDto.getSubsteps().size();
                long doneSubsteps = stepDto.getSubsteps().stream()
                        .filter(substep -> Boolean.TRUE.equals(substep.getIsDone()))
                        .count();

                ComputedStatus stepStatus = projectStatusService.computeStepStatusFromCounts(
                        stepDto.getDeadline(),
                        totalSubsteps,
                        doneSubsteps);

                stepDto.setStatus(stepStatus);
                stepStatuses.add(stepStatus);
            }

            projectDto.setStatus(
                    projectStatusService.computeProjectStatusFromStepStatuses(
                            projectDto.getProjectDeadline(),
                            stepStatuses));
        }

        return new ArrayList<>(projectMap.values());
    }

    private void validateProjectOwnerRole(AppUser owner) {
        Role ownerRole = Role.from(owner.getRole());

        if (ownerRole.isPm() || ownerRole.isPmManager()) {
            return;
        }

        throw new BadRequestException("Project owner must have PM or PM_MANAGER role");
    }
}