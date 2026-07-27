package com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ContactRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "businesspartner", name = "business_partner_contact")
public class BusinessPartnerContact {

    @Id
    @Column(name = "business_partner_contact_id")
    private UUID businessPartnerContactId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @Column(name = "contact_name", nullable = false, length = 255)
    private String contactName;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_role", nullable = false, length = 50)
    private ContactRole contactRole;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

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