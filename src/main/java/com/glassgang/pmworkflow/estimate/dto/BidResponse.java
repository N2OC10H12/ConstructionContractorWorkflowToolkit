package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.glassgang.pmworkflow.estimate.enums.BidStatus;
import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;

@Getter
@Setter
public class BidResponse {
    private UUID bidId;
    private UUID customerId;
    private String bidNumber;
    private String jobNumber;
    private String jobName;
    private String description;
    private DepartmentCode departmentCode;
    private BidStatus bidStatus;
    private UUID currentRevisionId;
    private UUID convertedProjectId;
    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}
