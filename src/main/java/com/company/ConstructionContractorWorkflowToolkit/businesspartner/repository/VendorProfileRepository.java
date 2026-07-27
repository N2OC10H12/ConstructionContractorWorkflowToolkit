package com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.VendorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VendorProfileRepository extends JpaRepository<VendorProfile, UUID> {

    Optional<VendorProfile> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(UUID businessPartnerId);

    boolean existsByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(UUID businessPartnerId);
}