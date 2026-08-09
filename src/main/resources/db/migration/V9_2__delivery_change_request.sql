CREATE TABLE delivery_change_request (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     BIGINT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    tracking_number VARCHAR(11) NOT NULL,
    case_number     VARCHAR(32),
    requested_date  DATE,
    requested_slot  VARCHAR(16),
    status          VARCHAR(20) NOT NULL DEFAULT 'REQUESTED'
        CHECK (status IN ('REQUESTED', 'PENDING_REVIEW', 'APPROVED', 'REJECTED')),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
