package com.company.ConstructionContractorWorkflowToolkit.project.repository.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface ProjectStepSummaryRow {

    UUID getProjectId();

    String getProjectName();

    LocalDate getProjectDeadline();

    UUID getStepId();

    String getStepName();

    Integer getStepOrderIndex();

    LocalDate getStepDeadline();

    Long getTotalSubsteps();

    Long getDoneSubsteps();
}