package com.company.ConstructionContractorWorkflowToolkit.estimate.entity;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CompanyWorkTypeSourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "company_work_type")
public class CompanyWorkType {

    @Id
    @Column(name = "company_work_type_id")
    private UUID companyWorkTypeId;

    @Column(name = "code", nullable = false, length = 30)
    private String code;

    @Column(name = "normalized_code", nullable = false, length = 30)
    private String normalizedCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "division_code", nullable = false, length = 2)
    private String divisionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_work_type_id")
    private CompanyWorkType parentWorkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 40)
    private CompanyWorkTypeSourceType sourceType;

    @Column(name = "source_edition", length = 20)
    private String sourceEdition;

    @Column(name = "original_name", length = 200)
    private String originalName;

    @Column(name = "search_aliases", columnDefinition = "text")
    private String searchAliases;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc")
    private LocalDateTime updatedAtUtc;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;
}