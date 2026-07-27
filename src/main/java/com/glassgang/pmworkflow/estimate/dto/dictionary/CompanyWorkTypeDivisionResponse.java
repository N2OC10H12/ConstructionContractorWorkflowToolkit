package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CompanyWorkTypeDivisionResponse {

    private UUID companyWorkTypeDivisionId;

    private String divisionCode;

    private String divisionName;

    private Boolean isEnabled;

    private LocalDateTime enabledAtUtc;

    private UUID enabledByUserId;

    private Long activeWorkTypeCount;
}