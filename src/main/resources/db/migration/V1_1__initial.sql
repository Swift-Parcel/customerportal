CREATE TABLE public.address
(
    id           BIGSERIAL PRIMARY KEY,
    city         VARCHAR(255) NOT NULL,
    postal_code  VARCHAR(255) NOT NULL,
    country_code VARCHAR(2)   NOT NULL
);

CREATE TABLE public.customer
(
    id                 BIGSERIAL PRIMARY KEY,
    email              VARCHAR(255) UNIQUE NOT NULL,
    full_name          VARCHAR(150)        NOT NULL,
    phone_number       VARCHAR(30)         NOT NULL,
    password_hash      VARCHAR(60)         NOT NULL,
    preferred_language VARCHAR(10),
    default_address_id BIGINT,
    CONSTRAINT fk_customer_address FOREIGN KEY (default_address_id) REFERENCES public.address (id)
);