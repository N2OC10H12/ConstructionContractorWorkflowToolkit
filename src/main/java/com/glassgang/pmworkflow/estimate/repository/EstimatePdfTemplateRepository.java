package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.EstimatePdfTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstimatePdfTemplateRepository extends JpaRepository<EstimatePdfTemplate, UUID> {

    Optional<EstimatePdfTemplate> findFirstByIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse();

    Optional<EstimatePdfTemplate> findByCodeAndIsDeletedFalse(String code);
}