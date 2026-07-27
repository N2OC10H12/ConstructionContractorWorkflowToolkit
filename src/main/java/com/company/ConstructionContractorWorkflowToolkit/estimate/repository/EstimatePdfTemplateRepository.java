package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.EstimatePdfTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstimatePdfTemplateRepository extends JpaRepository<EstimatePdfTemplate, UUID> {

    Optional<EstimatePdfTemplate> findFirstByIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse();

    Optional<EstimatePdfTemplate> findByCodeAndIsDeletedFalse(String code);
}