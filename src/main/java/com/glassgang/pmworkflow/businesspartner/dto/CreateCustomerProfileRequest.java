package com.glassgang.pmworkflow.businesspartner.dto;

import com.glassgang.pmworkflow.businesspartner.enums.CustomerCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCustomerProfileRequest {

    @NotNull
    private CustomerCategory customerCategory;

    @NotNull
    private Boolean defaultTaxable;

    @Size(max = 100)
    private String resaleNumber;

    @Size(max = 4000)
    private String internalNote;
}