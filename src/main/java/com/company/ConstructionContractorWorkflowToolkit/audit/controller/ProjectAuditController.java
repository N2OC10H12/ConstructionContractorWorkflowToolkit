package com.company.ConstructionContractorWorkflowToolkit.audit.controller;

import com.company.ConstructionContractorWorkflowToolkit.audit.dto.ProjectAuditLogResponse;
import com.company.ConstructionContractorWorkflowToolkit.common.dto.PagedResponse;
import com.company.ConstructionContractorWorkflowToolkit.audit.service.ProjectAuditService;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.Project;
import com.company.ConstructionContractorWorkflowToolkit.project.repository.ProjectRepository;
import com.company.ConstructionContractorWorkflowToolkit.project.service.ProjectAccessService;
import com.company.ConstructionContractorWorkflowToolkit.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/audit")
public class ProjectAuditController {

    private final ProjectAuditService auditService;
    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    public ProjectAuditController(ProjectAuditService auditService,
                           ProjectRepository projectRepository,
                           ProjectAccessService projectAccessService) {
        this.auditService = auditService;
        this.projectRepository = projectRepository;
        this.projectAccessService = projectAccessService;
    }

    @GetMapping
    public PagedResponse<ProjectAuditLogResponse> getAudit(
            @PathVariable UUID projectId,
            Pageable pageable
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        projectAccessService.requireProjectViewAccess(project);

        return auditService.getProjectAudit(projectId, pageable);
    }
}