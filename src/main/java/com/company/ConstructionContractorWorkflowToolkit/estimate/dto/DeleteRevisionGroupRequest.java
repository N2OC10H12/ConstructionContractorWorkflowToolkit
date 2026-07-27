package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteRevisionGroupRequest {
    @NotBlank
    @Size(max = 255)
    private String groupName;
}
