ALTER TABLE company.company_profile
    ADD COLUMN default_bid_rounding_mode varchar(20);

UPDATE company.company_profile
SET default_bid_rounding_mode = 'WHOLE'
WHERE default_bid_rounding_mode IS NULL;

ALTER TABLE company.company_profile
    ALTER COLUMN default_bid_rounding_mode SET DEFAULT 'WHOLE',
    ALTER COLUMN default_bid_rounding_mode SET NOT NULL;

ALTER TABLE company.company_profile
    ADD CONSTRAINT ck_company_profile_default_bid_rounding_mode
        CHECK (default_bid_rounding_mode IN ('WHOLE', 'FRACTIONAL'));

ALTER TABLE estimate.bid
    ADD COLUMN rounding_mode varchar(20);

UPDATE estimate.bid
SET rounding_mode = 'WHOLE'
WHERE rounding_mode IS NULL;

ALTER TABLE estimate.bid
    ALTER COLUMN rounding_mode SET DEFAULT 'WHOLE',
    ALTER COLUMN rounding_mode SET NOT NULL;

ALTER TABLE estimate.bid
    ADD CONSTRAINT ck_bid_rounding_mode
        CHECK (rounding_mode IN ('WHOLE', 'FRACTIONAL'));
