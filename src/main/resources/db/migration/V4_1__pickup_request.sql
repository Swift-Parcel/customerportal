CREATE TABLE pickup_request (
                                id                     BIGSERIAL     PRIMARY KEY,
                                customer_id            BIGINT        NOT NULL REFERENCES customer (id) ON DELETE CASCADE,
                                status                 VARCHAR(24)   NOT NULL DEFAULT 'DRAFT',
                                sender_address_id       BIGINT      NOT NULL REFERENCES address (id),
                                recipient_name         VARCHAR(150)  NOT NULL,
                                recipient_address_id    BIGINT      NOT NULL REFERENCES address (id),
                                weight_kg              FLOAT  NOT NULL,
                                length_cm              FLOAT  NOT NULL,
                                width_cm               FLOAT  NOT NULL,
                                height_cm              FLOAT  NOT NULL,
                                service_type           VARCHAR(16)   NOT NULL,
                                declared_value_eur     NUMERIC(10,2)  NOT NULL,
                                preferred_pickup_date  DATE          NOT NULL,
                                preferred_time_slot    VARCHAR(16)   NOT NULL,
                                quoted_price_eur       NUMERIC(10,2),
                                quote_expires_at       TIMESTAMPTZ,
                                paid_at                TIMESTAMPTZ,
                                submitted_at           TIMESTAMPTZ,
                                tracking_number        VARCHAR(11)   UNIQUE,
                                picked_up_at           TIMESTAMPTZ,
                                created_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
                                updated_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),
                                CONSTRAINT pickup_status_valid CHECK (status IN (
                                                                                 'DRAFT', 'QUOTED', 'CONFIRMED', 'SUBMITTED_TO_BACK_OFFICE',
                                                                                 'TRACKING_NUMBER_ASSIGNED', 'PICKED_UP', 'CANCELLED', 'EXPIRED'
                                    )),
                                CONSTRAINT pickup_service_type_valid CHECK (service_type IN (
                                                                                             'STANDARD', 'EXPRESS', 'SAME_DAY'
                                    )),
                                CONSTRAINT pickup_time_slot_valid CHECK (preferred_time_slot IN (
                                                                                                 'MORNING', 'AFTERNOON', 'EVENING'
                                    ))
);