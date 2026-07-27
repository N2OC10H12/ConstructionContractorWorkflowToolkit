package com.company.ConstructionContractorWorkflowToolkit.estimate.dto.pdf;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PreviewEstimatePdfTemplateRequest {

    @NotNull
    private UUID bidRevisionId;

    @NotBlank
    private String htmlTemplate;

    private String cssTemplate;

    private String templateDefinitionJson;
}