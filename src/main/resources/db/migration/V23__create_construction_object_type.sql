CREATE TABLE estimate.construction_object_type (
    construction_object_type_id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at_utc TIMESTAMP NOT NULL,
    updated_at_utc TIMESTAMP,
    deleted_at_utc TIMESTAMP,
    deleted_by_user_id UUID
);

CREATE UNIQUE INDEX ux_construction_object_type_code_active
ON estimate.construction_object_type (code)
WHERE is_deleted = false;

INSERT INTO estimate.construction_object_type (
    construction_object_type_id,
    code,
    name,
    is_active,
    is_deleted,
    created_at_utc,
    updated_at_utc
)
VALUES
    (gen_random_uuid(), 'CHURCH', 'Church', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'GOVERNMENT', 'Government', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HOSPITAL', 'Hospital', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HOTEL', 'Hotel', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'INDUSTRIAL_TILT_WALL', 'Industrial Tilt Wall', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'MULTIFAMILY', 'Multifamily', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'RESTAURANT', 'Restaurant', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'RETAIL_STORE', 'Retail Store', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HOUSE', 'House', true, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);