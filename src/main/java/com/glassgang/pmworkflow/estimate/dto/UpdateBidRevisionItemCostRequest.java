package com.glassgang.pmworkflow.estimate.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class UpdateBidRevisionItemCostRequest {

    private UUID costElementId;

    private UUID costRateId;

    @Size(max = 255, message = "groupName must be <= 255 characters")
    private String groupName;

    @DecimalMin(value = "0.0000", message = "quantity must be >= 0.0000")
    private BigDecimal quantity;

    @Size(max = 50, message = "unitOfMeasure must be <= 50 characters")
    private String unitOfMeasure;

    @DecimalMin(value = "0.0000", message = "unitCost must be >= 0.0000")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0000", message = "unitPrice must be >= 0.0000")
    private BigDecimal unitPrice;

    private Boolean showCustomer;

    private Boolean isTaxable;

    private Boolean isOptional;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;

    @Size(max = 4000, message = "customerNote must be <= 4000 characters")
    private String customerNote;

    @DecimalMin(value = "0.0000", message = "markupPercent must be >= 0.0000")
    private BigDecimal markupPercent;
}