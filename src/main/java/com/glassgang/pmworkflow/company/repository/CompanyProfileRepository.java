package com.glassgang.pmworkflow.company.repository;

import com.glassgang.pmworkflow.company.entity.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, UUID> {

    Optional<CompanyProfile> findByProfileCodeAndIsActiveTrueAndIsDeletedFalse(String profileCode);
}