package com.company.ConstructionContractorWorkflowToolkit.estimate.entity;

import com.company.ConstructionContractorWorkflowToolkit.file.entity.StoredFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        schema = "estimate",
        name = "bid_revision_item_quote")
public class BidRevisionItemQuote {

    @Id
    @Column(name = "bid_revision_item_quote_id")
    private UUID bidRevisionItemQuoteId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "bid_revision_item_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_bid_revision_item_quote_item"))
    private BidRevisionItem bidRevisionItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "stored_file_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_bid_revision_item_quote_stored_file"))
    private StoredFile storedFile;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at_utc", nullable = false)
    private LocalDateTime createdAtUtc;

    @Column(name = "created_by_user_id", nullable = false)
    private UUID createdByUserId;
}