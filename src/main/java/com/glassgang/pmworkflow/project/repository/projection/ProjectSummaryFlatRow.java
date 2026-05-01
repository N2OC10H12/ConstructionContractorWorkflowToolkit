package com.glassgang.pmworkflow.project.repository.projection;

import java.time.LocalDate;
import java.util.UUID;

public interface ProjectSummaryFlatRow {

    UUID getProjectId();

    String getProjectName();

    LocalDate getProjectDeadline();

    UUID getOwnerId();

    String getOwnerUsername();

    String getOwnerRole();

    UUID getStepId();

    String getStepName();

    Integer getStepOrderIndex();

    LocalDate getStepDeadline();

    UUID getSubstepId();

    String getSubstepName();

    Integer getSubstepOrderIndex();

    Boolean getSubstepIsDone();
}