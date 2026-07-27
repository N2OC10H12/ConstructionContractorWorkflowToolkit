package com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessPartnerRepository extends JpaRepository<BusinessPartner, UUID> {

    Optional<BusinessPartner> findByBusinessPartnerIdAndIsDeletedFalse(UUID businessPartnerId);

    List<BusinessPartner> findByIsDeletedFalseOrderByDisplayNameAsc();

    boolean existsByDisplayNameIgnoreCaseAndIsDeletedFalse(String displayName);
}