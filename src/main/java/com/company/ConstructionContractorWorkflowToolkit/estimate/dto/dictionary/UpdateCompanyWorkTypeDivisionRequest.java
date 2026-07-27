package com.company.ConstructionContractorWorkflowToolkit.estimate.dto.dictionary;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanyWorkTypeDivisionRequest {

    @NotNull(message = "isEnabled is required")
    private Boolean isEnabled;
}