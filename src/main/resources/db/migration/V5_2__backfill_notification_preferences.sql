INSERT INTO public.notification_preference ( customer_id, parcel_status, delivery_status, case_status, delivery_change, pickup_confirmed, quote_expiring )
SELECT customer.id, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE
FROM public.customer customer
WHERE NOT EXISTS ( SELECT 1
                   FROM public.notification_preference notification_preference
                   WHERE notification_preference.customer_id = customer.id );
