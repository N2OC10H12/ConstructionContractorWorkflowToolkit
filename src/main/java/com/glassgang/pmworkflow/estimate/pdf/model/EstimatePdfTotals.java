package com.glassgang.pmworkflow.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EstimatePdfTotals {

    private BigDecimal subtotalCost;
    private BigDecimal subtotalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private BigDecimal customerFacingSubtotalPrice;
}