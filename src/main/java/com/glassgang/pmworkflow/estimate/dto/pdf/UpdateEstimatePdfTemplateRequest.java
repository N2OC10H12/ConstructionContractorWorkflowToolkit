package com.glassgang.pmworkflow.estimate.dto.pdf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEstimatePdfTemplateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String htmlTemplate;

    private String cssTemplate;

    private String templateDefinitionJson;

    @NotNull
    private Boolean isActive;

    private String changeNote;
}