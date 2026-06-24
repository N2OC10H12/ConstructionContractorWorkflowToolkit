CREATE TABLE estimate.estimate_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    bid_id UUID,
    revision_id UUID,

    actor_user_id UUID NOT NULL REFERENCES users(id),

    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100),
    target_id UUID,

    old_value TEXT,
    new_value TEXT,

    message TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_estimate_audit_bid
    ON estimate.estimate_audit_log(bid_id);

CREATE INDEX idx_estimate_audit_revision
    ON estimate.estimate_audit_log(revision_id);

CREATE INDEX idx_estimate_audit_target
    ON estimate.estimate_audit_log(target_type, target_id);

CREATE INDEX idx_estimate_audit_actor
    ON estimate.estimate_audit_log(actor_user_id);

CREATE INDEX idx_estimate_audit_created_at
    ON estimate.estimate_audit_log(created_at);