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

    public record EstimateTaxCalculationContext(
            EstimateTaxModel taxModel,
            BigDecimal taxRatePercent) {
    }

    public EstimateTaxCalculationContext createTaxCalculationContext(
            BidRevisionItem item) {

        EstimateTaxModel taxModel = estimateTaxModelResolver.resolveTaxModel(
                item.getBidRevision()
                        .getBid()
                        .getDepartmentCode(),
                item.getBidRevision()
                        .getBid()
                        .getConstructionType());

        return new EstimateTaxCalculationContext(
                taxModel,
                nvl(item.getTaxRateSnapshotPercent()));
    }

    /**
     * Compatibility entry point used by the existing BidService.
     *
     * The Cost inherits its tax model and tax-rate percentage from its
     * parent Item.
     */
    public void recalculateItemCostTotals(BidRevisionItemCost cost) {
        EstimateTaxCalculationContext taxContext =
                createTaxCalculationContext(cost.getBidRevisionItem());

        recalculateItemCostTotals(cost, taxContext);
    }

    /**
     * Calculates the Cost row's own pricing and tax-derived fields.
     */
    public void recalculateItemCostTotals(
            BidRevisionItemCost cost,
            EstimateTaxCalculationContext taxContext) {

        BigDecimal quantity = nvl(cost.getQuantity());
        BigDecimal unitCost = nvl(cost.getUnitCost());

        BigDecimal markupPercent = cost.getMarkupPercent();

        if (markupPercent == null && cost.getUnitPrice() != null) {
            markupPercent = deriveMarkupPercent(
                    unitCost,
                    cost.getUnitPrice());

            cost.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            cost.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculateUnitPrice(
                unitCost,
                markupPercent);

        BigDecimal totalCost = quantity.multiply(unitCost);
        BigDecimal totalPrice = quantity.multiply(unitPrice);

        BigDecimal taxAmount = calculateCostTax(
                cost,
                totalPrice,
                taxContext);

        cost.setUnitPrice(money(unitPrice));
        cost.setTotalCost(money(totalCost));
        cost.setTotalPrice(money(totalPrice));
        cost.setGpmPercent(
                deriveGpmPercent(totalCost, totalPrice));

        cost.setTaxAmount(money(taxAmount));
        cost.setPriceWithTax(
                money(totalPrice.add(taxAmount)));
    }

    /**
     * Compatibility entry point used by the existing BidService.
     */
    public void recalculateItemTotals(BidRevisionItem item) {
        recalculateItemAggregateTotals(item);
    }

    /**
     * Calculates the Item's material tax and aggregates all active child Cost
     * pricing and tax values.
     *
     * Item totalCost and totalPrice remain material-only values.
     * Item taxAmount and priceWithTax are aggregate values.
     */
    public void recalculateItemAggregateTotals(BidRevisionItem item) {

        List<BidRevisionItemCost> costs =
                findActiveItemCosts(item);

        BigDecimal costRowsTotalCost = BigDecimal.ZERO;
        BigDecimal costRowsTotalPrice = BigDecimal.ZERO;
        BigDecimal costRowsTaxAmount = BigDecimal.ZERO;

        for (BidRevisionItemCost cost : costs) {
            costRowsTotalCost = costRowsTotalCost.add(
                    nvl(cost.getTotalCost()));

            costRowsTotalPrice = costRowsTotalPrice.add(
                    nvl(cost.getTotalPrice()));

            costRowsTaxAmount = costRowsTaxAmount.add(
                    nvl(cost.getTaxAmount()));
        }

        BigDecimal materialTotalCost =
                nvl(item.getTotalCost());

        BigDecimal materialTotalPrice =
                nvl(item.getTotalPrice());

        BigDecimal aggregateTotalCost =
                materialTotalCost.add(costRowsTotalCost);

        BigDecimal aggregateTotalPrice =
                materialTotalPrice.add(costRowsTotalPrice);

        EstimateTaxCalculationContext taxContext =
                createTaxCalculationContext(item);

        BigDecimal materialTaxAmount = calculateItemMaterialTax(
                item,
                materialTotalCost,
                materialTotalPrice,
                taxContext);

        BigDecimal aggregateTaxAmount =
                materialTaxAmount.add(costRowsTaxAmount);

        item.setGpmPercent(
                deriveGpmPercent(
                        aggregateTotalCost,
                        aggregateTotalPrice));

        item.setTaxAmount(
                money(aggregateTaxAmount));

        item.setPriceWithTax(
                money(aggregateTotalPrice.add(aggregateTaxAmount)));
    }

    /**
     * Aggregates material Item values and all active child Cost values into
     * revision totals.
     */
    public void recalculateRevisionTotals(BidRevision revision) {

        List<BidRevisionItem> items =
                bidRevisionItemRepository
                        .findByBidRevision_BidRevisionIdAndIsDeletedFalse(
                                revision.getBidRevisionId());

        BigDecimal subtotalCost = BigDecimal.ZERO;
        BigDecimal subtotalPrice = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (BidRevisionItem item : items) {

            subtotalCost = subtotalCost.add(
                    nvl(item.getTotalCost()));

            subtotalPrice = subtotalPrice.add(
                    nvl(item.getTotalPrice()));

            List<BidRevisionItemCost> costs =
                    findActiveItemCosts(item);

            for (BidRevisionItemCost cost : costs) {
                subtotalCost = subtotalCost.add(
                        nvl(cost.getTotalCost()));

                subtotalPrice = subtotalPrice.add(
                        nvl(cost.getTotalPrice()));
            }

            taxAmount = taxAmount.add(
                    nvl(item.getTaxAmount()));

            totalPrice = totalPrice.add(
                    nvl(item.getPriceWithTax()));
        }

        revision.setSubtotalCost(money(subtotalCost));
        revision.setSubtotalPrice(money(subtotalPrice));
        revision.setTaxAmount(money(taxAmount));
        revision.setTotalPrice(money(totalPrice));
    }

    /**
     * Calculates material-only pricing fields.
     */
    public void recalculateItemMaterialTotals(BidRevisionItem item) {

        BigDecimal quantity = nvl(item.getQuantity());
        BigDecimal unitCost = nvl(item.getUnitCost());

        BigDecimal markupPercent = item.getMarkupPercent();

        if (markupPercent == null && item.getUnitPrice() != null) {
            markupPercent = deriveMarkupPercent(
                    unitCost,
                    item.getUnitPrice());

            item.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            item.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculateUnitPrice(
                unitCost,
                markupPercent);

        BigDecimal totalCost =
                quantity.multiply(unitCost);

        BigDecimal totalPrice =
                quantity.multiply(unitPrice);

        item.setUnitPrice(money(unitPrice));
        item.setTotalCost(money(totalCost));
        item.setTotalPrice(money(totalPrice));
    }

    private BigDecimal calculateItemMaterialTax(
            BidRevisionItem item,
            BigDecimal materialTotalCost,
            BigDecimal materialTotalPrice,
            EstimateTaxCalculationContext taxContext) {

        if (!Boolean.TRUE.equals(item.getIsTaxable())) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxBase = switch (taxContext.taxModel()) {
            case MATERIAL_COST_ONLY -> materialTotalCost;
            case ALL_SELL_PRICE -> materialTotalPrice;
        };

        return calculateTax(
                taxBase,
                taxContext.taxRatePercent());
    }

    private BigDecimal calculateCostTax(
            BidRevisionItemCost cost,
            BigDecimal totalPrice,
            EstimateTaxCalculationContext taxContext) {

        if (!Boolean.TRUE.equals(cost.getIsTaxable())) {
            return BigDecimal.ZERO;
        }

        return switch (taxContext.taxModel()) {
            case MATERIAL_COST_ONLY -> BigDecimal.ZERO;
            case ALL_SELL_PRICE -> calculateTax(
                    totalPrice,
                    taxContext.taxRatePercent());
        };
    }

    private BigDecimal calculateTax(
            BigDecimal taxBase,
            BigDecimal taxRatePercent) {

        return nvl(taxBase)
                .multiply(nvl(taxRatePercent))
                .divide(
                        ONE_HUNDRED,
                        SCALE,
                        RoundingMode.HALF_UP);
    }

    private List<BidRevisionItemCost> findActiveItemCosts(
            BidRevisionItem item) {

        return bidRevisionItemCostRepository
                .findByBidRevisionItem_BidRevisionItemIdAndIsDeletedFalseOrderByDisplayOrderAsc(
                        item.getBidRevisionItemId());
    }

    private BigDecimal calculateUnitPrice(
            BigDecimal unitCost,
            BigDecimal markupPercent) {

        BigDecimal multiplier = BigDecimal.ONE.add(
                markupPercent.divide(
                        ONE_HUNDRED,
                        SCALE,
                        RoundingMode.HALF_UP));

        return unitCost.multiply(multiplier);
    }

    private BigDecimal deriveMarkupPercent(
            BigDecimal cost,
            BigDecimal price) {

        if (cost == null
                || cost.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return price.subtract(cost)
                .divide(
                        cost,
                        SCALE,
                        RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(
                        SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal deriveGpmPercent(
            BigDecimal cost,
            BigDecimal price) {

        if (price == null
                || price.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return price.subtract(cost)
                .divide(
                        price,
                        SCALE,
                        RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(
                        SCALE,
                        RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal value) {
        return nvl(value)
                .setScale(
                        SCALE,
                        RoundingMode.HALF_UP);
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value != null
                ? value
                : BigDecimal.ZERO;
    }
}