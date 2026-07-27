package com.company.ConstructionContractorWorkflowToolkit.estimate.entity;

import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.EstimatePriceDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.CustomerDisplayMode;
import com.company.ConstructionContractorWorkflowToolkit.estimate.enums.RevisionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "bid_revision")
public class BidRevision {

    @Id
    @Column(name = "bid_revision_id")
    private UUID bidRevisionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    @Column(name = "revision_number", nullable = false)
    private Integer revisionNumber;

    @Column(name = "revision_display_name", nullable = false, length = 100)
    private String revisionDisplayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "revision_status", nullable = false, length = 50)
    private RevisionStatus revisionStatus;

    @Column(name = "tax_type", length = 50)
    private String taxType;

    @Column(name = "tax_rate_percent", precision = 9, scale = 4)
    private BigDecimal taxRatePercent;

    @Column(name = "subtotal_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalCost;

    @Column(name = "subtotal_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalPrice;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPrice;

    @Column(name = "customer_note")
    private String customerNote;

    @Column(name = "internal_note")
    private String internalNote;

    @Column(name = "sent_at_utc")
    private LocalDateTime sentAtUtc;

    @Column(name = "awarded_at_utc")
    private LocalDateTime awardedAtUtc;

    @Column(name = "lost_at_utc")
    private LocalDateTime lostAtUtc;

    @Column(name = "archived_at_utc")
    private LocalDateTime archivedAtUtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloned_from_bid_revision_id")
    private BidRevision clonedFromBidRevision;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "updated_at_utc", nullable = false)
    private LocalDateTime updatedAtUtc;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "deleted_at_utc")
    private LocalDateTime deletedAtUtc;

    @Column(name = "deleted_by_user_id")
    private UUID deletedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_display_mode", nullable = false, length = 50)
    private EstimatePriceDisplayMode priceDisplayMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_display_mode", nullable = false, length = 50)
    private CustomerDisplayMode customerDisplayMode;

    @Column(name = "default_tax_rate_snapshot_code", length = 50)
    private String defaultTaxRateSnapshotCode;

    @Column(name = "default_tax_rate_snapshot_name", length = 150)
    private String defaultTaxRateSnapshotName;

    @Column(name = "default_tax_rate_snapshot_percent", precision = 9, scale = 4)
    private BigDecimal defaultTaxRateSnapshotPercent;
}