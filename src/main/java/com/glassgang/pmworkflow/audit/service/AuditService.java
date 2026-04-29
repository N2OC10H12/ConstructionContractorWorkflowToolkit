package com.glassgang.pmworkflow.audit.service;

import com.glassgang.pmworkflow.audit.dto.ProjectAuditLogResponse;
import com.glassgang.pmworkflow.audit.entity.ProjectAuditLog;
import com.glassgang.pmworkflow.audit.repository.ProjectAuditLogRepository;
import com.glassgang.pmworkflow.common.dto.PagedResponse;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import com.glassgang.pmworkflow.user.repository.AppUserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditService {

    private final ProjectAuditLogRepository auditLogRepository;
    private final CurrentUserUtil currentUserUtil;
    private final AppUserRepository appUserRepository;

    public AuditService(ProjectAuditLogRepository auditLogRepository,
                        CurrentUserUtil currentUserUtil,
                        AppUserRepository appUserRepository) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserUtil = currentUserUtil;
        this.appUserRepository = appUserRepository;
    }

    public void log(UUID projectId,
                    String action,
                    String targetType,
                    UUID targetId,
                    String oldValue,
                    String newValue) {

        ProjectAuditLog log = new ProjectAuditLog();
        log.setId(UUID.randomUUID());
        log.setProjectId(projectId);
        log.setActorUserId(currentUserUtil.getCurrentUserId());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }

    public PagedResponse<ProjectAuditLogResponse> getProjectAudit(UUID projectId, Pageable pageable) {

        Page<ProjectAuditLogResponse> page = auditLogRepository
                .findByProjectIdOrderByCreatedAtDesc(projectId, pageable)
                .map(log -> {
                    ProjectAuditLogResponse r = new ProjectAuditLogResponse();
                    r.setAction(log.getAction());
                    r.setTargetType(log.getTargetType());
                    r.setTargetId(log.getTargetId());
                    r.setActorUserId(log.getActorUserId());
                    r.setActorUsername(
                            appUserRepository.findById(log.getActorUserId())
                                    .map(user -> user.getUsername())
                                    .orElse("Unknown")
                    );
                    r.setOldValue(log.getOldValue());
                    r.setNewValue(log.getNewValue());
                    r.setCreatedAt(log.getCreatedAt());
                    return r;
                });

        PagedResponse<ProjectAuditLogResponse> response = new PagedResponse<>();
        response.setItems(page.getContent());
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }
}