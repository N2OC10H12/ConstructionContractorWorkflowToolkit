package com.glassgang.pmworkflow.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateStepDeadlineRequest {

    private LocalDate deadline;
}