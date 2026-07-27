-- Replace the legacy Estimate Item Type classification with
-- the canonical Company Work Type catalog.
--
-- This migration performs a clean schema replacement.
-- It does not map legacy Item Type values to Company Work Types.

ALTER TABLE estimate.bid_revision_item
    ADD COLUMN company_work_type_id UUID,
    ADD COLUMN company_work_type_snapshot_code VARCHAR(30),
    ADD COLUMN company_work_type_snapshot_name VARCHAR(200);

ALTER TABLE estimate.bid_revision_item
    ADD CONSTRAINT fk_bid_revision_item_company_work_type
        FOREIGN KEY (company_work_type_id)
        REFERENCES estimate.company_work_type (company_work_type_id);

CREATE INDEX idx_bid_revision_item_company_work_type_id
    ON estimate.bid_revision_item (company_work_type_id)
    WHERE is_deleted = FALSE;

CREATE INDEX idx_bid_revision_item_revision_group_company_work_type
    ON estimate.bid_revision_item (
        bid_revision_id,
        group_name,
        company_work_type_id
    )
    WHERE is_deleted = FALSE;


-- Replace persisted display-mode terminology.

ALTER TABLE estimate.bid_revision
    DROP CONSTRAINT chk_bid_revision_customer_display_mode;

ALTER TABLE estimate.bid_revision
    DROP CONSTRAINT chk_bid_revision_price_display_mode;

UPDATE estimate.bid_revision
SET customer_display_mode = 'WORK_TYPE_LEVEL'
WHERE customer_display_mode = 'ITEM_TYPE_LEVEL';

UPDATE estimate.bid_revision
SET price_display_mode = 'WORK_TYPE_LEVEL'
WHERE price_display_mode = 'ITEM_TYPE_LEVEL';

ALTER TABLE estimate.bid_revision
    ALTER COLUMN price_display_mode
        SET DEFAULT 'WORK_TYPE_LEVEL';

ALTER TABLE estimate.bid_revision
    ADD CONSTRAINT chk_bid_revision_customer_display_mode
        CHECK (
            customer_display_mode IN (
                'GROUP_LEVEL',
                'WORK_TYPE_LEVEL',
                'ITEM_LEVEL',
                'ITEM_COST_LEVEL'
            )
        );

ALTER TABLE estimate.bid_revision
    ADD CONSTRAINT chk_bid_revision_price_display_mode
        CHECK (
            price_display_mode IN (
                'TOTALS',
                'GROUP_LEVEL',
                'WORK_TYPE_LEVEL',
                'ITEM_LEVEL',
                'ITEM_COST_LEVEL'
            )
        );


-- Remove the legacy Item Type relationship and catalog.

ALTER TABLE estimate.bid_revision_item
    DROP CONSTRAINT fk_bid_revision_item_item_type;

ALTER TABLE estimate.bid_revision_item
    DROP COLUMN item_type_id;

DROP TABLE estimate.item_type;