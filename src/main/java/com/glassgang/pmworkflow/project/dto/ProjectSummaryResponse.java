package com.glassgang.pmworkflow.project.dto;

import com.glassgang.pmworkflow.project.entity.ComputedStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ProjectSummaryResponse {

    private UUID id;
    private String name;
    private ComputedStatus status;
    private LocalDate projectDeadline;
}