package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface TaxRateRepository extends JpaRepository<TaxRate, UUID> {

    Optional<TaxRate> findByTaxRateIdAndIsDeletedFalseAndIsActiveTrue(UUID taxRateId);

    Optional<TaxRate> findByIsDefaultTrueAndIsDeletedFalseAndIsActiveTrue();

    List<TaxRate> findByIsDeletedFalseOrderByCodeAsc();
}