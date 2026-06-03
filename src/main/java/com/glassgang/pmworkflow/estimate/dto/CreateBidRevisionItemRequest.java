package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateBidRevisionItemRequest {

    @Size(max = 255, message = "groupName must be <= 255 characters")
    private String groupName;

    @NotBlank(message = "description is required")
    @Size(max = 4000, message = "description must be <= 4000 characters")
    private String description;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.0000", message = "quantity must be >= 0.0000")
    private BigDecimal quantity;

    @NotBlank(message = "unitOfMeasure is required")
    @Size(max = 50, message = "unitOfMeasure must be <= 50 characters")
    private String unitOfMeasure;

    @NotNull(message = "isOptional is required")
    private Boolean isOptional;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;

    @NotNull(message = "itemTypeId is required")
    private UUID itemTypeId;

    @NotNull(message = "taxRateId is required")
    private UUID taxRateId;

    @NotNull(message = "customerDisplayMode is required")
    private CustomerDisplayMode customerDisplayMode;

    @Size(max = 4000, message = "customerNote must be <= 4000 characters")
    private String customerNote;

    @DecimalMin(value = "0.0000", message = "unitCost must be >= 0.0000")
    private BigDecimal unitCost;

    @DecimalMin(value = "0.0000", message = "markupPercent must be >= 0.0000")
    private BigDecimal markupPercent;
}