package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import com.glassgang.pmworkflow.estimate.enums.EstimatePriceDisplayMode;
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