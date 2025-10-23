package app.controllers;

import app.entities.Bottom;
import app.entities.Topping;
import app.exceptions.DatabaseException;
import app.persistence.BottomMapper;
import app.persistence.ToppingMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.List;

public class CupcakeController {

    public static void addRoutes(Javalin app) {

        app.get("/homepage", ctx -> showToppings(ctx));
        app.get("/homepage", ctx -> showBottoms(ctx));

    }

    private static void showToppings(Context ctx) throws DatabaseException {

            List<Topping> toppingList = ToppingMapper.getAllToppings();
            ctx.attribute("toppingList", toppingList);
            //ctx.render() dropdown menu

    }

    private static void showBottoms(Context ctx) {

            List<Bottom> bottomList = BottomMapper.getAllBottoms();
            ctx.attribute("bottomList", bottomList);
            //ctx.render() dropdown menu

    }



}
