package com.glassgang.pmworkflow.audit.service;

import com.glassgang.pmworkflow.audit.dto.ProjectAuditLogResponse;
import com.glassgang.pmworkflow.audit.entity.ProjectAuditLog;
import com.glassgang.pmworkflow.audit.repository.ProjectAuditLogRepository;
import com.glassgang.pmworkflow.common.dto.PagedResponse;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

    private final ProjectAuditLogRepository auditLogRepository;
    private final CurrentUserUtil currentUserUtil;

    public AuditService(ProjectAuditLogRepository auditLogRepository,
                        CurrentUserUtil currentUserUtil) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserUtil = currentUserUtil;
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

        Page<Object[]> page = auditLogRepository.findAuditWithUser(projectId, pageable);

        List<ProjectAuditLogResponse> items = page.getContent().stream()
                .map(row -> {
                    ProjectAuditLog log = (ProjectAuditLog) row[0];
                    String username = (String) row[1];

                    ProjectAuditLogResponse r = new ProjectAuditLogResponse();
                    r.setAction(log.getAction());
                    r.setTargetType(log.getTargetType());
                    r.setTargetId(log.getTargetId());
                    r.setActorUserId(log.getActorUserId());
                    r.setActorUsername(username != null ? username : "Unknown");
                    r.setOldValue(log.getOldValue());
                    r.setNewValue(log.getNewValue());
                    r.setCreatedAt(log.getCreatedAt());
                    return r;
                })
                .toList();

        PagedResponse<ProjectAuditLogResponse> response = new PagedResponse<>();
        response.setItems(items);
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }
}