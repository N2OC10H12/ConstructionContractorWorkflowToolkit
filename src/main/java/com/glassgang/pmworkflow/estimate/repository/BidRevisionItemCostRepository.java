package com.glassgang.pmworkflow.estimate.repository;

import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import org.springframework.data.jpa.repository.JpaRepository;

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
}