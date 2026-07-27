package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRevisionRepository extends JpaRepository<BidRevision, UUID> {

    List<BidRevision> findByBid_BidIdAndIsDeletedFalseOrderByRevisionNumberAsc(UUID bidId);

    Optional<BidRevision> findByRevisionDisplayNameAndIsDeletedFalse(String revisionDisplayName);

    Optional<BidRevision> findByBidRevisionIdAndIsDeletedFalse(UUID bidRevisionId);
}