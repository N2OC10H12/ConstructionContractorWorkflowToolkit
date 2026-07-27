package com.company.ConstructionContractorWorkflowToolkit.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProjectOwnerResponse {
    private UUID id;
    private String username;
    private String role;
}