package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    Optional<Bid> findByBidNumberAndIsDeletedFalse(String bidNumber);

    Optional<Bid> findByJobNumberAndIsDeletedFalse(String jobNumber);

    Optional<Bid> findByBidIdAndIsDeletedFalse(UUID bidId);
}