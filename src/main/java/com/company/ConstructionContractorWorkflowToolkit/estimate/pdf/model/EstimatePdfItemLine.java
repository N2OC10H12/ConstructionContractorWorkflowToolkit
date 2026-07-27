package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfItemLine {

    private UUID bidRevisionItemId;

    private Integer lineNumber;
    private Integer displayOrder;

    private String description;
    private BigDecimal quantity;
    private String unitOfMeasure;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal priceWithTax;

    private Boolean isOptional;
    private Boolean showCustomerRow;
    private Boolean showCustomerPrice;

    private Boolean showPrice;
    private Boolean showCostLines;

    private String customerNote;

    private String taxRateCode;
    private String taxRateName;
    private BigDecimal taxRatePercent;

    private List<EstimatePdfItemCostLine> costs = new ArrayList<>();
}