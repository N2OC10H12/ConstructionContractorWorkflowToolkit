package com.glassgang.pmworkflow.estimate.dto.dictionary;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCostElementRequest {

    @Size(max = 100, message = "code must be <= 100 characters")
    private String code;

    @Size(max = 255, message = "name must be <= 255 characters")
    private String name;

    private String description;

    private Boolean isActive;
}