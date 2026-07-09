package com.glassgang.pmworkflow.estimate.dto.pdf;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EstimatePdfTemplateResponse {

    private UUID estimatePdfTemplateId;

    private String code;
    private String name;

    private String htmlTemplate;
    private String cssTemplate;

    private Boolean isDefault;
    private Boolean isActive;

    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;

    private String templateDefinitionJson;
    private Integer versionNumber;
}