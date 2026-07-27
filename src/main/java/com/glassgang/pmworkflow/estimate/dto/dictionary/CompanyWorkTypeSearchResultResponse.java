package com.glassgang.pmworkflow.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyWorkTypeSearchResultResponse {

    private UUID companyWorkTypeId;

    private String code;

    private String name;

    private Integer level;

    private Boolean isSelectable;

    private String divisionCode;

    private String divisionName;

    private String displayLabel;
}