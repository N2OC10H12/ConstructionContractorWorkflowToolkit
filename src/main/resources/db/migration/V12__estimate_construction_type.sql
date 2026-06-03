alter table estimate.bid
    add column construction_type varchar(50)
    not null
    default 'NEW_CONSTRUCTION';