package com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTaxRateRequest {

    @NotBlank(message = "code is required")
    @Size(max = 50, message = "code must be <= 50 characters")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must be <= 150 characters")
    private String name;

    @NotNull(message = "ratePercent is required")
    @DecimalMin(value = "0.0000", message = "ratePercent must be >= 0")
    private BigDecimal ratePercent;

    private Boolean isDefault;

    private Boolean isActive;
}