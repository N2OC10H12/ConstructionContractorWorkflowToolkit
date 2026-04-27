package com.glassgang.pmworkflow.project.repository;

import com.glassgang.pmworkflow.project.entity.ProjectStep;
import com.glassgang.pmworkflow.project.entity.ProjectSubstep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectSubstepRepository extends JpaRepository<ProjectSubstep, UUID> {

    List<ProjectSubstep> findByStep(ProjectStep step);
}