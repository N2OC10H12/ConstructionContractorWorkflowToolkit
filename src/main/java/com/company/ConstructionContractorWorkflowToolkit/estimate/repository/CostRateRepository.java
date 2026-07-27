package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CostRate;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CostRateRepository extends JpaRepository<CostRate, UUID> {

    Optional<CostRate> findByCostRateIdAndIsDeletedFalseAndIsActiveTrue(UUID costRateId);

    Optional<CostRate> findByCostRateIdAndIsDeletedFalse(UUID costRateId);

    Optional<CostRate> findByCodeAndIsDeletedFalse(String code);

    boolean existsByCodeAndIsDeletedFalse(String code);

    List<CostRate> findByIsDeletedFalseAndIsActiveTrueOrderByCodeAsc();

    @EntityGraph(attributePaths = "costElement")
    List<CostRate> findByIsDeletedFalseOrderByCodeAsc();

    @EntityGraph(attributePaths = "costElement")
    List<CostRate> findByCostElement_CostElementIdAndIsDeletedFalseAndIsActiveTrueOrderByCodeAsc(
            UUID costElementId);

    Optional<CostRate> findByCode(String code);

    boolean existsByCode(String code);
}