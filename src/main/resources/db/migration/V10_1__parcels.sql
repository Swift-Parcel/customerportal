CREATE TABLE parcel
(
    id                 BIGSERIAL    PRIMARY KEY,
    tracking_number    VARCHAR(11)  NOT NULL UNIQUE,
    pickup_request_id  BIGINT       UNIQUE REFERENCES pickup_request (id) ON DELETE SET NULL,
    customer_id        BIGINT       NOT NULL REFERENCES customer (id) ON DELETE CASCADE,
    status             VARCHAR(30)  NOT NULL,
    status_updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT parcel_status_valid CHECK (status IN (
                                                     'PENDING_PICKUP', 'PICKED_UP', 'IN_TRANSIT', 'OUT_FOR_DELIVERY',
                                                     'DELIVERED', 'DELIVERY_ATTEMPT_FAILED', 'LOST', 'DAMAGED'
        ))
);
