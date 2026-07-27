package com.company.ConstructionContractorWorkflowToolkit.audit.repository;

import com.company.ConstructionContractorWorkflowToolkit.audit.entity.EstimateAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface EstimateAuditLogRepository extends JpaRepository<EstimateAuditLog, UUID> {

    Page<EstimateAuditLog> findByBidIdOrderByCreatedAtDesc(UUID bidId, Pageable pageable);

    Page<EstimateAuditLog> findByRevisionIdOrderByCreatedAtDesc(UUID revisionId, Pageable pageable);

    @Query(
            value = """
                    SELECT l, u.username
                    FROM EstimateAuditLog l
                    LEFT JOIN AppUser u ON u.id = l.actorUserId
                    WHERE l.bidId = :bidId
                    ORDER BY l.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(l)
                    FROM EstimateAuditLog l
                    WHERE l.bidId = :bidId
                    """
    )
    Page<Object[]> findAuditWithUserByBidId(UUID bidId, Pageable pageable);

    @Query(
            value = """
                    SELECT l, u.username
                    FROM EstimateAuditLog l
                    LEFT JOIN AppUser u ON u.id = l.actorUserId
                    WHERE l.revisionId = :revisionId
                    ORDER BY l.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(l)
                    FROM EstimateAuditLog l
                    WHERE l.revisionId = :revisionId
                    """
    )
    Page<Object[]> findAuditWithUserByRevisionId(UUID revisionId, Pageable pageable);
}