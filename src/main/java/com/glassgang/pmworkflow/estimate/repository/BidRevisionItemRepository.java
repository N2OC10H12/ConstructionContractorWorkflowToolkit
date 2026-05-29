package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRevisionItemRepository extends JpaRepository<BidRevisionItem, UUID> {

    @EntityGraph(attributePaths = { "itemType", "taxRate" })
    List<BidRevisionItem> findByBidRevision_BidRevisionIdAndIsDeletedFalseOrderByDisplayOrderAsc(
            UUID bidRevisionId);

    @EntityGraph(attributePaths = { "itemType", "taxRate" })
    List<BidRevisionItem> findByBidRevision_BidRevisionIdAndIsDeletedFalse(
            UUID bidRevisionId);

    Integer countByBidRevision_BidRevisionIdAndIsDeletedFalse(
            UUID bidRevisionId);

    Optional<BidRevisionItem> findByBidRevisionItemIdAndIsDeletedFalse(
            UUID bidRevisionItemId);

    @Query("""
                select max(i.lineNumber)
                from BidRevisionItem i
                where i.bidRevision.bidRevisionId = :bidRevisionId
            """)
    Optional<Integer> findTopLineNumberByBidRevisionId(
            @Param("bidRevisionId") UUID bidRevisionId);

    @Query("""
                select max(i.displayOrder)
                from BidRevisionItem i
                where i.bidRevision.bidRevisionId = :bidRevisionId
            """)
    Optional<Integer> findTopDisplayOrderByBidRevisionId(
            @Param("bidRevisionId") UUID bidRevisionId);

    boolean existsByItemType_ItemTypeIdAndIsDeletedFalse(UUID itemTypeId);

    boolean existsByTaxRate_TaxRateIdAndIsDeletedFalse(UUID taxRateId);
}