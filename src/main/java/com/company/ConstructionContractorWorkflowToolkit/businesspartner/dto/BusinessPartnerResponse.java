package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.BusinessPartnerType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BusinessPartnerResponse {
    private UUID businessPartnerId;

    private BusinessPartnerType partnerType;

    private String displayName;
    private String companyName;
    private String firstName;
    private String lastName;

    private String email;
    private String phone;
    private String website;

    private String internalNote;

    private Boolean isCustomer;
    private CustomerProfileResponse customerProfile;

    private Boolean isVendor;
    private VendorProfileResponse vendorProfile;

    private BusinessPartnerAddressResponse primaryBillingAddress;
    private BusinessPartnerAddressResponse primaryShippingAddress;
    private BusinessPartnerContactResponse primaryContact;

    private List<BusinessPartnerAddressResponse> addresses;
    private List<BusinessPartnerContactResponse> contacts;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}