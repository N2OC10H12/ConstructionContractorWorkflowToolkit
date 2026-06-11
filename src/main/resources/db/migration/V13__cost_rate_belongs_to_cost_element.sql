alter table estimate.cost_rate
    add column cost_element_id uuid;

alter table estimate.cost_rate
    add constraint cost_rate_cost_element_id_fkey
    foreign key (cost_element_id)
    references estimate.cost_element(cost_element_id);

update estimate.cost_element
set is_active = false,
    is_deleted = true,
    deleted_at_utc = now(),
    updated_at_utc = now()
where code in (
    'FIELD_INSTALL',
    'SHOP_FABRICATION',
    'FIELD_LAYOUT',
    'FIELD_STAGING',
    'FIELD_SUPERVISION',
    'MATERIAL'
)
and is_deleted = false;

insert into estimate.cost_element (
    cost_element_id, code, name, description,
    is_active, created_at_utc, updated_at_utc, is_deleted
)
values
('00000000-0000-0000-0000-000000000501', 'FIELD_LABOR', 'Field Labor', 'Field labor, travel, and field expenses.', true, now(), now(), false),
('00000000-0000-0000-0000-000000000502', 'FAB_LABOR', 'Fabrication Labor', 'Shop and fabrication labor.', true, now(), now(), false),
('00000000-0000-0000-0000-000000000503', 'OFFICE_LABOR', 'Office Labor', 'PM, estimating, and management labor.', true, now(), now(), false);

update estimate.cost_rate
set code = 'INSTALLATION',
    name = 'Installation',
    description = 'Installation labor hours.',
    cost_element_id = '00000000-0000-0000-0000-000000000501',
    rate_unit = 'HOUR',
    is_active = true,
    is_deleted = false,
    updated_at_utc = now()
where cost_rate_id = '00000000-0000-0000-0000-000000000201';

insert into estimate.cost_rate (
    cost_rate_id, cost_element_id, code, name, description, rate_amount, rate_unit,
    is_active, created_at_utc, updated_at_utc, is_deleted
)
values
-- Field Labor
('00000000-0000-0000-0000-000000000601', '00000000-0000-0000-0000-000000000501', 'DRIVE_TIME', 'Drive Time', 'Drive time hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000603', '00000000-0000-0000-0000-000000000501', 'LAYOUT', 'Layout', 'Layout hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000604', '00000000-0000-0000-0000-000000000501', 'STAGING', 'Staging', 'Staging hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000605', '00000000-0000-0000-0000-000000000501', 'FIELD_SUPERVISION', 'Field Supervision', 'Field supervision hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000606', '00000000-0000-0000-0000-000000000501', 'HOTEL', 'Hotel', 'Hotel nights.', 0.0000, 'NIGHT', true, now(), now(), false),
('00000000-0000-0000-0000-000000000607', '00000000-0000-0000-0000-000000000501', 'PER_DIEM', 'Per Diem', 'Per diem days.', 0.0000, 'DAY', true, now(), now(), false),
('00000000-0000-0000-0000-000000000608', '00000000-0000-0000-0000-000000000501', 'OTHER_FIELD_LABOR', 'Other Field Labor', 'Other field labor hours.', 0.0000, 'HOUR', true, now(), now(), false),

-- Fabrication Labor
('00000000-0000-0000-0000-000000000621', '00000000-0000-0000-0000-000000000502', 'FAB_SUPERVISION', 'Fabrication Supervision', 'Fabrication supervision hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000622', '00000000-0000-0000-0000-000000000502', 'FABRICATION', 'Fabrication', 'Fabrication hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000623', '00000000-0000-0000-0000-000000000502', 'CUTTING', 'Cutting', 'Cutting hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000624', '00000000-0000-0000-0000-000000000502', 'OTHER_FAB_LABOR', 'Other Fab Labor', 'Other fabrication labor hours.', 0.0000, 'HOUR', true, now(), now(), false),

-- Office Labor
('00000000-0000-0000-0000-000000000641', '00000000-0000-0000-0000-000000000503', 'PM', 'Project Management', 'Project management hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000642', '00000000-0000-0000-0000-000000000503', 'SENIOR_PM', 'Senior PM', 'Senior project management hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000643', '00000000-0000-0000-0000-000000000503', 'ESTIMATOR', 'Estimator', 'Estimator hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000644', '00000000-0000-0000-0000-000000000503', 'MANAGEMENT', 'Management', 'Management hours.', 0.0000, 'HOUR', true, now(), now(), false),
('00000000-0000-0000-0000-000000000645', '00000000-0000-0000-0000-000000000503', 'OTHER_OFFICE_LABOR', 'Other Office Labor', 'Other office labor hours.', 0.0000, 'HOUR', true, now(), now(), false);

update estimate.cost_rate
set cost_element_id = '00000000-0000-0000-0000-000000000501'
where cost_element_id is null;

alter table estimate.cost_rate
    alter column cost_element_id set not null;