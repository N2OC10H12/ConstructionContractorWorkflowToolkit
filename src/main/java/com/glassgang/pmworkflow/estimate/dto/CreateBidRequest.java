package com.glassgang.pmworkflow.estimate.dto;

import com.glassgang.pmworkflow.estimate.enums.DepartmentCode;
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

    @Size(max = 4000, message = "description must be <= 4000 characters")
    private String description;
}