package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class BidRevisionItemResponse {

    private UUID bidRevisionItemId;

    private UUID bidRevisionId;

    private Integer lineNumber;

    private Integer displayOrder;

    private String groupName;

    private String description;

    private BigDecimal quantity;

    private String unitOfMeasure;

    private BigDecimal unitCost;

    private BigDecimal unitPrice;

    private BigDecimal totalCost;

    private BigDecimal totalPrice;

    private BigDecimal markupPercent;

    private BigDecimal gpmPercent;

    private Boolean isTaxable;

    private BigDecimal taxAmount;

    private BigDecimal priceWithTax;

    private Boolean isOptional;

    private Boolean showCustomerRow;

    private Boolean showCustomerPrice;

    private String internalNote;

    private String customerNote;

    private UUID clonedFromItemId;

    private LocalDateTime createdAtUtc;

    private LocalDateTime updatedAtUtc;

    private UUID companyWorkTypeId;

    private String companyWorkTypeCode;

    private String companyWorkTypeName;

    private String companyWorkTypeDisplayLabel;

    private UUID taxRateId;

    private String taxRateCode;

    private String taxRateName;

    private BigDecimal taxRatePercent;

}