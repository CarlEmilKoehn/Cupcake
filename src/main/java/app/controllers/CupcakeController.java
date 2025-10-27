package app.controllers;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.BottomMapper;
import app.persistence.ToppingMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app) {

        app.get("/homepage", ctx -> {
            showToppings(ctx);
            showBottoms(ctx);
            ctx.render("homepage.html");
        });

        app.post("cupcake/order", CupcakeController::handleOrder);

    }

    private static void showToppings(Context ctx) throws DatabaseException {

        List<Topping> toppingList = ToppingMapper.getAllToppings();
        ctx.attribute("toppingList", toppingList);

    }

    private static void showBottoms(Context ctx) throws DatabaseException {

        List<Bottom> bottomList = BottomMapper.getAllBottoms();
        ctx.attribute("bottomList", bottomList);

    }

    private static void handleOrder(Context ctx) throws DatabaseException {
        int toppingId = Integer.parseInt(ctx.formParam("topping_id"));
        int bottomId = Integer.parseInt(ctx.formParam("bottom_id"));
        int amount = Integer.parseInt(ctx.formParam("amount"));

        ctx.result("Order placed: Topping: " + toppingId + ", Bottom: " + bottomId + ", Amount: " + amount);
    }



}
