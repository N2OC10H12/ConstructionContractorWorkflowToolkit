ALTER TABLE estimate.bid
ADD COLUMN default_tax_rate_id UUID NULL;

ALTER TABLE estimate.bid
ADD CONSTRAINT fk_bid_default_tax_rate
FOREIGN KEY (default_tax_rate_id)
REFERENCES estimate.tax_rate(tax_rate_id);

CREATE INDEX idx_bid_default_tax_rate_id
ON estimate.bid(default_tax_rate_id);