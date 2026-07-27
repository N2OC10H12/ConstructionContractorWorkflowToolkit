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
     * WORK_TYPE:
     * group, workType
     *
     * ITEM:
     * group, workType, item
     *
     * COST:
     * group, workType, item, cost
     */
    private EstimatePdfGroup group;
    private EstimatePdfWorkTypeGroup workType;
    private EstimatePdfItemLine item;
    private EstimatePdfItemCostLine cost;

    public boolean isGroupRow() {
        return rowType == EstimatePdfPrintableRowType.GROUP;
    }

    public boolean isWorkTypeRow() {
        return rowType == EstimatePdfPrintableRowType.WORK_TYPE;
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

    public static EstimatePdfPrintableRow forWorkType(
            EstimatePdfGroup group,
            EstimatePdfWorkTypeGroup workType) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.WORK_TYPE);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setWorkType(
                Objects.requireNonNull(workType, "workType is required"));
        return row;
    }

    public static EstimatePdfPrintableRow forItem(
            EstimatePdfGroup group,
            EstimatePdfWorkTypeGroup workType,
            EstimatePdfItemLine item) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.ITEM);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setWorkType(
                Objects.requireNonNull(workType, "workType is required"));
        row.setItem(Objects.requireNonNull(item, "item is required"));
        return row;
    }

    public static EstimatePdfPrintableRow forCost(
            EstimatePdfGroup group,
            EstimatePdfWorkTypeGroup workType,
            EstimatePdfItemLine item,
            EstimatePdfItemCostLine cost) {
        EstimatePdfPrintableRow row = new EstimatePdfPrintableRow();
        row.setRowType(EstimatePdfPrintableRowType.COST);
        row.setGroup(Objects.requireNonNull(group, "group is required"));
        row.setWorkType(
                Objects.requireNonNull(workType, "workType is required"));
        row.setItem(Objects.requireNonNull(item, "item is required"));
        row.setCost(Objects.requireNonNull(cost, "cost is required"));
        return row;
    }

    public EstimatePdfPrintableRow copyAsContinuationContext() {
        EstimatePdfPrintableRow copy = new EstimatePdfPrintableRow();

        copy.setRowType(rowType);
        copy.setContinuationContext(true);
        copy.setGroup(group);
        copy.setWorkType(workType);
        copy.setItem(item);
        copy.setCost(cost);

        return copy;
    }
}