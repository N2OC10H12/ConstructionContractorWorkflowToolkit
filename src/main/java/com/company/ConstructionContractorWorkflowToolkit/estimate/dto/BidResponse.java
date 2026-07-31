package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidStatus;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.ConstructionType;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.DepartmentCode;

@Getter
@Setter
public class BidResponse {
    private UUID bidId;
    private UUID customerId;
    private BidOwnerResponse owner;
    private String bidNumber;
    private String jobNumber;
    private String jobName;
    private String jobAddressLine1;
    private String jobAddressLine2;
    private String jobCity;
    private String jobState;
    private String jobPostalCode;
    private String jobCountry;
    private String description;
    private String estimateScope;
    private DepartmentCode departmentCode;
    private BidStatus bidStatus;
    private UUID currentRevisionId;
    private UUID convertedProjectId;
    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
    private ConstructionType constructionType;
    private UUID constructionObjectTypeId;
    private String constructionObjectTypeCode;
    private String constructionObjectTypeName;
    private UUID defaultTaxRateId;
    private String defaultTaxRateCode;
    private String defaultTaxRateName;
    private BigDecimal defaultTaxRatePercent;
    private BidRoundingMode roundingMode;
}