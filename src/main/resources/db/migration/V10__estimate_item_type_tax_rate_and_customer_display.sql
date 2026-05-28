CREATE TABLE estimate.item_type (
    item_type_id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at_utc TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at_utc TIMESTAMP,
    deleted_at_utc TIMESTAMP,
    deleted_by_user_id UUID
);

CREATE TABLE estimate.tax_rate (
    tax_rate_id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    rate_percent NUMERIC(9,4) NOT NULL,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at_utc TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at_utc TIMESTAMP,
    deleted_at_utc TIMESTAMP,
    deleted_by_user_id UUID,

    CONSTRAINT chk_tax_rate_percent_non_negative
        CHECK (rate_percent >= 0)
);

ALTER TABLE estimate.bid_revision_item
    ADD COLUMN item_type_id UUID,
    ADD COLUMN tax_rate_id UUID,
    ADD COLUMN tax_rate_snapshot_code VARCHAR(50),
    ADD COLUMN tax_rate_snapshot_name VARCHAR(150),
    ADD COLUMN tax_rate_snapshot_percent NUMERIC(9,4),
    ADD COLUMN customer_display_mode VARCHAR(80) NOT NULL DEFAULT 'ITEM_TOTAL_ONLY';

ALTER TABLE estimate.bid_revision_item
    ADD CONSTRAINT fk_bid_revision_item_item_type
        FOREIGN KEY (item_type_id)
        REFERENCES estimate.item_type(item_type_id);

ALTER TABLE estimate.bid_revision_item
    ADD CONSTRAINT fk_bid_revision_item_tax_rate
        FOREIGN KEY (tax_rate_id)
        REFERENCES estimate.tax_rate(tax_rate_id);

ALTER TABLE estimate.bid_revision_item
    ADD CONSTRAINT chk_bid_revision_item_customer_display_mode
        CHECK (customer_display_mode IN (
            'HIDDEN',
            'TYPE_NAME_ONLY',
            'TYPE_TOTAL_ONLY',
            'TYPE_WITH_ITEM_BREAKDOWN_NO_ITEM_PRICES',
            'TYPE_WITH_ITEM_BREAKDOWN_AND_ITEM_PRICES',
            'ITEM_TOTAL_ONLY',
            'ITEM_WITH_COST_BREAKDOWN'
        ));

INSERT INTO estimate.item_type (
    item_type_id,
    code,
    name,
    description
)
VALUES
('00000000-0000-0000-0000-000000000301', 'GLASS', 'Glass', 'Glass estimate item type'),
('00000000-0000-0000-0000-000000000302', 'DOOR', 'Door', 'Door estimate item type'),
('00000000-0000-0000-0000-000000000303', 'CAULK', 'Caulk', 'Caulk estimate item type'),
('00000000-0000-0000-0000-000000000304', 'HARDWARE', 'Hardware', 'Hardware estimate item type'),
('00000000-0000-0000-0000-000000000305', 'CURTAIN_WALL', 'Curtain Wall', 'Curtain wall estimate item type'),
('00000000-0000-0000-0000-000000000306', 'STOREFRONT', 'Storefront', 'Storefront estimate item type'),
('00000000-0000-0000-0000-000000000307', 'MISC', 'Misc', 'Miscellaneous estimate item type');

INSERT INTO estimate.tax_rate (
    tax_rate_id,
    code,
    name,
    rate_percent,
    is_default
)
VALUES
('00000000-0000-0000-0000-000000000401', 'TAX_EXEMPT', 'Tax Exempt', 0.0000, TRUE),
('00000000-0000-0000-0000-000000000402', 'STANDARD_0825', 'Standard 8.25%', 8.2500, FALSE);