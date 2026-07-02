package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.ConstructionType;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
import com.glassgang.pmworkflow.estimate.enums.EstimatePriceDisplayMode;

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

    private DepartmentCode departmentCode;
    private ConstructionType constructionType;
    private EstimatePriceDisplayMode priceDisplayMode;
}
