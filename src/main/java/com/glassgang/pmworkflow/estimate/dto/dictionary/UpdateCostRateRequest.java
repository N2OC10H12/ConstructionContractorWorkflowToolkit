package com.glassgang.pmworkflow.estimate.dto.dictionary;

import com.glassgang.pmworkflow.estimate.enums.CostRateUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCostRateRequest {

    @Size(max = 100, message = "code must be <= 100 characters")
    private String code;

    @Size(max = 255, message = "name must be <= 255 characters")
    private String name;

    private String description;

    @DecimalMin(value = "0.0000", message = "rateAmount must be >= 0")
    private BigDecimal rateAmount;

    private CostRateUnit rateUnit;

    private Boolean isActive;
}