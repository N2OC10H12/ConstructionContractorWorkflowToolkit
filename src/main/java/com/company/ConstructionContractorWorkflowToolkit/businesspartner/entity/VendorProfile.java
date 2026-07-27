package com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.VendorCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "businesspartner", name = "vendor_profile")
public class VendorProfile {

    @Id
    @Column(name = "vendor_profile_id")
    private UUID vendorProfileId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @Enumerated(EnumType.STRING)
    @Column(name = "vendor_category", nullable = false, length = 50)
    private VendorCategory vendorCategory;

    @Column(name = "vendor1099", nullable = false)
    private Boolean vendor1099 = false;

    @Column(name = "tax_identifier_last4", length = 4)
    private String taxIdentifierLast4;

    @Column(name = "account_number", length = 100)
    private String accountNumber;

    @Column(name = "default_payment_terms", length = 100)
    private String defaultPaymentTerms;

    @Column(name = "insurance_expiration_date")
    private LocalDate insuranceExpirationDate;

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