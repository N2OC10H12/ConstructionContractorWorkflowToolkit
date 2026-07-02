ALTER TABLE estimate.bid_revision
ADD COLUMN price_display_mode VARCHAR(50) NOT NULL DEFAULT 'ITEM_TYPE_LEVEL';