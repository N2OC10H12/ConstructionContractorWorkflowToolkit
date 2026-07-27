package com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateCompanyWorkTypeRequest {

    @Size(max = 30, message = "code must be <= 30 characters")
    private String code;

    @Size(max = 200, message = "name must be <= 200 characters")
    private String name;

    private UUID parentWorkTypeId;

    private Boolean clearParentWorkType;

    @Size(max = 2000, message = "searchAliases must be <= 2000 characters")
    private String searchAliases;

    private Boolean isActive;
}