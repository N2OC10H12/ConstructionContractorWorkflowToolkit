package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItemQuote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRevisionItemQuoteRepository
        extends JpaRepository<BidRevisionItemQuote, UUID> {

    @EntityGraph(attributePaths = {
            "storedFile",
            "bidRevisionItem",
            "bidRevisionItem.bidRevision",
            "bidRevisionItem.bidRevision.bid"
    })
    Optional<BidRevisionItemQuote> findByBidRevisionItemQuoteIdAndBidRevisionItem_IsDeletedFalse(
            UUID bidRevisionItemQuoteId);

    @EntityGraph(attributePaths = {
            "storedFile"
    })
    List<BidRevisionItemQuote> findByBidRevisionItem_BidRevisionItemIdOrderByDisplayOrderAsc(
            UUID bidRevisionItemId);

    @EntityGraph(attributePaths = {
            "storedFile",
            "bidRevisionItem"
    })
    List<BidRevisionItemQuote> findByBidRevisionItem_BidRevisionItemIdIn(
            Collection<UUID> bidRevisionItemIds);

    @Query("""
            select max(q.displayOrder)
            from BidRevisionItemQuote q
            where q.bidRevisionItem.bidRevisionItemId = :bidRevisionItemId
            """)
    Optional<Integer> findTopDisplayOrderByBidRevisionItemId(
            @Param("bidRevisionItemId") UUID bidRevisionItemId);

    long countByStoredFile_StoredFileId(
            UUID storedFileId);
}