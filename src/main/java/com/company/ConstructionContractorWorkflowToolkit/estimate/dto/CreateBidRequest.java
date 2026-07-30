package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.ConstructionType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.DepartmentCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBidRequest {

    @NotNull(message = "customerId is required")
    private UUID customerId;

    @NotNull(message = "departmentCode is required")
    private DepartmentCode departmentCode;

    @NotBlank(message = "jobName is required")
    @Size(max = 255, message = "jobName must be <= 255 characters")
    private String jobName;

    @Size(max = 255, message = "jobAddressLine1 must be <= 255 characters")
    private String jobAddressLine1;

    @Size(max = 255, message = "jobAddressLine2 must be <= 255 characters")
    private String jobAddressLine2;

    @Size(max = 100, message = "jobCity must be <= 100 characters")
    private String jobCity;

    @Size(max = 100, message = "jobState must be <= 100 characters")
    private String jobState;

    @Size(max = 30, message = "jobPostalCode must be <= 30 characters")
    private String jobPostalCode;

    @Size(max = 100, message = "jobCountry must be <= 100 characters")
    private String jobCountry;

    @Size(max = 4000, message = "description must be <= 4000 characters")
    private String description;

    @Size(max = 1000, message = "estimateScope must be <= 1000 characters")
    private String estimateScope;

    @NotNull(message = "constructionType is required")
    private ConstructionType constructionType;

    private UUID constructionObjectTypeId;

    private UUID defaultTaxRateId;
}