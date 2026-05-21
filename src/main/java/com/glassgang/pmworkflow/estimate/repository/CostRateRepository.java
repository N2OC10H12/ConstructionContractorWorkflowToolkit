package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CostRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CostRateRepository extends JpaRepository<CostRate, UUID> {

    Optional<CostRate> findByCodeAndIsDeletedFalse(String code);
}