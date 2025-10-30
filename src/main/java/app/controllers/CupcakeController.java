package app.controllers;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.BottomMapper;
import app.persistence.CupcakeMapper;
import app.persistence.ToppingMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app) {

        app.get("/homepage", ctx -> {
            showToppingsAndBottoms(ctx);
            ctx.render("homepage.html");
        });

        // CupcakeController.addRoutes(...)
        app.get("/api/cupcakes/resolve", ctx -> {
            int toppingId = Integer.parseInt(ctx.queryParam("topping_id"));
            int bottomId  = Integer.parseInt(ctx.queryParam("bottom_id"));
            Integer cupcakeId = CupcakeMapper.getCupcakeId(toppingId, bottomId);
            if (cupcakeId == null) { ctx.status(404).json(java.util.Map.of("message","Not found")); return; }
            int price = CupcakeMapper.getCupcakePriceById(cupcakeId);
            ctx.json(java.util.Map.of("cupcakeId", cupcakeId, "price", price));
        });



// Render the payment page
        app.get("/payment", ctx -> ctx.render("payment.html"));


    }

    private static void showToppingsAndBottoms(Context ctx) throws DatabaseException {
        List<Topping> toppingList = ToppingMapper.getAllToppings();
        List<Bottom> bottomList = BottomMapper.getAllBottoms();

        ctx.attribute("toppingList", toppingList);
        ctx.attribute("bottomList", bottomList);
    }

}
