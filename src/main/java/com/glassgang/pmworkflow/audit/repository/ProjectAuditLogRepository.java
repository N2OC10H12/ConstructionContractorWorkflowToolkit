package com.glassgang.pmworkflow.audit.repository;

import com.glassgang.pmworkflow.audit.entity.ProjectAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProjectAuditLogRepository extends JpaRepository<ProjectAuditLog, UUID> {

    Page<ProjectAuditLog> findByProjectIdOrderByCreatedAtDesc(UUID projectId, Pageable pageable);

    @Query(
            value = """
                    SELECT l, u.username
                    FROM ProjectAuditLog l
                    LEFT JOIN AppUser u ON u.id = l.actorUserId
                    WHERE l.projectId = :projectId
                    ORDER BY l.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(l)
                    FROM ProjectAuditLog l
                    WHERE l.projectId = :projectId
                    """
    )
    Page<Object[]> findAuditWithUser(UUID projectId, Pageable pageable);
}