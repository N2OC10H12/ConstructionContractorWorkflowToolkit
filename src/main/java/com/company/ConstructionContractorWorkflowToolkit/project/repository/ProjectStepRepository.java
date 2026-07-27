package com.company.ConstructionContractorWorkflowToolkit.project.repository;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.Project;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectStepRepository extends JpaRepository<ProjectStep, UUID> {

    List<ProjectStep> findByProject(Project project);
}