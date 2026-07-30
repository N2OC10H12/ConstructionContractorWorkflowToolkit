package com.company.ConstructionContractorWorkflowToolkit.company.repository;

import com.company.ConstructionContractorWorkflowToolkit.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, UUID> {

    Optional<CompanyProfile> findByProfileCodeAndIsActiveTrueAndIsDeletedFalse(String profileCode);
    
    boolean existsByLogoStoredFile_StoredFileId(UUID storedFileId);
}