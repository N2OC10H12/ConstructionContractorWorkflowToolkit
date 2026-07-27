package com.company.ConstructionContractorWorkflowToolkit.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EstimatePdfGroup {

    private String groupName;

    private BigDecimal subtotalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private Boolean showPrice;

    private List<EstimatePdfWorkTypeGroup> workTypes = new ArrayList<>();
}