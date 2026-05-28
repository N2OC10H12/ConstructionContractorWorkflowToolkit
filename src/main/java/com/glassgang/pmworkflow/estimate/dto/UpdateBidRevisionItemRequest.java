package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateBidRevisionItemRequest {

    @Size(max = 255, message = "groupName must be <= 255 characters")
    private String groupName;

    @Size(max = 4000, message = "description must be <= 4000 characters")
    private String description;

    @DecimalMin(value = "0.0000", message = "quantity must be >= 0.0000")
    private BigDecimal quantity;

    @Size(max = 50, message = "unitOfMeasure must be <= 50 characters")
    private String unitOfMeasure;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;

    private Boolean isOptional;

    private UUID itemTypeId;

    private UUID taxRateId;

    private CustomerDisplayMode customerDisplayMode;
}