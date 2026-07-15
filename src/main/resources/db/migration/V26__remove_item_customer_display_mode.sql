ALTER TABLE estimate.bid_revision_item
    DROP CONSTRAINT chk_bid_revision_item_customer_display_mode;

ALTER TABLE estimate.bid_revision_item
    DROP COLUMN customer_display_mode;