package com.company.ConstructionContractorWorkflowToolkit.note.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SubstepNoteResponse {
    private UUID id;
    private UUID substepId;
    private String noteText;
    private UUID createdBy;
    private LocalDateTime createdAt;
}