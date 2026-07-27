package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstimatePdfTemplateVersionRepository
        extends JpaRepository<EstimatePdfTemplateVersion, UUID> {
}