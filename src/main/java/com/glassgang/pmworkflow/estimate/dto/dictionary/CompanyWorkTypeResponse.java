package com.glassgang.pmworkflow.estimate.dto.dictionary;

import com.glassgang.pmworkflow.estimate.enums.CompanyWorkTypeSourceType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyWorkTypeResponse {

    private UUID companyWorkTypeId;

    private String code;

    private String name;

    private Integer level;

    private String divisionCode;

    private String divisionName;

    private UUID parentWorkTypeId;

    private String parentWorkTypeCode;

    private String parentWorkTypeName;

    private CompanyWorkTypeSourceType sourceType;

    private String sourceEdition;

    private String originalName;

    private String searchAliases;

    private Integer displayOrder;

    private Boolean isActive;
}