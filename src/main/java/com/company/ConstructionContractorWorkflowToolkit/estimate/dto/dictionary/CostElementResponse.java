package com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CostElementResponse {

    private UUID costElementId;
    private String code;
    private String name;
    private String description;
    private Boolean isActive;
}