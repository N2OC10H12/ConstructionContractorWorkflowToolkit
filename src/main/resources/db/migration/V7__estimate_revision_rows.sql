CREATE TABLE estimate.bid_revision_item (
    bid_revision_item_id UUID PRIMARY KEY,
    bid_revision_id UUID NOT NULL,

    line_number INTEGER NOT NULL,
    display_order INTEGER NOT NULL,

    group_name VARCHAR(255) NULL,

    description TEXT NOT NULL,

    quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(50) NOT NULL,

    unit_cost NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_price NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_cost NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_price NUMERIC(19,4) NOT NULL DEFAULT 0,

    markup_percent NUMERIC(9,4) NULL,
    gpm_percent NUMERIC(9,4) NULL,

    is_taxable BOOLEAN NOT NULL DEFAULT true,
    tax_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    price_with_tax NUMERIC(19,4) NOT NULL DEFAULT 0,

    is_optional BOOLEAN NOT NULL DEFAULT false,

    show_customer_row BOOLEAN NOT NULL DEFAULT true,
    show_customer_price BOOLEAN NOT NULL DEFAULT true,

    internal_note TEXT NULL,

    cloned_from_item_id UUID NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_bid_revision_item_revision
        FOREIGN KEY (bid_revision_id)
        REFERENCES estimate.bid_revision (bid_revision_id),

    CONSTRAINT fk_bid_revision_item_cloned_from
        FOREIGN KEY (cloned_from_item_id)
        REFERENCES estimate.bid_revision_item (bid_revision_item_id),

    CONSTRAINT uq_bid_revision_item_line_number
        UNIQUE (bid_revision_id, line_number),

    CONSTRAINT chk_bid_revision_item_line_number_positive
        CHECK (line_number > 0),

    CONSTRAINT chk_bid_revision_item_display_order_non_negative
        CHECK (display_order >= 0),

    CONSTRAINT chk_bid_revision_item_description_not_blank
        CHECK (length(trim(description)) > 0),

    CONSTRAINT chk_bid_revision_item_quantity_non_negative
        CHECK (quantity >= 0),

    CONSTRAINT chk_bid_revision_item_uom_not_blank
        CHECK (length(trim(unit_of_measure)) > 0),

    CONSTRAINT chk_bid_revision_item_money_non_negative
        CHECK (
            unit_cost >= 0
            AND unit_price >= 0
            AND total_cost >= 0
            AND total_price >= 0
            AND tax_amount >= 0
            AND price_with_tax >= 0
        ),

    CONSTRAINT chk_bid_revision_item_markup_non_negative
        CHECK (markup_percent IS NULL OR markup_percent >= 0),

    CONSTRAINT chk_bid_revision_item_gpm_non_negative
        CHECK (gpm_percent IS NULL OR gpm_percent >= 0)
);

CREATE TABLE estimate.bid_revision_item_cost (
    bid_revision_item_cost_id UUID PRIMARY KEY,
    bid_revision_item_id UUID NOT NULL,

    cost_element_id UUID NOT NULL,
    cost_rate_id UUID NULL,

    line_number INTEGER NOT NULL,
    display_order INTEGER NOT NULL,

    quantity NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_of_measure VARCHAR(50) NOT NULL,

    rate_snapshot NUMERIC(19,4) NULL,
    rate_unit_snapshot VARCHAR(50) NULL,

    unit_cost NUMERIC(19,4) NOT NULL DEFAULT 0,
    unit_price NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_cost NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_price NUMERIC(19,4) NOT NULL DEFAULT 0,

    markup_percent NUMERIC(9,4) NULL,
    gpm_percent NUMERIC(9,4) NULL,

    is_taxable BOOLEAN NOT NULL DEFAULT true,
    tax_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    price_with_tax NUMERIC(19,4) NOT NULL DEFAULT 0,

    show_customer BOOLEAN NOT NULL DEFAULT false,
    is_optional BOOLEAN NOT NULL DEFAULT false,

    group_name VARCHAR(255) NULL,
    internal_note TEXT NULL,

    cloned_from_item_cost_id UUID NULL,

    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP NOT NULL,
    created_by_user_id UUID NULL,
    updated_by_user_id UUID NULL,

    is_deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at_utc TIMESTAMP NULL,
    deleted_by_user_id UUID NULL,

    CONSTRAINT fk_bid_revision_item_cost_item
        FOREIGN KEY (bid_revision_item_id)
        REFERENCES estimate.bid_revision_item (bid_revision_item_id),

    CONSTRAINT fk_bid_revision_item_cost_element
        FOREIGN KEY (cost_element_id)
        REFERENCES estimate.cost_element (cost_element_id),

    CONSTRAINT fk_bid_revision_item_cost_rate
        FOREIGN KEY (cost_rate_id)
        REFERENCES estimate.cost_rate (cost_rate_id),

    CONSTRAINT fk_bid_revision_item_cost_cloned_from
        FOREIGN KEY (cloned_from_item_cost_id)
        REFERENCES estimate.bid_revision_item_cost (bid_revision_item_cost_id),

    CONSTRAINT uq_bid_revision_item_cost_line_number
        UNIQUE (bid_revision_item_id, line_number),

    CONSTRAINT chk_bid_revision_item_cost_line_number_positive
        CHECK (line_number > 0),

    CONSTRAINT chk_bid_revision_item_cost_display_order_non_negative
        CHECK (display_order >= 0),

    CONSTRAINT chk_bid_revision_item_cost_quantity_non_negative
        CHECK (quantity >= 0),

    CONSTRAINT chk_bid_revision_item_cost_uom_not_blank
        CHECK (length(trim(unit_of_measure)) > 0),

    CONSTRAINT chk_bid_revision_item_cost_money_non_negative
        CHECK (
            unit_cost >= 0
            AND unit_price >= 0
            AND total_cost >= 0
            AND total_price >= 0
            AND tax_amount >= 0
            AND price_with_tax >= 0
        ),

    CONSTRAINT chk_bid_revision_item_cost_markup_non_negative
        CHECK (markup_percent IS NULL OR markup_percent >= 0),

    CONSTRAINT chk_bid_revision_item_cost_gpm_non_negative
        CHECK (gpm_percent IS NULL OR gpm_percent >= 0)
);

CREATE INDEX idx_bid_revision_item_revision_id
    ON estimate.bid_revision_item (bid_revision_id);

CREATE INDEX idx_bid_revision_item_not_deleted
    ON estimate.bid_revision_item (is_deleted);

CREATE INDEX idx_bid_revision_item_cost_item_id
    ON estimate.bid_revision_item_cost (bid_revision_item_id);

CREATE INDEX idx_bid_revision_item_cost_element_id
    ON estimate.bid_revision_item_cost (cost_element_id);

CREATE INDEX idx_bid_revision_item_cost_rate_id
    ON estimate.bid_revision_item_cost (cost_rate_id);

CREATE INDEX idx_bid_revision_item_cost_not_deleted
    ON estimate.bid_revision_item_cost (is_deleted);