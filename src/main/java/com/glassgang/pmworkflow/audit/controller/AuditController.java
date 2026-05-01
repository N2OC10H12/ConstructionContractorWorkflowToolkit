package com.glassgang.pmworkflow.audit.controller;

import com.glassgang.pmworkflow.audit.dto.ProjectAuditLogResponse;
import com.glassgang.pmworkflow.common.dto.PagedResponse;
import com.glassgang.pmworkflow.audit.service.AuditService;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.repository.ProjectRepository;
import com.glassgang.pmworkflow.project.service.ProjectAccessService;
import com.glassgang.pmworkflow.common.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/audit")
public class AuditController {

    private final AuditService auditService;
    private final ProjectRepository projectRepository;
    private final ProjectAccessService projectAccessService;

    public AuditController(AuditService auditService,
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