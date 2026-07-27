package com.company.ConstructionContractorWorkflowToolkit.file.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SubstepFileResponse {

    private UUID id;
    private UUID substepId;
    private String fileName;
    private UUID uploadedBy;
    private LocalDateTime uploadedAt;
    private String previewUrl;
    private String downloadUrl;
}