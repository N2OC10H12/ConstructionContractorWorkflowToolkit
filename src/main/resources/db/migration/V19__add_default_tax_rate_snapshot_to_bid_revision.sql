ALTER TABLE estimate.bid_revision
ADD COLUMN default_tax_rate_snapshot_code VARCHAR(50) NULL,
ADD COLUMN default_tax_rate_snapshot_name VARCHAR(150) NULL,
ADD COLUMN default_tax_rate_snapshot_percent NUMERIC(9,4) NULL;

UPDATE estimate.bid_revision br
SET
    default_tax_rate_snapshot_code = tr.code,
    default_tax_rate_snapshot_name = tr.name,
    default_tax_rate_snapshot_percent = tr.rate_percent
FROM estimate.bid b
JOIN estimate.tax_rate tr
    ON tr.tax_rate_id = b.default_tax_rate_id
WHERE br.bid_id = b.bid_id;