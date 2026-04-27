package com.glassgang.pmworkflow.project.repository;

import com.glassgang.pmworkflow.project.entity.Project;
import com.glassgang.pmworkflow.project.entity.ProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectStepRepository extends JpaRepository<ProjectStep, UUID> {

    List<ProjectStep> findByProject(Project project);
}