package com.company.ConstructionContractorWorkflowToolkit.file.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteSubstepFilesRequest {

    @NotEmpty
    private List<UUID> fileIds;
}