package com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ExternalEntityType;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ExternalSystem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "businesspartner", name = "business_partner_external_ref")
public class BusinessPartnerExternalRef {

    @Id
    @Column(name = "business_partner_external_ref_id")
    private UUID businessPartnerExternalRefId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_partner_id", nullable = false)
    private BusinessPartner businessPartner;

    @Enumerated(EnumType.STRING)
    @Column(name = "external_system", nullable = false, length = 50)
    private ExternalSystem externalSystem;

    @Enumerated(EnumType.STRING)
    @Column(name = "external_entity_type", nullable = false, length = 50)
    private ExternalEntityType externalEntityType;

    @Column(name = "realm_id", length = 100)
    private String realmId;

    @Column(name = "external_id", nullable = false, length = 100)
    private String externalId;

    @Column(name = "sync_token", length = 100)
    private String syncToken;

    @Column(name = "display_name_snapshot", length = 255)
    private String displayNameSnapshot;

    @Column(name = "fully_qualified_name_snapshot", length = 500)
    private String fullyQualifiedNameSnapshot;

    @Column(name = "active_snapshot")
    private Boolean activeSnapshot;

    @Column(name = "last_synced_at_utc")
    private LocalDateTime lastSyncedAtUtc;

    @Column(name = "last_error")
    private String lastError;

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