package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRevisionItemRepository
        extends JpaRepository<BidRevisionItem, UUID> {

    @EntityGraph(attributePaths = {
            "companyWorkType",
            "taxRate"
    })
    List<BidRevisionItem>
    findByBidRevision_BidRevisionIdAndIsDeletedFalseOrderByDisplayOrderAsc(
            UUID bidRevisionId);

    @EntityGraph(attributePaths = {
            "companyWorkType",
            "taxRate"
    })
    List<BidRevisionItem>
    findByBidRevision_BidRevisionIdAndIsDeletedFalse(
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

    boolean existsByTaxRate_TaxRateIdAndIsDeletedFalse(
            UUID taxRateId);

    @EntityGraph(attributePaths = {
            "companyWorkType",
            "taxRate"
    })
    List<BidRevisionItem>
    findByBidRevision_BidRevisionIdAndGroupNameAndIsDeletedFalse(
            UUID bidRevisionId,
            String groupName);

    boolean existsByCompanyWorkType_CompanyWorkTypeIdAndIsDeletedFalse(
            UUID companyWorkTypeId);

    @EntityGraph(attributePaths = {
            "companyWorkType",
            "taxRate"
    })
    List<BidRevisionItem>
    findByBidRevision_BidRevisionIdAndGroupNameAndCompanyWorkType_CompanyWorkTypeIdAndIsDeletedFalse(
            UUID bidRevisionId,
            String groupName,
            UUID companyWorkTypeId);
}