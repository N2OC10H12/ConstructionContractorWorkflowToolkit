package com.glassgang.pmworkflow.estimate.entity;

import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartner;
import com.glassgang.pmworkflow.estimate.enums.BidStatus;
import com.glassgang.pmworkflow.estimate.enums.ConstructionType;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "bid")
public class Bid {

    @Id
    @Column(name = "bid_id")
    private UUID bidId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_business_partner_id", nullable = false)
    private BusinessPartner customer;

    @Column(name = "bid_number", nullable = false, length = 50)
    private String bidNumber;

    @Column(name = "job_number", nullable = false, length = 50)
    private String jobNumber;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "department_code", nullable = false, length = 10)
    private DepartmentCode departmentCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status", nullable = false, length = 50)
    private BidStatus bidStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_revision_id")
    private BidRevision currentRevision;

    @Column(name = "converted_project_id")
    private UUID convertedProjectId;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc", nullable = false)
    private LocalDateTime updatedAtUtc;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "construction_type", nullable = false, length = 50)
    private ConstructionType constructionType;
}