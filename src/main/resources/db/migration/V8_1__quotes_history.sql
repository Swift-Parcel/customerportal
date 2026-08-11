CREATE TABLE quote (
                       id                  BIGSERIAL     PRIMARY KEY,
                       pickup_request_id   BIGINT        NOT NULL REFERENCES pickup_request (id) ON DELETE CASCADE,
                       base_price_eur      NUMERIC(10,2) NOT NULL,
                       weight_charge_eur   NUMERIC(10,2) NOT NULL,
                       surcharge_eur       NUMERIC(10,2) NOT NULL,
                       zone_adjustment_eur NUMERIC(10,2) NOT NULL,
                       total_price_eur     NUMERIC(10,2) NOT NULL,
                       quote_route_type    VARCHAR(32)   NOT NULL,
                       quoted_at           TIMESTAMPTZ   NOT NULL DEFAULT now(),
                       quote_expires_at    TIMESTAMPTZ   NOT NULL
);
--