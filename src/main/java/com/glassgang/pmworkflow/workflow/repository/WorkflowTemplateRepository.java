package com.glassgang.pmworkflow.workflow.repository;

import com.glassgang.pmworkflow.workflow.entity.WorkflowTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkflowTemplateRepository extends JpaRepository<WorkflowTemplate, UUID> {

    Optional<WorkflowTemplate> findByIsDefaultTrue();
}