package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TaxRateResponse {

    private UUID taxRateId;
    private String code;
    private String name;
    private BigDecimal ratePercent;
    private Boolean isDefault;
    private Boolean isActive;
}