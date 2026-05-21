INSERT INTO estimate.cost_element (
    cost_element_id,
    code,
    name,
    description,
    is_active,
    created_at_utc,
    updated_at_utc
)
VALUES
    ('00000000-0000-0000-0000-000000000101', 'FIELD_INSTALL', 'Field Install', 'Field installation labor.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000102', 'SHOP_FABRICATION', 'Shop Fabrication', 'Shop fabrication labor.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000103', 'FIELD_LAYOUT', 'Field Layout', 'Field layout and measurement labor.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000104', 'PROJECT_MANAGEMENT', 'Project Management', 'Project management cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000105', 'DRIVE_TIME', 'Drive Time', 'Travel and drive time cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000106', 'FIELD_STAGING', 'Field Staging', 'Field staging and preparation.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000107', 'PER_DIEM', 'Per Diem', 'Daily per diem cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000108', 'HOTEL', 'Hotel', 'Hotel and lodging cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000109', 'FIELD_SUPERVISION', 'Field Supervision', 'Field supervision labor.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000110', 'MATERIAL', 'Material', 'Material cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000111', 'OTHER', 'Other', 'Miscellaneous cost.', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    INSERT INTO estimate.cost_rate (
    cost_rate_id,
    code,
    name,
    description,
    rate_amount,
    rate_unit,
    is_active,
    created_at_utc,
    updated_at_utc
)
VALUES
    ('00000000-0000-0000-0000-000000000201', 'INSTALLER_LABOR', 'Installer Labor', 'Default installer labor hourly rate.', 0.0000, 'HOUR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000202', 'FOREMAN_LABOR', 'Foreman Labor', 'Default foreman hourly rate.', 0.0000, 'HOUR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000203', 'DRIVE_TIME', 'Drive Time', 'Default drive time hourly rate.', 0.0000, 'HOUR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000204', 'HOTEL_NIGHT', 'Hotel Night', 'Default hotel nightly cost.', 0.0000, 'NIGHT', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('00000000-0000-0000-0000-000000000205', 'PER_DIEM_DAY', 'Per Diem Day', 'Default per diem daily cost.', 0.0000, 'DAY', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);