package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.ConstructionType;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateBidRequest {

    private UUID customerId;
    private String jobName;
    private String description;
    private DepartmentCode departmentCode;
    private ConstructionType constructionType;
}