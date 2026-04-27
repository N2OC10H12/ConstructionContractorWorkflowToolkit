package com.glassgang.pmworkflow.project.service;

import com.glassgang.pmworkflow.project.entity.ComputedStatus;
import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.entity.ProjectStep;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProjectStatusService {

    public ComputedStatus computeSubstepStatus(ProjectSubstep substep) {
        return Boolean.TRUE.equals(substep.getIsDone())
                ? ComputedStatus.GREEN
                : ComputedStatus.NEUTRAL;
    }

    public ComputedStatus computeStepStatus(ProjectStep step) {
        boolean allDone = step.getSubsteps().stream()
                .allMatch(s -> Boolean.TRUE.equals(s.getIsDone()));

        if (allDone) {
            return ComputedStatus.GREEN;
        }

        if (step.getDeadline() != null &&
                step.getDeadline().isBefore(LocalDate.now())) {
            return ComputedStatus.RED;
        }

        return ComputedStatus.NEUTRAL;
    }

    public ComputedStatus computeProjectStatus(Project project) {
        boolean allDone = project.getSteps().stream()
                .allMatch(step ->
                        step.getSubsteps().stream()
                                .allMatch(s -> Boolean.TRUE.equals(s.getIsDone()))
                );

        if (allDone) {
            return ComputedStatus.GREEN;
        }

        if (project.getProjectDeadline() != null &&
                project.getProjectDeadline().isBefore(LocalDate.now())) {
            return ComputedStatus.RED;
        }

        return ComputedStatus.NEUTRAL;
    }
}