CREATE TABLE estimate.bid_revision_item_quote (
    bid_revision_item_quote_id UUID NOT NULL,
    bid_revision_item_id UUID NOT NULL,
    stored_file_id UUID NOT NULL,

    description VARCHAR(500),
    display_order INTEGER NOT NULL,

    created_at_utc TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by_user_id UUID NOT NULL,

    CONSTRAINT pk_bid_revision_item_quote
        PRIMARY KEY (bid_revision_item_quote_id),

    CONSTRAINT fk_bid_revision_item_quote_item
        FOREIGN KEY (bid_revision_item_id)
        REFERENCES estimate.bid_revision_item (bid_revision_item_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_bid_revision_item_quote_stored_file
        FOREIGN KEY (stored_file_id)
        REFERENCES public.stored_file (stored_file_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_bid_revision_item_quote_created_by
        FOREIGN KEY (created_by_user_id)
        REFERENCES public.users (id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_bid_revision_item_quote_display_order
        CHECK (display_order > 0)
);

CREATE INDEX idx_bid_revision_item_quote_item_order
    ON estimate.bid_revision_item_quote (
        bid_revision_item_id,
        display_order
    );

CREATE INDEX idx_bid_revision_item_quote_stored_file
    ON estimate.bid_revision_item_quote (stored_file_id);

CREATE INDEX idx_bid_revision_item_quote_created_by
    ON estimate.bid_revision_item_quote (created_by_user_id);