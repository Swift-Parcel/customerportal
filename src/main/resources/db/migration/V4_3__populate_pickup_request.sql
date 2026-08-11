INSERT INTO public.pickup_request (
    customer_id, status, sender_address_id, recipient_name, recipient_address_id,
    weight_kg, width_cm, length_cm, height_cm, service_type, declared_value_eur,
    preferred_pickup_date, preferred_time_slot
) VALUES

      (1, 'DRAFT', 1, 'Anna Kovács', 1, 12.5, 25, 40, 30, 'STANDARD', 250.00, '2026-08-12', 'AFTERNOON'),

      (1, 'DRAFT', 1, 'Lukas Huber', 2, 8.0, 20, 30, 20, 'EXPRESS', 100.00, '2026-08-13', 'MORNING'),

      (1, 'DRAFT', 1, 'Marek Wójcik', 3, 3.0, 15, 20, 10, 'STANDARD', 50.00, '2026-08-14', 'EVENING');