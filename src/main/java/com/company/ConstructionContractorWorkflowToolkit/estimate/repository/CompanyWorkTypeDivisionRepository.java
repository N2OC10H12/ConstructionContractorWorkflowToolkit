package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CompanyWorkTypeDivision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyWorkTypeDivisionRepository
        extends JpaRepository<CompanyWorkTypeDivision, UUID> {

    Optional<CompanyWorkTypeDivision>
    findByDivisionCode(String divisionCode);

    boolean existsByDivisionCode(String divisionCode);

    List<CompanyWorkTypeDivision>
    findAllByOrderByDivisionCodeAsc();

    List<CompanyWorkTypeDivision>
    findByIsEnabledTrueOrderByDivisionCodeAsc();
}