ALTER TABLE estimate.bid
    ADD COLUMN estimate_scope VARCHAR(1000);

ALTER TABLE estimate.bid_revision
    DROP COLUMN customer_note,
    DROP COLUMN internal_note;