BEGIN;

CREATE TABLE IF NOT EXISTS public.bottom
(
    id serial NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    price integer NOT NULL,
    CONSTRAINT bottom_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.cupcake
(
    id serial NOT NULL,
    topping_id integer NOT NULL,
    bottom_id integer NOT NULL,
    cupcake_price integer NOT NULL,
    CONSTRAINT cupcake_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public."order"
(
    id serial NOT NULL,
    email character varying COLLATE pg_catalog."default" NOT NULL,
    date date NOT NULL,
    CONSTRAINT order_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.order_holder
(
    order_holder_id serial NOT NULL,
    order_id integer NOT NULL,
    cupcake_id integer NOT NULL,
    quantity integer NOT NULL DEFAULT 1,
    CONSTRAINT order_holder_pkey PRIMARY KEY (order_holder_id)
    );

CREATE TABLE IF NOT EXISTS public.topping
(
    id serial NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    price integer NOT NULL,
    CONSTRAINT topping_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public."user"
(
    email character varying COLLATE pg_catalog."default" NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    role character varying COLLATE pg_catalog."default" NOT NULL,
    "balance" integer NOT NULL,
    CONSTRAINT user_pkey PRIMARY KEY (email)
    );

ALTER TABLE IF EXISTS public.cupcake
    ADD CONSTRAINT cupcake_bottom_id_fkey FOREIGN KEY (bottom_id)
    REFERENCES public.bottom (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.cupcake
    ADD CONSTRAINT cupcake_topping_id_fkey FOREIGN KEY (topping_id)
    REFERENCES public.topping (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public."order"
    ADD CONSTRAINT order_email_fkey FOREIGN KEY (email)
    REFERENCES public."user" (email) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;


ALTER TABLE IF EXISTS public.order_holder
    ADD CONSTRAINT order_holder_order_id_fkey FOREIGN KEY (order_id)
    REFERENCES public."order" (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;

END;
