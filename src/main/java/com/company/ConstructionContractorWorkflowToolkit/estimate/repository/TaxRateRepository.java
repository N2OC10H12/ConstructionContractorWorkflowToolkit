package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRateRepository extends JpaRepository<TaxRate, UUID> {

    Optional<TaxRate> findByTaxRateIdAndIsDeletedFalseAndIsActiveTrue(UUID taxRateId);

    Optional<TaxRate> findByTaxRateIdAndIsDeletedFalse(UUID taxRateId);

    Optional<TaxRate> findByCodeAndIsDeletedFalse(String code);

    boolean existsByCodeAndIsDeletedFalse(String code);

    Optional<TaxRate> findByIsDefaultTrueAndIsDeletedFalseAndIsActiveTrue();

    List<TaxRate> findByIsDeletedFalseOrderByCodeAsc();

    List<TaxRate> findByIsDeletedFalseAndIsActiveTrueOrderByCodeAsc();

    Optional<TaxRate> findByCode(String code);

    boolean existsByCode(String code);
}