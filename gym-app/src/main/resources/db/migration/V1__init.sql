CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE membership_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    billing_interval VARCHAR(20) NOT NULL,
    price_cents INTEGER NOT NULL CHECK (price_cents >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE memberships (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    plan_id BIGINT NOT NULL REFERENCES membership_plans(id),
    status VARCHAR(20) NOT NULL, -- ACTIVE, FROZEN, CANCELLED, EXPIRED
    start_date DATE NOT NULL,
    end_date DATE,
    frozen_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE class_templates (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    instructor_name VARCHAR(120) NOT NULL,
    day_of_week VARCHAR(15) NOT NULL,
    start_time TIME NOT NULL,
    duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0),
    default_capacity INTEGER NOT NULL CHECK (default_capacity > 0),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE class_instances (
    id BIGSERIAL PRIMARY KEY,
    class_template_id BIGINT REFERENCES class_templates(id) ON DELETE SET NULL,
    title VARCHAR(120) NOT NULL,
    instructor_name VARCHAR(120) NOT NULL,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, CANCELLED, COMPLETED
    CONSTRAINT chk_class_time_order CHECK (ends_at > starts_at)
);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    class_instance_id BIGINT NOT NULL REFERENCES class_instances(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL, -- CONFIRMED, WAITLISTED, CANCELLED, NO_SHOW
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP,
    CONSTRAINT uq_booking_member_instance UNIQUE (member_id, class_instance_id)
);

CREATE INDEX idx_memberships_member_id ON memberships(member_id);
CREATE INDEX idx_class_instances_starts_at ON class_instances(starts_at);
CREATE INDEX idx_bookings_class_instance_id ON bookings(class_instance_id);
CREATE INDEX idx_bookings_member_id ON bookings(member_id);