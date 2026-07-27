package com.company.ConstructionContractorWorkflowToolkit.businesspartner.dto;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.CustomerCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerProfileRequest {

    @NotNull
    private CustomerCategory customerCategory;

    @NotNull
    private Boolean defaultTaxable;

    @Size(max = 100)
    private String resaleNumber;

    @Size(max = 4000)
    private String internalNote;
}