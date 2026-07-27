package com.glassgang.pmworkflow.estimate.pdf;

import com.glassgang.pmworkflow.estimate.enums.CustomerDisplayMode;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfGroup;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfItemLine;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfWorkTypeGroup;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfModel;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfPrintableRow;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfPrintableRowPartition;
import com.glassgang.pmworkflow.estimate.pdf.model.EstimatePdfPrintableRowType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class EstimatePdfPrintableRowPartitionService {

        private static final int MINIMUM_FINAL_DETAIL_ROWS = 3;

        public EstimatePdfPrintableRowPartition partition(
                        EstimatePdfModel model) {
                Objects.requireNonNull(model, "model is required");

                return partition(
                                model.getPrintableRows(),
                                model.getCustomerDisplayMode());
        }

        public EstimatePdfPrintableRowPartition partition(
                        List<EstimatePdfPrintableRow> printableRows,
                        CustomerDisplayMode customerDisplayMode) {
                Objects.requireNonNull(printableRows, "printableRows is required");

                if (printableRows.isEmpty()) {
                        return EstimatePdfPrintableRowPartition.empty();
                }

                CustomerDisplayMode effectiveDisplayMode = customerDisplayMode != null
                                ? customerDisplayMode
                                : CustomerDisplayMode.ITEM_LEVEL;

                Set<EstimatePdfItemLine> itemsWithVisibleCosts = findItemsWithVisibleCosts(printableRows);

                List<EstimatePdfPrintableRow> detailRows = printableRows.stream()
                                .filter(row -> !row.isContinuationContext())
                                .filter(row -> isDetailRow(
                                                row,
                                                effectiveDisplayMode,
                                                itemsWithVisibleCosts))
                                .toList();

                if (detailRows.isEmpty()) {
                        return new EstimatePdfPrintableRowPartition(
                                        printableRows,
                                        List.of());
                }

                int carryStartIndex = Math.max(
                                0,
                                detailRows.size() - MINIMUM_FINAL_DETAIL_ROWS);

                List<EstimatePdfPrintableRow> selectedCarryDetails = detailRows.subList(
                                carryStartIndex,
                                detailRows.size());

                List<EstimatePdfPrintableRow> remainingDetails = detailRows.subList(
                                0,
                                carryStartIndex);

                Set<EstimatePdfPrintableRow> selectedCarryDetailSet = newIdentitySet();
                selectedCarryDetailSet.addAll(selectedCarryDetails);

                List<EstimatePdfPrintableRow> mainRows = new ArrayList<>();

                for (EstimatePdfPrintableRow row : printableRows) {
                        if (selectedCarryDetailSet.contains(row)) {
                                continue;
                        }

                        if (isContextOnlyRow(
                                        row,
                                        effectiveDisplayMode,
                                        itemsWithVisibleCosts)
                                        && !hasRemainingDetail(row, remainingDetails)) {
                                continue;
                        }

                        mainRows.add(row);
                }

                ContextRowIndex contextRowIndex = new ContextRowIndex(printableRows);

                Set<EstimatePdfPrintableRow> mainRowSet = newIdentitySet();
                mainRowSet.addAll(mainRows);

                List<EstimatePdfPrintableRow> finalCarryRows = buildFinalCarryRows(
                                selectedCarryDetails,
                                effectiveDisplayMode,
                                contextRowIndex,
                                mainRowSet);

                return new EstimatePdfPrintableRowPartition(
                                mainRows,
                                finalCarryRows);
        }

        private List<EstimatePdfPrintableRow> buildFinalCarryRows(
                        List<EstimatePdfPrintableRow> selectedDetails,
                        CustomerDisplayMode displayMode,
                        ContextRowIndex contextRowIndex,
                        Set<EstimatePdfPrintableRow> mainRowSet) {
                List<EstimatePdfPrintableRow> carryRows = new ArrayList<>();

                EstimatePdfGroup currentGroup = null;
                EstimatePdfWorkTypeGroup currentWorkType = null;
                EstimatePdfItemLine currentItem = null;

                for (EstimatePdfPrintableRow detail : selectedDetails) {
                        if (displayMode != CustomerDisplayMode.GROUP_LEVEL
                                        && detail.getGroup() != currentGroup) {

                                addCarryContext(
                                                carryRows,
                                                contextRowIndex.groupRow(detail.getGroup()),
                                                EstimatePdfPrintableRow.forGroup(
                                                                detail.getGroup()),
                                                mainRowSet);

                                currentGroup = detail.getGroup();
                                currentWorkType = null;
                                currentItem = null;
                        }

                        if ((displayMode == CustomerDisplayMode.ITEM_LEVEL
                                        || displayMode == CustomerDisplayMode.ITEM_COST_LEVEL)
                                        && detail.getWorkType() != currentWorkType) {

                                addCarryContext(
                                                carryRows,
                                                contextRowIndex.workTypeRow(
                                                                detail.getWorkType()),
                                                EstimatePdfPrintableRow.forWorkType(
                                                                detail.getGroup(),
                                                                detail.getWorkType()),
                                                mainRowSet);

                                currentWorkType = detail.getWorkType();
                                currentItem = null;
                        }

                        if (displayMode == CustomerDisplayMode.ITEM_COST_LEVEL
                                        && detail.getRowType() == EstimatePdfPrintableRowType.COST
                                        && detail.getItem() != currentItem) {

                                addCarryContext(
                                                carryRows,
                                                contextRowIndex.itemRow(detail.getItem()),
                                                EstimatePdfPrintableRow.forItem(
                                                                detail.getGroup(),
                                                                detail.getWorkType(),
                                                                detail.getItem()),
                                                mainRowSet);

                                currentItem = detail.getItem();
                        }

                        carryRows.add(detail);

                        if (detail.getRowType() == EstimatePdfPrintableRowType.ITEM) {
                                currentItem = detail.getItem();
                        }
                }

                return carryRows;
        }

        private void addCarryContext(
                        List<EstimatePdfPrintableRow> carryRows,
                        EstimatePdfPrintableRow originalContextRow,
                        EstimatePdfPrintableRow fallbackContextRow,
                        Set<EstimatePdfPrintableRow> mainRowSet) {
                EstimatePdfPrintableRow contextRow = originalContextRow != null
                                ? originalContextRow
                                : fallbackContextRow;

                if (mainRowSet.contains(contextRow)) {
                        carryRows.add(
                                        contextRow.copyAsContinuationContext());
                        return;
                }

                carryRows.add(contextRow);
        }

        private Set<EstimatePdfItemLine> findItemsWithVisibleCosts(
                        List<EstimatePdfPrintableRow> printableRows) {
                Set<EstimatePdfItemLine> items = Collections.newSetFromMap(
                                new IdentityHashMap<>());

                for (EstimatePdfPrintableRow row : printableRows) {
                        if (row.getRowType() == EstimatePdfPrintableRowType.COST
                                        && row.getItem() != null) {
                                items.add(row.getItem());
                        }
                }

                return items;
        }

        private boolean isDetailRow(
                        EstimatePdfPrintableRow row,
                        CustomerDisplayMode displayMode,
                        Set<EstimatePdfItemLine> itemsWithVisibleCosts) {
                return switch (displayMode) {
                        case GROUP_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.GROUP;

                        case WORK_TYPE_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.WORK_TYPE;

                        case ITEM_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.ITEM;

                        case ITEM_COST_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.COST
                                                || (row.getRowType() == EstimatePdfPrintableRowType.ITEM
                                                                && !itemsWithVisibleCosts.contains(
                                                                                row.getItem()));
                };
        }

        private boolean isContextOnlyRow(
                        EstimatePdfPrintableRow row,
                        CustomerDisplayMode displayMode,
                        Set<EstimatePdfItemLine> itemsWithVisibleCosts) {
                if (isDetailRow(
                                row,
                                displayMode,
                                itemsWithVisibleCosts)) {
                        return false;
                }

                return switch (displayMode) {
                        case GROUP_LEVEL -> false;

                        case WORK_TYPE_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.GROUP;

                        case ITEM_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.GROUP
                                                || row.getRowType() == EstimatePdfPrintableRowType.WORK_TYPE;

                        case ITEM_COST_LEVEL ->
                                row.getRowType() == EstimatePdfPrintableRowType.GROUP
                                                || row.getRowType() == EstimatePdfPrintableRowType.WORK_TYPE
                                                || row.getRowType() == EstimatePdfPrintableRowType.ITEM;
                };
        }

        private boolean hasRemainingDetail(
                        EstimatePdfPrintableRow contextRow,
                        List<EstimatePdfPrintableRow> remainingDetails) {
                for (EstimatePdfPrintableRow detail : remainingDetails) {
                        if (contextRow.getRowType() == EstimatePdfPrintableRowType.GROUP
                                        && detail.getGroup() == contextRow.getGroup()) {
                                return true;
                        }

                        if (contextRow.getRowType() == EstimatePdfPrintableRowType.WORK_TYPE
                                        && detail.getWorkType() == contextRow.getWorkType()) {
                                return true;
                        }

                        if (contextRow.getRowType() == EstimatePdfPrintableRowType.ITEM
                                        && detail.getItem() == contextRow.getItem()) {
                                return true;
                        }
                }

                return false;
        }

        private static <T> Set<T> newIdentitySet() {
                return Collections.newSetFromMap(
                                new IdentityHashMap<>());
        }

        private static class ContextRowIndex {

                private final Map<EstimatePdfGroup, EstimatePdfPrintableRow> groupRows = new IdentityHashMap<>();

                private final Map<EstimatePdfWorkTypeGroup, EstimatePdfPrintableRow> workTypeRows = new IdentityHashMap<>();

                private final Map<EstimatePdfItemLine, EstimatePdfPrintableRow> itemRows = new IdentityHashMap<>();

                private ContextRowIndex(
                                List<EstimatePdfPrintableRow> printableRows) {
                        for (EstimatePdfPrintableRow row : printableRows) {
                                if (row.getRowType() == EstimatePdfPrintableRowType.GROUP) {
                                        groupRows.put(row.getGroup(), row);
                                } else if (row.getRowType() == EstimatePdfPrintableRowType.WORK_TYPE) {
                                        workTypeRows.put(row.getWorkType(), row);
                                } else if (row.getRowType() == EstimatePdfPrintableRowType.ITEM) {
                                        itemRows.put(row.getItem(), row);
                                }
                        }
                }

                private EstimatePdfPrintableRow groupRow(
                                EstimatePdfGroup group) {
                        return groupRows.get(group);
                }

                private EstimatePdfPrintableRow workTypeRow(
                                EstimatePdfWorkTypeGroup workType) {
                        return workTypeRows.get(workType);
                }

                private EstimatePdfPrintableRow itemRow(
                                EstimatePdfItemLine item) {
                        return itemRows.get(item);
                }
        }
}