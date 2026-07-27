package com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerExternalRef;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ExternalEntityType;
import com.company.ConstructionContractorWorkflowToolkit.businesspartner.enums.ExternalSystem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessPartnerExternalRefRepository extends JpaRepository<BusinessPartnerExternalRef, UUID> {

    Optional<BusinessPartnerExternalRef> findByBusinessPartnerExternalRefIdAndIsDeletedFalse(
            UUID businessPartnerExternalRefId
    );

    List<BusinessPartnerExternalRef> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(
            UUID businessPartnerId
    );

    Optional<BusinessPartnerExternalRef> findByExternalSystemAndExternalEntityTypeAndRealmIdAndExternalIdAndIsDeletedFalse(
            ExternalSystem externalSystem,
            ExternalEntityType externalEntityType,
            String realmId,
            String externalId
    );
}