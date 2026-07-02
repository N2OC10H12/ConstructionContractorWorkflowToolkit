package com.glassgang.pmworkflow.businesspartner.repository;

import com.glassgang.pmworkflow.businesspartner.entity.BusinessPartnerContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessPartnerContactRepository extends JpaRepository<BusinessPartnerContact, UUID> {

        Optional<BusinessPartnerContact> findByBusinessPartnerContactIdAndIsDeletedFalse(UUID businessPartnerContactId);

        List<BusinessPartnerContact> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByIsPrimaryDescContactNameAsc(
                        UUID businessPartnerId);

        List<BusinessPartnerContact> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByCreatedAtUtcAsc(
                        UUID businessPartnerId);

        Optional<BusinessPartnerContact> findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseAndIsPrimaryTrueOrderByUpdatedAtUtcDesc(
                        UUID businessPartnerId);

        Optional<BusinessPartnerContact> findFirstByBusinessPartner_BusinessPartnerIdAndIsDeletedFalseOrderByUpdatedAtUtcDesc(
                        UUID businessPartnerId);
}