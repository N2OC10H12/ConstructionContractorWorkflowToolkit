package com.glassgang.pmworkflow.estimate.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateBidRevisionRequest {

    @Size(max = 50, message = "taxType must be <= 50 characters")
    private String taxType;

    @DecimalMin(value = "0.0000", message = "taxRatePercent must be >= 0.0000")
    @DecimalMax(value = "100.0000", message = "taxRatePercent must be <= 100.0000")
    private BigDecimal taxRatePercent;

    @Size(max = 4000, message = "customerNote must be <= 4000 characters")
    private String customerNote;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;
}