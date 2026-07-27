package com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.CustomerCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "businesspartner", name = "customer_profile")
public class CustomerProfile {

    @Id
    @Column(name = "customer_profile_id")
    private UUID customerProfileId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_category", nullable = false, length = 50)
    private CustomerCategory customerCategory;

    @Column(name = "default_taxable", nullable = false)
    private Boolean defaultTaxable = true;

    @Column(name = "resale_number", length = 100)
    private String resaleNumber;

    @Column(name = "internal_note")
    private String internalNote;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc", nullable = false)
    private LocalDateTime updatedAtUtc;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;
}