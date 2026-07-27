package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBusinessPartnerAddressRequest {

    @NotNull
    private AddressType addressType;

    @NotBlank
    @Size(max = 255)
    private String line1;

    @Size(max = 255)
    private String line2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 50)
    private String postalCode;

    @Size(max = 100)
    private String country;

    @NotNull
    private Boolean isPrimary;
}