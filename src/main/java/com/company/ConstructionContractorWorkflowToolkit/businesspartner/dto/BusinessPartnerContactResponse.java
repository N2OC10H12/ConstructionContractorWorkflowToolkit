package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ContactRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BusinessPartnerContactResponse {
    private UUID businessPartnerContactId;
    private UUID businessPartnerId;

    private String contactName;
    private String title;
    private String email;
    private String phone;
    private String mobilePhone;

    private ContactRole contactRole;

    private Boolean isPrimary;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}