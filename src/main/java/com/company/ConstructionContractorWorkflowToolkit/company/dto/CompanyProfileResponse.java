package com.company.ConstructionContractorWorkflowToolkit.company.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CompanyProfileResponse(
        UUID companyProfileId,
        String profileCode,

        String companyName,
        String legalName,
        String employerId,
        String country,

        String companyAddressLine1,
        String companyAddressLine2,
        String companyCity,
        String companyState,
        String companyPostalCode,
        String companyCountry,

        String introductionData,

        String legalAddressLine1,
        String legalAddressLine2,
        String legalCity,
        String legalState,
        String legalPostalCode,
        String legalCountry,

        String customerCommunicationAddressLine1,
        String customerCommunicationAddressLine2,
        String customerCommunicationCity,
        String customerCommunicationState,
        String customerCommunicationPostalCode,
        String customerCommunicationCountry,

        String primaryPhone,
        String email,
        String website,

        UUID logoFileId,
        String logoOriginalFilename,
        String logoContentType,
        Long logoSizeBytes,
        String logoUrl,

        Integer syncToken,
        Boolean isActive,
        LocalDateTime createdAtUtc,
        LocalDateTime updatedAtUtc
) {
}