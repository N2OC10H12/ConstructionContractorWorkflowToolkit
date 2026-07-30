package com.company.ConstructionContractorWorkflowToolkit.estimate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimatePriceDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CustomerDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.RevisionStatus;

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
    private CustomerDisplayMode customerDisplayMode;
    private EstimatePriceDisplayMode priceDisplayMode;
    private LocalDateTime sentAtUtc;
    private LocalDateTime awardedAtUtc;
    private LocalDateTime lostAtUtc;
    private LocalDateTime archivedAtUtc;
    private UUID clonedFromBidRevisionId;
    private LocalDateTime createdAtUtc;
    private LocalDateTime updatedAtUtc;
    private String defaultTaxRateSnapshotCode;
    private String defaultTaxRateSnapshotName;
    private BigDecimal defaultTaxRateSnapshotPercent;
}
