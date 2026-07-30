CREATE TABLE notification_preference (
                                        id                     BIGSERIAL     PRIMARY KEY,
                                        customer_id            BIGINT        NOT NULL REFERENCES customer (id) ON DELETE CASCADE,
                                        parcel_status          BOOLEAN       DEFAULT TRUE,
                                        delivery_status        BOOLEAN       DEFAULT TRUE,
                                        case_status            BOOLEAN       DEFAULT TRUE,
                                        delivery_change        BOOLEAN       DEFAULT TRUE,
                                        pickup_confirmed       BOOLEAN       DEFAULT TRUE,
                                        quote_expiring         BOOLEAN       DEFAULT TRUE
);