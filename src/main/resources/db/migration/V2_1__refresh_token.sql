CREATE TABLE refresh_token (
                               id             BIGSERIAL    PRIMARY KEY,
                               customer_email VARCHAR(255) NOT NULL REFERENCES customer(email) ON DELETE CASCADE,
                               token_hash     CHAR(64)     NOT NULL UNIQUE,
                               expires_at     TIMESTAMPTZ  NOT NULL,
                               revoked_at     TIMESTAMPTZ,
                               created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_token_customer
    ON refresh_token (customer_email) WHERE revoked_at  IS NULL;