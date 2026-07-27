INSERT INTO public.address (city, postal_code, country_code)
VALUES ('Budapest', '1051', 'HU'),
       ('Vienna', '1010', 'AT'),
       ('Kraków', '30001', 'PL');

INSERT INTO public.customer (email, full_name, phone_number, password_hash, preferred_language, default_address_id)
VALUES ('diego.santos@gmail.com', 'Diego Santos', '+36 30 123 4567', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHGC2', 'en', 1),
       ('anna.kovacs@example.com', 'Anna Kovács', '+36 30 987 6543', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHGC2', 'hu', 1),
       ('marek.wojcik@example.pl', 'Marek Wójcik', '+48 12 345 6789', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00dmxs.TVuHGC2', 'pl', 3);
