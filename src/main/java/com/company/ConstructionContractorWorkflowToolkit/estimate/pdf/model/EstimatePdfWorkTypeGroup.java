package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfWorkTypeGroup {

    private UUID workTypeId;
    private String workTypeCode;
    private String workTypeName;

    private BigDecimal subtotalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private Boolean showPrice;

    private List<EstimatePdfItemLine> items = new ArrayList<>();
}