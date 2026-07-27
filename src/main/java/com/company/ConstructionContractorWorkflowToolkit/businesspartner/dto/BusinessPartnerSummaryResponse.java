package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.CustomerCategory;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.VendorCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BusinessPartnerSummaryResponse {
    private UUID businessPartnerId;

    private String displayName;
    private String companyName;
    private String email;
    private String phone;

    private CustomerCategory customerCategory;
    private VendorCategory vendorCategory;
}