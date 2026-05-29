package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CostRate;
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

    List<CostRate> findByIsDeletedFalseOrderByCodeAsc();
}