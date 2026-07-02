package com.glassgang.pmworkflow.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfItemTypeGroup {

    private UUID itemTypeId;
    private String itemTypeCode;
    private String itemTypeName;

    private BigDecimal subtotalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private Boolean showPrice;

    private List<EstimatePdfItemLine> items = new ArrayList<>();
}