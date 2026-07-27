package com.company.ConstructionContractorWorkflowToolkit.company.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "company_profile")
public class CompanyProfile {

    @Id
    @Column(name = "company_profile_id")
    private UUID companyProfileId;

    @Column(name = "profile_code", nullable = false, length = 50)
    private String profileCode;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "legal_name")
    private String legalName;

    @Column(name = "employer_id", length = 50)
    private String employerId;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "company_address_line_1")
    private String companyAddressLine1;

    @Column(name = "company_address_line_2")
    private String companyAddressLine2;

    @Column(name = "company_city", length = 100)
    private String companyCity;

    @Column(name = "company_state", length = 100)
    private String companyState;

    @Column(name = "company_postal_code", length = 50)
    private String companyPostalCode;

    @Column(name = "company_country", length = 100)
    private String companyCountry;

    @Column(name = "legal_address_line_1")
    private String legalAddressLine1;

    @Column(name = "legal_address_line_2")
    private String legalAddressLine2;

    @Column(name = "legal_city", length = 100)
    private String legalCity;

    @Column(name = "legal_state", length = 100)
    private String legalState;

    @Column(name = "legal_postal_code", length = 50)
    private String legalPostalCode;

    @Column(name = "legal_country", length = 100)
    private String legalCountry;

    @Column(name = "customer_communication_address_line_1")
    private String customerCommunicationAddressLine1;

    @Column(name = "customer_communication_address_line_2")
    private String customerCommunicationAddressLine2;

    @Column(name = "customer_communication_city", length = 100)
    private String customerCommunicationCity;

    @Column(name = "customer_communication_state", length = 100)
    private String customerCommunicationState;

    @Column(name = "customer_communication_postal_code", length = 50)
    private String customerCommunicationPostalCode;

    @Column(name = "customer_communication_country", length = 100)
    private String customerCommunicationCountry;

    @Column(name = "primary_phone", length = 50)
    private String primaryPhone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "logo_file_id")
    private UUID logoFileId;

    @Column(name = "logo_original_filename")
    private String logoOriginalFilename;

    @Column(name = "logo_content_type", length = 100)
    private String logoContentType;

    @Column(name = "logo_size_bytes")
    private Long logoSizeBytes;

    @Column(name = "logo_storage_path")
    private String logoStoragePath;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "sync_token", nullable = false)
    private Integer syncToken;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc", nullable = false)
    private LocalDateTime updatedAtUtc;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @PrePersist
    void prePersist() {
        if (companyProfileId == null) {
            companyProfileId = UUID.randomUUID();
        }
        if (syncToken == null) {
            syncToken = 1;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAtUtc == null) {
            createdAtUtc = now;
        }
        if (updatedAtUtc == null) {
            updatedAtUtc = now;
        }
    }
}