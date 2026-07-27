package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteRevisionGroupCompanyWorkTypeRequest {

    @NotBlank
    @Size(max = 255)
    private String groupName;

    @NotNull
    private UUID companyWorkTypeId;
}