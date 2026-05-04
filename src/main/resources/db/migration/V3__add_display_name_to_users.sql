ALTER TABLE public.users
ADD COLUMN display_name VARCHAR(150);

UPDATE public.users
SET display_name = username
WHERE display_name IS NULL;

ALTER TABLE public.users
ALTER COLUMN display_name SET NOT NULL;