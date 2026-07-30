package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record BidRevisionItemQuoteResponse(
        UUID bidRevisionItemQuoteId,
        UUID bidRevisionItemId,

        String description,
        Integer displayOrder,

        String fileName,
        String contentType,
        Long sizeBytes,

        UUID uploadedByUserId,
        LocalDateTime uploadedAtUtc,

        UUID attachedByUserId,
        LocalDateTime attachedAtUtc,

        String previewUrl,
        String downloadUrl
) {
}