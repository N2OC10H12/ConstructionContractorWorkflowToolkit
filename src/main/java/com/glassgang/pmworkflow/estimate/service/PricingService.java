package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemCostRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {

    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final BidRevisionItemRepository bidRevisionItemRepository;

    public PricingService(
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            BidRevisionItemRepository bidRevisionItemRepository) {
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
    }

    public void recalculateItemCostTotals(BidRevisionItemCost cost) {
        BigDecimal totalCost = cost.getQuantity().multiply(cost.getUnitCost());
        BigDecimal totalPrice = cost.getQuantity().multiply(cost.getUnitPrice());

        cost.setTotalCost(totalCost);
        cost.setTotalPrice(totalPrice);

        // Legacy compatibility only.
        // Cost-level tax is no longer part of active pricing.
        cost.setTaxAmount(BigDecimal.ZERO);
        cost.setPriceWithTax(totalPrice);
    }

    public void recalculateItemTotals(BidRevisionItem item) {
        List<BidRevisionItemCost> costs = bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        item.getBidRevisionItemId());

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (BidRevisionItemCost cost : costs) {
            totalCost = totalCost.add(cost.getTotalCost());
            totalPrice = totalPrice.add(cost.getTotalPrice());
        }

        BigDecimal taxRatePercent = item.getTaxRateSnapshotPercent() != null
                ? item.getTaxRateSnapshotPercent()
                : BigDecimal.ZERO;

        BigDecimal taxAmount = totalPrice
                .multiply(taxRatePercent)
                .divide(new BigDecimal("100.0000"), 4, RoundingMode.HALF_UP);

        item.setUnitCost(totalCost);
        item.setUnitPrice(totalPrice);
        item.setTotalCost(totalCost);
        item.setTotalPrice(totalPrice);
        item.setTaxAmount(taxAmount);
        item.setPriceWithTax(totalPrice.add(taxAmount));
    }

    public void recalculateRevisionTotals(BidRevision revision) {
        List<BidRevisionItem> items = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndIsDeletedFalse(
                        revision.getBidRevisionId());

        BigDecimal subtotalCost = BigDecimal.ZERO;
        BigDecimal subtotalPrice = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (BidRevisionItem item : items) {
            subtotalCost = subtotalCost.add(item.getTotalCost());
            subtotalPrice = subtotalPrice.add(item.getTotalPrice());
            taxAmount = taxAmount.add(item.getTaxAmount());
        }

        revision.setSubtotalCost(subtotalCost);
        revision.setSubtotalPrice(subtotalPrice);
        revision.setTaxAmount(taxAmount);
        revision.setTotalPrice(subtotalPrice.add(taxAmount));
    }
}