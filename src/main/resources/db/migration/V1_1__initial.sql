CREATE TABLE public.customer
(
    email           VARCHAR(255)    UNIQUE  NOT NULL,
    full_name       VARCHAR(150)    NOT NULL,
    phone_number    VARCHAR(30)     NOT NULL,
    password_hash   VARCHAR(60)     NOT NULL,
    PRIMARY KEY (email)
)