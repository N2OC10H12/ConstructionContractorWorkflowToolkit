package com.glassgang.pmworkflow.estimate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "estimate", name = "bid_revision_item_cost")
public class BidRevisionItemCost {

    @Id
    @Column(name = "bid_revision_item_cost_id")
    private UUID bidRevisionItemCostId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bid_revision_item_id", nullable = false)
    private BidRevisionItem bidRevisionItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cost_element_id", nullable = false)
    private CostElement costElement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_rate_id")
    private CostRate costRate;

    @Column(name = "line_number", nullable = false)
    private Integer lineNumber;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_of_measure", nullable = false, length = 50)
    private String unitOfMeasure;

    @Column(name = "rate_snapshot", precision = 19, scale = 4)
    private BigDecimal rateSnapshot;

    @Column(name = "rate_unit_snapshot", length = 50)
    private String rateUnitSnapshot;

    @Column(name = "unit_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "total_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCost;

    @Column(name = "total_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPrice;

    @Column(name = "markup_percent", precision = 9, scale = 4)
    private BigDecimal markupPercent;

    @Column(name = "gpm_percent", precision = 9, scale = 4)
    private BigDecimal gpmPercent;

    @Column(name = "is_taxable", nullable = false)
    private Boolean isTaxable;

    @Column(name = "tax_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal taxAmount;

    @Column(name = "price_with_tax", nullable = false, precision = 19, scale = 4)
    private BigDecimal priceWithTax;

    @Column(name = "show_customer", nullable = false)
    private Boolean showCustomer;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "internal_note")
    private String internalNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloned_from_item_cost_id")
    private BidRevisionItemCost clonedFromItemCost;

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
}