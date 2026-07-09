package com.glassgang.pmworkflow.estimate.dto.pdf;

import java.util.List;

public record EstimatePdfDesignerBlockResponse(
        String key,
        String label,
        String category,
        String description,
        Boolean required,
        Boolean protectedBlock,
        Boolean dynamic,
        List<String> allowedStyleKeys,
        List<String> cssClasses
) {
}