package com.company.ConstructionContractorWorkflowToolkit.businesspartner.repository;

import com.company.ConstructionContractorWorkflowToolkit.businesspartner.entity.BusinessPartnerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessPartnerAddressRepository extends JpaRepository<BusinessPartnerAddress, UUID> {

        Optional<BusinessPartnerAddress> findByBusinessPartnerAddressIdAndIsDeletedFalse(UUID businessPartnerAddressId);

        List<BusinessPartnerAddress> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByIsPrimaryDescAddressTypeAsc(
                        UUID businessPartnerId);

        List<BusinessPartnerAddress> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByCreatedAtUtcAsc(
                        UUID businessPartnerId);

        Optional<BusinessPartnerAddress> findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseAndIsPrimaryTrueOrderByUpdatedAtUtcDesc(
                        UUID businessPartnerId);

        Optional<BusinessPartnerAddress> findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByUpdatedAtUtcDesc(
                        UUID businessPartnerId);
}