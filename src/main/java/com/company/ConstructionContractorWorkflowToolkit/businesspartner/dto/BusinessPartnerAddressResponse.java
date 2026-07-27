package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BusinessPartnerAddressResponse {
    private UUID businessPartnerAddressId;
    private UUID businessPartnerId;

    private AddressType addressType;

    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private Boolean isPrimary;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}