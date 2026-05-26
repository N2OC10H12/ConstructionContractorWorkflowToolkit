package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerContactRepository
        extends JpaRepository<CustomerContact, UUID> {
}