package com.glassgang.pmworkflow.estimate.dto.dictionary;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItemTypeRequest {

    @Size(max = 50, message = "code must be <= 50 characters")
    private String code;

    @Size(max = 150, message = "name must be <= 150 characters")
    private String name;

    private String description;

    private Boolean isActive;
}