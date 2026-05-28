package com.glassgang.pmworkflow.estimate.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreateBidRevisionItemCostRequest {

    @NotNull(message = "costElementId is required")
    private UUID costElementId;

    private UUID costRateId;

    @Size(max = 255, message = "groupName must be <= 255 characters")
    private String groupName;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.0000", message = "quantity must be >= 0.0000")
    private BigDecimal quantity;

    @NotBlank(message = "unitOfMeasure is required")
    @Size(max = 50, message = "unitOfMeasure must be <= 50 characters")
    private String unitOfMeasure;

    @NotNull(message = "unitCost is required")
    @DecimalMin(value = "0.0000", message = "unitCost must be >= 0.0000")
    private BigDecimal unitCost;

    @NotNull(message = "unitPrice is required")
    @DecimalMin(value = "0.0000", message = "unitPrice must be >= 0.0000")
    private BigDecimal unitPrice;

    private Boolean isTaxable;

    @NotNull(message = "showCustomer is required")
    private Boolean showCustomer;

    @NotNull(message = "isOptional is required")
    private Boolean isOptional;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;
}