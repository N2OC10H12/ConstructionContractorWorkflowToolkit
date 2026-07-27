package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.BusinessPartnerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateBusinessPartnerRequest {

    @NotNull
    private BusinessPartnerType partnerType;

    @NotBlank
    @Size(max = 255)
    private String displayName;

    @Size(max = 255)
    private String companyName;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 50)
    private String phone;

    @Size(max = 255)
    private String website;

    @Size(max = 4000)
    private String internalNote;

    @Valid
    @Size(max = 20)
    private List<CreateBusinessPartnerAddressRequest> addresses = new ArrayList<>();

    @Valid
    @Size(max = 20)
    private List<CreateBusinessPartnerContactRequest> contacts = new ArrayList<>();

    @Valid
    private CreateCustomerProfileRequest customerProfile;

    @Valid
    private CreateVendorProfileRequest vendorProfile;
}