package com.glassgang.pmworkflow.estimate.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBidRevisionRequest {

    @Size(max = 4000, message = "customerNote must be <= 4000 characters")
    private String customerNote;

    @Size(max = 4000, message = "internalNote must be <= 4000 characters")
    private String internalNote;
}