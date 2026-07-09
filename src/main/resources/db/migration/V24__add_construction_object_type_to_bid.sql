ALTER TABLE estimate.bid
ADD COLUMN construction_object_type_id UUID;

ALTER TABLE estimate.bid
ADD CONSTRAINT fk_bid_construction_object_type
FOREIGN KEY (construction_object_type_id)
REFERENCES estimate.construction_object_type (construction_object_type_id);

CREATE INDEX ix_bid_construction_object_type_id
ON estimate.bid (construction_object_type_id);