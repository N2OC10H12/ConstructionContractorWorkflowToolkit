package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.ConstructionType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.DepartmentCode;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBidFromRevisionRequest {

    private UUID customerId;

    @Size(max = 255, message = "jobName must be <= 255 characters")
    private String jobName;

    @Size(max = 4000, message = "description must be <= 4000 characters")
    private String description;

    @Size(max = 1000, message = "estimateScope must be <= 1000 characters")
    private String estimateScope;

    private DepartmentCode departmentCode;
    private ConstructionType constructionType;
    private UUID constructionObjectTypeId;
    private BidRoundingMode roundingMode;
}
