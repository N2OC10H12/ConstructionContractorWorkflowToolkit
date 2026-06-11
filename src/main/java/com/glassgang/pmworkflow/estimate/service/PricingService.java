package com.glassgang.pmworkflow.estimate.service;

import com.glassgang.pmworkflow.estimate.entity.BidRevision;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItem;
import com.glassgang.pmworkflow.estimate.entity.BidRevisionItemCost;
import com.glassgang.pmworkflow.estimate.enums.EstimateTaxModel;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemCostRepository;
import com.glassgang.pmworkflow.estimate.repository.BidRevisionItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {

    private static final int SCALE = 4;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.0000");

    private final BidRevisionItemCostRepository bidRevisionItemCostRepository;
    private final BidRevisionItemRepository bidRevisionItemRepository;
    private final EstimateTaxModelResolver estimateTaxModelResolver;

    public PricingService(
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            EstimateTaxModelResolver estimateTaxModelResolver) {
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.estimateTaxModelResolver = estimateTaxModelResolver;
    }

    public void recalculateItemCostTotals(BidRevisionItemCost cost) {
        BigDecimal quantity = nvl(cost.getQuantity());
        BigDecimal unitCost = nvl(cost.getUnitCost());

        BigDecimal markupPercent = cost.getMarkupPercent();

        if (markupPercent == null && cost.getUnitPrice() != null) {
            markupPercent = deriveMarkupPercent(unitCost, cost.getUnitPrice());
            cost.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            cost.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculateUnitPrice(unitCost, markupPercent);
        BigDecimal totalCost = quantity.multiply(unitCost);
        BigDecimal totalPrice = quantity.multiply(unitPrice);

        cost.setUnitPrice(money(unitPrice));
        cost.setTotalCost(money(totalCost));
        cost.setTotalPrice(money(totalPrice));
        cost.setGpmPercent(deriveGpmPercent(totalCost, totalPrice));

        cost.setTaxAmount(BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP));
        cost.setPriceWithTax(money(totalPrice));
    }

    public void recalculateItemTotals(BidRevisionItem item) {
        List<BidRevisionItemCost> costs = bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        item.getBidRevisionItemId());

        BigDecimal extraCostTotal = BigDecimal.ZERO;
        BigDecimal extraPriceTotal = BigDecimal.ZERO;

        for (BidRevisionItemCost cost : costs) {
            extraCostTotal = extraCostTotal.add(nvl(cost.getTotalCost()));
            extraPriceTotal = extraPriceTotal.add(nvl(cost.getTotalPrice()));
        }

        BigDecimal itemMaterialCost = nvl(item.getTotalCost());
        BigDecimal itemMaterialPrice = nvl(item.getTotalPrice());

        BigDecimal combinedCost = itemMaterialCost.add(extraCostTotal);
        BigDecimal combinedPrice = itemMaterialPrice.add(extraPriceTotal);

        BigDecimal taxRatePercent = nvl(item.getTaxRateSnapshotPercent());

        EstimateTaxModel taxModel = estimateTaxModelResolver.resolveTaxModel(
                item.getBidRevision().getBid().getDepartmentCode(),
                item.getBidRevision().getBid().getConstructionType());

        BigDecimal taxBase;

        if (taxModel == EstimateTaxModel.MATERIAL_COST_ONLY) {
            taxBase = itemMaterialCost;
        } else if (taxModel == EstimateTaxModel.ALL_SELL_PRICE) {
            taxBase = combinedPrice;
        } else {
            taxBase = BigDecimal.ZERO;
        }

        BigDecimal taxAmount = taxBase
                .multiply(taxRatePercent)
                .divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP);

        //item.setMarkupPercent(deriveMarkupPercent(combinedCost, combinedPrice)); //================================================markup override
        item.setGpmPercent(deriveGpmPercent(combinedCost, combinedPrice));
        item.setTaxAmount(money(taxAmount));
        item.setPriceWithTax(money(combinedPrice.add(taxAmount)));
    }

    public void recalculateRevisionTotals(BidRevision revision) {
        List<BidRevisionItem> items = bidRevisionItemRepository
                .findByBidRevision_BidRevisionIdAndIsDeletedFalse(
                        revision.getBidRevisionId());

        BigDecimal subtotalCost = BigDecimal.ZERO;
        BigDecimal subtotalPrice = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (BidRevisionItem item : items) {
            subtotalCost = subtotalCost.add(nvl(item.getTotalCost()));
            subtotalPrice = subtotalPrice.add(nvl(item.getTotalPrice()));
            taxAmount = taxAmount.add(nvl(item.getTaxAmount()));
            totalPrice = totalPrice.add(nvl(item.getPriceWithTax()));
        }

        revision.setSubtotalCost(money(subtotalCost));
        revision.setSubtotalPrice(money(subtotalPrice));
        revision.setTaxAmount(money(taxAmount));
        revision.setTotalPrice(money(totalPrice));
    }

    public void recalculateItemMaterialTotals(BidRevisionItem item) {
        BigDecimal quantity = nvl(item.getQuantity());
        BigDecimal unitCost = nvl(item.getUnitCost());

        BigDecimal markupPercent = item.getMarkupPercent();

        if (markupPercent == null && item.getUnitPrice() != null) {
            markupPercent = deriveMarkupPercent(unitCost, item.getUnitPrice());
            item.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            item.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculateUnitPrice(unitCost, markupPercent);
        BigDecimal totalCost = quantity.multiply(unitCost);
        BigDecimal totalPrice = quantity.multiply(unitPrice);

        item.setUnitPrice(money(unitPrice));
        item.setTotalCost(money(totalCost));
        item.setTotalPrice(money(totalPrice));
    }

    private BigDecimal calculateUnitPrice(BigDecimal unitCost, BigDecimal markupPercent) {
        BigDecimal multiplier = BigDecimal.ONE.add(
                markupPercent.divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP));

        return unitCost.multiply(multiplier);
    }

    private BigDecimal deriveMarkupPercent(BigDecimal cost, BigDecimal price) {
        if (cost == null || cost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return price.subtract(cost)
                .divide(cost, SCALE, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal deriveGpmPercent(BigDecimal cost, BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return price.subtract(cost)
                .divide(price, SCALE, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal value) {
        return nvl(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}