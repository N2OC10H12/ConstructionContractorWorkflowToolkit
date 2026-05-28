package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CostElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface CostElementRepository extends JpaRepository<CostElement, UUID> {

    Optional<CostElement> findByCostElementIdAndIsDeletedFalseAndIsActiveTrue(
            UUID costElementId
    );

    List<CostElement> findByIsDeletedFalseOrderByCodeAsc();
}