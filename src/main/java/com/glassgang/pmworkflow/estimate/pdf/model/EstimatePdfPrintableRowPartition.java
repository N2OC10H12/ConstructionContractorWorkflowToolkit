package com.glassgang.pmworkflow.estimate.pdf.model;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class EstimatePdfPrintableRowPartition {

    private final List<EstimatePdfPrintableRow> mainRows;
    private final List<EstimatePdfPrintableRow> finalCarryRows;

    public EstimatePdfPrintableRowPartition(
            List<EstimatePdfPrintableRow> mainRows,
            List<EstimatePdfPrintableRow> finalCarryRows
    ) {
        this.mainRows = List.copyOf(
                Objects.requireNonNull(mainRows, "mainRows is required"));

        this.finalCarryRows = List.copyOf(
                Objects.requireNonNull(
                        finalCarryRows,
                        "finalCarryRows is required"));
    }

    public static EstimatePdfPrintableRowPartition empty() {
        return new EstimatePdfPrintableRowPartition(
                List.of(),
                List.of());
    }
}