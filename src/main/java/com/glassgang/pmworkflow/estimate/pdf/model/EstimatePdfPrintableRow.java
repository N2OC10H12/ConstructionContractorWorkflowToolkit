package com.glassgang.pmworkflow.estimate.pdf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class EstimatePdfPrintableRow {

    private EstimatePdfPrintableRowType rowType;

    /*
     * True for repeated hierarchy rows inserted only to provide context
     * inside the final carry block.
     *
     * Continuation-context rows must not count toward the required
     * customer-visible detail-row minimum.
     */
    private boolean continuationContext;

    /*
     * Ancestor and source-row references.
     *
     * GROUP:
     * group
     *
     * ITEM_TYPE:
     * group, itemType
     *
     * ITEM:
     * group, itemType, item
     *
     * COST:
     * group, itemType, item, cost
     */
    private EstimatePdfGroup group;
    private EstimatePdfItemTypeGroup itemType;
    private EstimatePdfItemLine item;
    private EstimatePdfItemCostLine cost;

    public boolean isGroupRow() {
        return rowType == EstimatePdfPrintableRowType.GROUP;
    }

    public boolean isItemTypeRow() {
        return rowType == EstimatePdfPrintableRowType.ITEM_TYPE;
    }

    public boolean isItemRow() {
        return rowType == EstimatePdfPrintableRowType.ITEM;
    }

    public boolean isCostRow() {
        return rowType == EstimatePdfPrintableRowType.COST;
    }

    public static EstimatePdfPrintableRow forGroup(
            EstimatePdfGroup group) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.GROUP);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        return row;
    }

    public static EstimatePdfPrintableRow forItemType(
            EstimatePdfGroup group,
            EstimatePdfItemTypeGroup itemType) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.ITEM_TYPE);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setItemType(
                Objects.requireNonNull(itemType, "itemType is required"));
        return row;
    }

    public static EstimatePdfPrintableRow forItem(
            EstimatePdfGroup group,
            EstimatePdfItemTypeGroup itemType,
            EstimatePdfItemLine item) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.ITEM);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setItemType(
                Objects.requireNonNull(itemType, "itemType is required"));
        row.setItem(Objects.requireNonNull(item, "item is required"));
        return row;
    }

    public static EstimatePdfPrintableRow forCost(
            EstimatePdfGroup group,
            EstimatePdfItemTypeGroup itemType,
            EstimatePdfItemLine item,
            EstimatePdfItemCostLine cost) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.COST);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setItemType(
                Objects.requireNonNull(itemType, "itemType is required"));
        row.setItem(Objects.requireNonNull(item, "item is required"));
        row.setCost(Objects.requireNonNull(cost, "cost is required"));
        return row;
    }

    public EstimatePdfPrintableRow copyAsContinuationContext() {
        EstimatePdfPrintableRow copy = new EstimatePdfPrintableRow();

        copy.setRowType(rowType);
        copy.setContinuationContext(true);
        copy.setGroup(group);
        copy.setItemType(itemType);
        copy.setItem(item);
        copy.setCost(cost);

        return copy;
    }
}