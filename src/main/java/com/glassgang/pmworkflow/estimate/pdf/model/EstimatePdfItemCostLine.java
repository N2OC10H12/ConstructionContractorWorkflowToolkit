package com.glassgang.pmworkflow.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfItemCostLine {

    private UUID bidRevisionItemCostId;

    private Integer lineNumber;
    private Integer displayOrder;

    private String costElementCode;
    private String costElementName;

    private String costRateCode;
    private String costRateName;

    private BigDecimal quantity;
    private String unitOfMeasure;

    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal priceWithTax;

    private Boolean isOptional;

    private Boolean showPrice;

    private String customerNote;
}