package com.glassgang.pmworkflow.estimate.dto.pdf;

import java.util.List;

public record EstimatePdfDesignerRegistryResponse(
        List<EstimatePdfDesignerVariableResponse> variables,
        List<EstimatePdfDesignerBlockResponse> blocks
) {
}