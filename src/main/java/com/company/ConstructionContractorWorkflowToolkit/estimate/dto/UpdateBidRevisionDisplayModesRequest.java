package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CustomerDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimatePriceDisplayMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBidRevisionDisplayModesRequest {

    @NotNull(message = "customerDisplayMode is required")
    private CustomerDisplayMode customerDisplayMode;

    @NotNull(message = "priceDisplayMode is required")
    private EstimatePriceDisplayMode priceDisplayMode;
}