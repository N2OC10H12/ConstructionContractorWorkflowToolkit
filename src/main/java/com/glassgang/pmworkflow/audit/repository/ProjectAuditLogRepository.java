package com.glassgang.pmworkflow.audit.repository;

import com.glassgang.pmworkflow.audit.entity.ProjectAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectAuditLogRepository extends JpaRepository<ProjectAuditLog, UUID> {

    Page<ProjectAuditLog> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);
}