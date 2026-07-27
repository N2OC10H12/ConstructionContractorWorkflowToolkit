package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerContactRepository
        extends JpaRepository<CustomerContact, UUID> {
}