package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CostRateResponse {

    private UUID costRateId;
    private String code;
    private String name;
    private BigDecimal rateAmount;
    private String rateUnit;
    private Boolean isActive;
    private UUID costElementId;
    private String costElementCode;
    private String costElementName;
}
