-- Rename the price mode. The totals block remains visible.
UPDATE estimate.bid_revision
SET price_display_mode = 'TOTALS'
WHERE price_display_mode = 'HIDDEN';


-- Customer detail level now belongs to the revision.
--
-- Existing revisions use ITEM_LEVEL because the old per-item values
-- cannot be converted losslessly into one revision-wide value.
ALTER TABLE estimate.bid_revision
    ADD COLUMN customer_display_mode VARCHAR(50)
        NOT NULL
        DEFAULT 'ITEM_LEVEL';


ALTER TABLE estimate.bid_revision
    ADD CONSTRAINT chk_bid_revision_customer_display_mode
        CHECK (customer_display_mode IN (
            'GROUP_LEVEL',
            'ITEM_TYPE_LEVEL',
            'ITEM_LEVEL',
            'ITEM_COST_LEVEL'
        ));


ALTER TABLE estimate.bid_revision
    ADD CONSTRAINT chk_bid_revision_price_display_mode
        CHECK (price_display_mode IN (
            'TOTALS',
            'GROUP_LEVEL',
            'ITEM_TYPE_LEVEL',
            'ITEM_LEVEL',
            'ITEM_COST_LEVEL'
        ));