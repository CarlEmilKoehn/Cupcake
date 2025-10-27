INSERT INTO public.bottom (name, price) VALUES
                                            ('Chocolate', 5),
                                            ('Vanilla', 5),
                                            ('Nutmeg', 5),
                                            ('Pistachio', 6),
                                            ('Almond', 7);

INSERT INTO public.topping (name, price) VALUES
                                             ('Chocolate', 5),
                                             ('Blueberry', 5),
                                             ('Raspberry', 5),
                                             ('Crispy', 6),
                                             ('Strawberry', 6),
                                             ('Rum/Raisin', 7),
                                             ('Orange', 8),
                                             ('Lemon', 8),
                                             ('Blue cheese', 9);

INSERT INTO public.cupcake (topping_id, bottom_id, cupcake_price)
SELECT
    t.id AS topping_id,
    b.id AS bottom_id,
    t.price + b.price AS cupcake_price
FROM public.topping t
         CROSS JOIN public.bottom b;
