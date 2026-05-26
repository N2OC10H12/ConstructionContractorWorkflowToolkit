package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, UUID> {
}