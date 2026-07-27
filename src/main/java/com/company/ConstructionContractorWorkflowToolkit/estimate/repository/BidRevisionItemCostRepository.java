package com.company.ConstructionContractorWorkflowToolkit.estimate.repository;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItemCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BidRevisionItemCostRepository extends JpaRepository<BidRevisionItemCost, UUID> {

        List<BidRevisionItemCost> findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        UUID bidRevisionItemId);

        Integer countByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalse(
                        UUID bidRevisionItemId);

        Optional<BidRevisionItemCost> findByBidRevisionItemCostIdAndIsDeletedFalse(
                        UUID bidRevisionItemCostId);

        List<BidRevisionItemCost> findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalse(
                        UUID bidRevisionItemId);

        @Query("""
                        select max(c.lineNumber)
                        from BidRevisionItemCost c
                        where c.bidRevisionItem.bidRevisionItemId = :bidRevisionItemId
                        """)
        Optional<Integer> findTopLineNumberByBidRevisionItemId(
                        @Param("bidRevisionItemId") UUID bidRevisionItemId);

        @Query("""
                        select max(c.displayOrder)
                        from BidRevisionItemCost c
                        where c.bidRevisionItem.bidRevisionItemId = :bidRevisionItemId
                        """)
        Optional<Integer> findTopDisplayOrderByBidRevisionItemId(
                        @Param("bidRevisionItemId") UUID bidRevisionItemId);

        boolean existsByCostElement_CostElementIdAndIsDeletedFalse(UUID costElementId);

        boolean existsByCostRate_CostRateIdAndIsDeletedFalse(UUID costRateId);
}