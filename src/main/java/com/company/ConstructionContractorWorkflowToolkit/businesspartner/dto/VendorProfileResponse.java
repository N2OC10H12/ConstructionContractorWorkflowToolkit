package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.VendorCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class VendorProfileResponse {
    private UUID vendorProfileId;
    private UUID businessPartnerId;

    private VendorCategory vendorCategory;

    private Boolean vendor1099;
    private String taxIdentifierLast4;
    private String accountNumber;
    private String defaultPaymentTerms;
    private LocalDate insuranceExpirationDate;
    private String internalNote;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}