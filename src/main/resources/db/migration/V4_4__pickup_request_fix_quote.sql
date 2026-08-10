ALTER TABLE pickup_request ADD COLUMN accepted_quote_id BIGINT;
ALTER TABLE pickup_request DROP COLUMN quoted_price_eur;
ALTER TABLE pickup_request DROP COLUMN quote_expires_at;
ALTER TABLE pickup_request ALTER COLUMN status TYPE VARCHAR(32);
ALTER TABLE pickup_request ADD COLUMN created_at TIMESTAMPTZ;