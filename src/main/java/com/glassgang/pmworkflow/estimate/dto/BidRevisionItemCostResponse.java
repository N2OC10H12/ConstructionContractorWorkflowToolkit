package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BidRevisionItemCostResponse {

    private UUID bidRevisionItemCostId;
    private UUID bidRevisionItemId;
    private UUID costElementId;
    private String costElementCode;
    private String costElementName;
    private UUID costRateId;
    private String costRateCode;
    private String costRateName;

    private Integer lineNumber;
    private Integer displayOrder;

    private String groupName;

    private BigDecimal quantity;
    private String unitOfMeasure;

    private BigDecimal rateSnapshot;
    private String rateUnitSnapshot;

    private BigDecimal unitCost;
    private BigDecimal unitPrice;
    private BigDecimal totalCost;
    private BigDecimal totalPrice;

    private BigDecimal markupPercent;
    private BigDecimal gpmPercent;

    private Boolean isTaxable;
    private BigDecimal taxAmount;
    private BigDecimal priceWithTax;

    private Boolean showCustomer;
    private Boolean isOptional;

    private String internalNote;
    private String customerNote;

    private UUID clonedFromItemCostId;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}