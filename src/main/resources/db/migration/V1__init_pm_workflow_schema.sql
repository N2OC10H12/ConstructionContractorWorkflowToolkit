-- Enable UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- =========================
-- WORKFLOW TEMPLATE
-- =========================
CREATE TABLE workflow_template (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    is_default BOOLEAN NOT NULL
);

CREATE TABLE workflow_template_step (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id UUID NOT NULL REFERENCES workflow_template(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL
);

CREATE TABLE workflow_template_substep (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    step_id UUID NOT NULL REFERENCES workflow_template_step(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL
);

-- =========================
-- PROJECT
-- =========================
CREATE TABLE project (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    owner_user_id UUID NOT NULL REFERENCES users(id),
    planning_deadline DATE,
    project_deadline DATE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE project_step (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    template_step_id UUID,
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    deadline DATE,
    assigned_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE project_substep (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    step_id UUID NOT NULL REFERENCES project_step(id) ON DELETE CASCADE,
    template_substep_id UUID,
    name VARCHAR(255) NOT NULL,
    order_index INT NOT NULL,
    is_done BOOLEAN NOT NULL DEFAULT FALSE,
    assigned_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- =========================
-- FILES & NOTES
-- =========================
CREATE TABLE substep_file (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    substep_id UUID NOT NULL REFERENCES project_substep(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR NOT NULL,
    uploaded_by UUID NOT NULL REFERENCES users(id),
    uploaded_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE substep_note (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    substep_id UUID NOT NULL REFERENCES project_substep(id) ON DELETE CASCADE,
    note_text TEXT NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- =========================
-- AUDIT LOG
-- =========================
CREATE TABLE project_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    actor_user_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(50),
    target_id UUID,
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- =========================
-- INDEXES
-- =========================
CREATE INDEX idx_project_owner ON project(owner_user_id);
CREATE INDEX idx_project_name ON project(name);

CREATE INDEX idx_step_project ON project_step(project_id);
CREATE INDEX idx_substep_step ON project_substep(step_id);

CREATE INDEX idx_file_substep ON substep_file(substep_id);
CREATE INDEX idx_note_substep ON substep_note(substep_id);

CREATE INDEX idx_audit_project ON project_audit_log(project_id);