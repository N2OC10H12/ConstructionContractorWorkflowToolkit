package com.glassgang.pmworkflow.audit.service;

import com.glassgang.pmworkflow.audit.dto.EstimateAuditLogResponse;
import com.glassgang.pmworkflow.audit.entity.EstimateAuditLog;
import com.glassgang.pmworkflow.audit.repository.EstimateAuditLogRepository;
import com.glassgang.pmworkflow.common.dto.PagedResponse;
import com.glassgang.pmworkflow.common.util.CurrentUserUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EstimateAuditService {

    private final EstimateAuditLogRepository auditLogRepository;
    private final CurrentUserUtil currentUserUtil;

    public EstimateAuditService(EstimateAuditLogRepository auditLogRepository,
                                CurrentUserUtil currentUserUtil) {
        this.auditLogRepository = auditLogRepository;
        this.currentUserUtil = currentUserUtil;
    }

    @Transactional
    public void log(UUID bidId,
                    UUID revisionId,
                    String action,
                    String targetType,
                    UUID targetId,
                    String oldValue,
                    String newValue,
                    String message) {

        EstimateAuditLog log = new EstimateAuditLog();

        log.setBidId(bidId);
        log.setRevisionId(revisionId);
        log.setActorUserId(currentUserUtil.getCurrentUserId());

        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);

        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setMessage(message);

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EstimateAuditLogResponse> getBidAudit(UUID bidId, Pageable pageable) {

        Page<Object[]> page = auditLogRepository.findAuditWithUserByBidId(bidId, pageable);

        return toPagedResponse(page);
    }

    @Transactional(readOnly = true)
    public PagedResponse<EstimateAuditLogResponse> getRevisionAudit(UUID revisionId, Pageable pageable) {

        Page<Object[]> page = auditLogRepository.findAuditWithUserByRevisionId(revisionId, pageable);

        return toPagedResponse(page);
    }

    private PagedResponse<EstimateAuditLogResponse> toPagedResponse(Page<Object[]> page) {

        List<EstimateAuditLogResponse> items = page.getContent().stream()
                .map(row -> {
                    EstimateAuditLog log = (EstimateAuditLog) row[0];
                    String username = (String) row[1];

                    EstimateAuditLogResponse response = new EstimateAuditLogResponse();

                    response.setAction(log.getAction());
                    response.setTargetType(log.getTargetType());
                    response.setTargetId(log.getTargetId());

                    response.setActorUserId(log.getActorUserId());
                    response.setActorUsername(username != null ? username : "Unknown");

                    response.setOldValue(log.getOldValue());
                    response.setNewValue(log.getNewValue());
                    response.setMessage(log.getMessage());

                    response.setCreatedAt(log.getCreatedAt());

                    return response;
                })
                .toList();

        PagedResponse<EstimateAuditLogResponse> response = new PagedResponse<>();
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