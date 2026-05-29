package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CostElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CostElementRepository extends JpaRepository<CostElement, UUID> {

    Optional<CostElement> findByCostElementIdAndIsDeletedFalseAndIsActiveTrue(UUID costElementId);

    Optional<CostElement> findByCostElementIdAndIsDeletedFalse(UUID costElementId);

    Optional<CostElement> findByCodeAndIsDeletedFalse(String code);

    boolean existsByCodeAndIsDeletedFalse(String code);

    List<CostElement> findByIsDeletedFalseAndIsActiveTrueOrderByCodeAsc();

    List<CostElement> findByIsDeletedFalseOrderByCodeAsc();
}