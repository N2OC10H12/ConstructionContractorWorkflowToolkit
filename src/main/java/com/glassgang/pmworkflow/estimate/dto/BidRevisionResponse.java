package com.glassgang.pmworkflow.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.glassgang.pmworkflow.estimate.enums.RevisionStatus;

@Getter
@Setter
public class BidRevisionResponse {
    private UUID bidRevisionId;
    private UUID bidId;
    private Integer revisionNumber;
    private String revisionDisplayName;
    private RevisionStatus revisionStatus;
    private BigDecimal subtotalCost;
    private BigDecimal subtotalPrice;
    private BigDecimal taxAmount;
    private BigDecimal totalPrice;
    private String customerNote;
    private LocalDateTime sentAtUtc;
    private LocalDateTime awardedAtUtc;
    private LocalDateTime lostAtUtc;
    private LocalDateTime archivedAtUtc;
    private UUID clonedFromBidRevisionId;
    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
}
