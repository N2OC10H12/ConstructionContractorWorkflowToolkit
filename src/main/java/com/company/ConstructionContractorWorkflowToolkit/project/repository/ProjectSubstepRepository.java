package com.company.ConstructionContractorWorkflowToolkit.project.repository;

import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectStep;
import com.company.ConstructionContractorWorkflowToolkit.project.entity.ProjectSubstep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectSubstepRepository extends JpaRepository<ProjectSubstep, UUID> {

    List<ProjectSubstep> findByStep(ProjectStep step);
}