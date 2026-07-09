package com.glassgang.pmworkflow.estimate.dto.pdf;

public record EstimatePdfDesignerVariableResponse(
        String key,
        String label,
        String category,
        String description,
        String mustache,
        String sampleValue
) {
}