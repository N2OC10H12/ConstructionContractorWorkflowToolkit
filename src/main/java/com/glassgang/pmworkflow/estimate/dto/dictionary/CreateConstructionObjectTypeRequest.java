package com.glassgang.pmworkflow.estimate.dto.dictionary;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConstructionObjectTypeRequest {

    @NotBlank(message = "code is required")
    @Size(max = 50, message = "code must be <= 50 characters")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must be <= 150 characters")
    private String name;

    private String description;

    private Boolean isActive;
}