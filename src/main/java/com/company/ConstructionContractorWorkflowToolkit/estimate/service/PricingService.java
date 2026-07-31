package com.company.ConstructionContractorWorkflowToolkit.estimate.service;

import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevision;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItem;
import com.company.ConstructionContractorWorkflowToolkit.estimate.entity.BidRevisionItemCost;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.BidRoundingMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimateTaxModel;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemCostRepository;
import com.company.ConstructionContractorWorkflowToolkit.estimate.repository.BidRevisionItemRepository;
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
    private final EstimateRoundingPolicy estimateRoundingPolicy;

    public PricingService(
            BidRevisionItemCostRepository bidRevisionItemCostRepository,
            BidRevisionItemRepository bidRevisionItemRepository,
            EstimateTaxModelResolver estimateTaxModelResolver,
            EstimateRoundingPolicy estimateRoundingPolicy) {
        this.bidRevisionItemCostRepository = bidRevisionItemCostRepository;
        this.bidRevisionItemRepository = bidRevisionItemRepository;
        this.estimateTaxModelResolver = estimateTaxModelResolver;
        this.estimateRoundingPolicy = estimateRoundingPolicy;
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

        BidRoundingMode roundingMode =
                resolveRoundingMode(cost);

        BigDecimal quantity = normalizeInput(
                cost.getQuantity(),
                roundingMode);

        BigDecimal unitCost = normalizeInput(
                cost.getUnitCost(),
                roundingMode);

        cost.setQuantity(quantity);
        cost.setUnitCost(unitCost);

        if (cost.getRateSnapshot() != null) {
            cost.setRateSnapshot(
                    normalizeInput(
                            cost.getRateSnapshot(),
                            roundingMode));
        }

        BigDecimal markupPercent = cost.getMarkupPercent();

        if (markupPercent == null && cost.getUnitPrice() != null) {
            BigDecimal enteredUnitPrice = normalizeInput(
                    cost.getUnitPrice(),
                    roundingMode);

            cost.setUnitPrice(enteredUnitPrice);

            markupPercent = deriveMarkupPercent(
                    unitCost,
                    enteredUnitPrice);

            cost.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            cost.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculatedValue(
                calculateUnitPrice(
                        unitCost,
                        markupPercent),
                roundingMode);

        BigDecimal totalCost = calculatedValue(
                quantity.multiply(unitCost),
                roundingMode);

        BigDecimal totalPrice = calculatedValue(
                quantity.multiply(unitPrice),
                roundingMode);

        BigDecimal taxAmount = calculateCostTax(
                cost,
                totalPrice,
                taxContext,
                roundingMode);

        cost.setUnitPrice(unitPrice);
        cost.setTotalCost(totalCost);
        cost.setTotalPrice(totalPrice);
        cost.setGpmPercent(
                deriveGpmPercent(totalCost, totalPrice));

        cost.setTaxAmount(
                calculatedValue(
                        taxAmount,
                        roundingMode));

        cost.setPriceWithTax(
                calculatedValue(
                        totalPrice.add(taxAmount),
                        roundingMode));
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

        BidRoundingMode roundingMode =
                resolveRoundingMode(item);

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
                calculatedValue(
                        materialTotalCost.add(costRowsTotalCost),
                        roundingMode);

        BigDecimal aggregateTotalPrice =
                calculatedValue(
                        materialTotalPrice.add(costRowsTotalPrice),
                        roundingMode);

        EstimateTaxCalculationContext taxContext =
                createTaxCalculationContext(item);

        BigDecimal materialTaxAmount = calculateItemMaterialTax(
                item,
                materialTotalCost,
                materialTotalPrice,
                taxContext,
                roundingMode);

        BigDecimal aggregateTaxAmount =
                calculatedValue(
                        materialTaxAmount.add(costRowsTaxAmount),
                        roundingMode);

        item.setGpmPercent(
                deriveGpmPercent(
                        aggregateTotalCost,
                        aggregateTotalPrice));

        item.setTaxAmount(aggregateTaxAmount);

        item.setPriceWithTax(
                calculatedValue(
                        aggregateTotalPrice.add(aggregateTaxAmount),
                        roundingMode));
    }

    /**
     * Aggregates material Item values and all active child Cost values into
     * revision totals.
     */
    public void recalculateRevisionTotals(BidRevision revision) {

        BidRoundingMode roundingMode =
                resolveRoundingMode(revision);

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

        revision.setSubtotalCost(
                calculatedValue(
                        subtotalCost,
                        roundingMode));

        revision.setSubtotalPrice(
                calculatedValue(
                        subtotalPrice,
                        roundingMode));

        revision.setTaxAmount(
                calculatedValue(
                        taxAmount,
                        roundingMode));

        revision.setTotalPrice(
                calculatedValue(
                        totalPrice,
                        roundingMode));
    }

    /**
     * Calculates material-only pricing fields.
     */
    public void recalculateItemMaterialTotals(BidRevisionItem item) {

        BidRoundingMode roundingMode =
                resolveRoundingMode(item);

        BigDecimal quantity = normalizeInput(
                item.getQuantity(),
                roundingMode);

        BigDecimal unitCost = normalizeInput(
                item.getUnitCost(),
                roundingMode);

        item.setQuantity(quantity);
        item.setUnitCost(unitCost);

        BigDecimal markupPercent = item.getMarkupPercent();

        if (markupPercent == null && item.getUnitPrice() != null) {
            BigDecimal enteredUnitPrice = normalizeInput(
                    item.getUnitPrice(),
                    roundingMode);

            item.setUnitPrice(enteredUnitPrice);

            markupPercent = deriveMarkupPercent(
                    unitCost,
                    enteredUnitPrice);

            item.setMarkupPercent(markupPercent);
        }

        if (markupPercent == null) {
            markupPercent = BigDecimal.ZERO;
            item.setMarkupPercent(markupPercent);
        }

        BigDecimal unitPrice = calculatedValue(
                calculateUnitPrice(
                        unitCost,
                        markupPercent),
                roundingMode);

        BigDecimal totalCost =
                calculatedValue(
                        quantity.multiply(unitCost),
                        roundingMode);

        BigDecimal totalPrice =
                calculatedValue(
                        quantity.multiply(unitPrice),
                        roundingMode);

        item.setUnitPrice(unitPrice);
        item.setTotalCost(totalCost);
        item.setTotalPrice(totalPrice);
    }

    private BigDecimal calculateItemMaterialTax(
            BidRevisionItem item,
            BigDecimal materialTotalCost,
            BigDecimal materialTotalPrice,
            EstimateTaxCalculationContext taxContext,
            BidRoundingMode roundingMode) {

        if (!Boolean.TRUE.equals(item.getIsTaxable())) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxBase = switch (taxContext.taxModel()) {
            case MATERIAL_COST_ONLY -> materialTotalCost;
            case ALL_SELL_PRICE -> materialTotalPrice;
        };

        return calculateTax(
                taxBase,
                taxContext.taxRatePercent(),
                roundingMode);
    }

    private BigDecimal calculateCostTax(
            BidRevisionItemCost cost,
            BigDecimal totalPrice,
            EstimateTaxCalculationContext taxContext,
            BidRoundingMode roundingMode) {

        if (!Boolean.TRUE.equals(cost.getIsTaxable())) {
            return BigDecimal.ZERO;
        }

        return switch (taxContext.taxModel()) {
            case MATERIAL_COST_ONLY -> BigDecimal.ZERO;
            case ALL_SELL_PRICE -> calculateTax(
                    totalPrice,
                    taxContext.taxRatePercent(),
                    roundingMode);
        };
    }

    private BigDecimal calculateTax(
            BigDecimal taxBase,
            BigDecimal taxRatePercent,
            BidRoundingMode roundingMode) {

        BigDecimal rawTax = nvl(taxBase)
                .multiply(nvl(taxRatePercent))
                .movePointLeft(2);

        return calculatedValue(
                rawTax,
                roundingMode);
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

    private BigDecimal normalizeInput(
            BigDecimal value,
            BidRoundingMode roundingMode) {

        return nvl(
                estimateRoundingPolicy.normalizeInput(
                        nvl(value),
                        roundingMode));
    }

    private BigDecimal calculatedValue(
            BigDecimal value,
            BidRoundingMode roundingMode) {

        return estimateRoundingPolicy.roundCalculated(
                value,
                roundingMode);
    }

    private BidRoundingMode resolveRoundingMode(
            BidRevisionItemCost cost) {

        if (cost == null
                || cost.getBidRevisionItem() == null) {
            return BidRoundingMode.WHOLE;
        }

        return resolveRoundingMode(
                cost.getBidRevisionItem());
    }

    private BidRoundingMode resolveRoundingMode(
            BidRevisionItem item) {

        if (item == null
                || item.getBidRevision() == null) {
            return BidRoundingMode.WHOLE;
        }

        return resolveRoundingMode(
                item.getBidRevision());
    }

    private BidRoundingMode resolveRoundingMode(
            BidRevision revision) {

        if (revision == null
                || revision.getBid() == null
                || revision.getBid().getRoundingMode() == null) {
            return BidRoundingMode.WHOLE;
        }

        return revision.getBid()
                .getRoundingMode();
    }

    private static BigDecimal nvl(BigDecimal value) {
        return value != null
                ? value
                : BigDecimal.ZERO;
    }
}
