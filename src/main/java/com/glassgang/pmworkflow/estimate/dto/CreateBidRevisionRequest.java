package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateBidRevisionRequest {

    private String taxType;

    private BigDecimal taxRatePercent;

    private String customerNote;

    private String internalNote;
}