package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, UUID> {
}