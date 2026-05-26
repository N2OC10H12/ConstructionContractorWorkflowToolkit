package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByCustomerIdAndIsDeletedFalse(UUID customerId);
}