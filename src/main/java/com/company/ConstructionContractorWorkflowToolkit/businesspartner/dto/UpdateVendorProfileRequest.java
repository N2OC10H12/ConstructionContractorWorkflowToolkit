package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.VendorCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateVendorProfileRequest {

    @NotNull
    private VendorCategory vendorCategory;

    @NotNull
    private Boolean vendor1099;

    @Size(max = 4)
    private String taxIdentifierLast4;

    @Size(max = 100)
    private String accountNumber;

    @Size(max = 100)
    private String defaultPaymentTerms;

    private LocalDate insuranceExpirationDate;

    @Size(max = 4000)
    private String internalNote;
}