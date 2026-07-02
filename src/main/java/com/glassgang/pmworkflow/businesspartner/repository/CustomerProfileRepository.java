package com.glassgang.pmworkflow.businesspartner.repository;

import com.glassgang.pmworkflow.businesspartner.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {

    Optional<CustomerProfile> findByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(UUID businessPartnerId);

    boolean existsByBusinessPartner_BusinessPartnerIdAndIsDeletedFalse(UUID businessPartnerId);
}