package com.company.ConstructionContractorWorkflowToolkit.estimate.entity;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartner;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidStatus;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.ConstructionType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.DepartmentCode;
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

    @Column(name = "job_address_line1", length = 255)
    private String jobAddressLine1;

    @Column(name = "job_address_line2", length = 255)
    private String jobAddressLine2;

    @Column(name = "job_city", length = 100)
    private String jobCity;

    @Column(name = "job_state", length = 100)
    private String jobState;

    @Column(name = "job_postal_code", length = 30)
    private String jobPostalCode;

    @Column(name = "job_country", length = 100)
    private String jobCountry;

    @Column(name = "description")
    private String description;

    @Column(name = "estimate_scope", length = 1000)
    private String estimateScope;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_tax_rate_id")
    private TaxRate defaultTaxRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_object_type_id")
    private ConstructionObjectType constructionObjectType;

}