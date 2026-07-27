package com.company.ConstructionContractorWorkflowToolkit.workflow.repository;

import com.company.ConstructionContractorWorkflowToolkit.workflow.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, UUID> {

    Optional<WorkflowTemplate> findByIsDefaultTrue();
}