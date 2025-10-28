package app.controllers;

import app.entities.Cupcake;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

    public static void addRouting (Javalin app) {

        app.get("/viewOrder", ctx -> ctx.render("viewOrder.html"));
        app.post("/viewOrder", ctx -> {

            List<Order> orders = OrderMapper.getAllOrdersByEmail(ctx.attribute("currentUser"));
            ctx.attribute("orders", orders);
        });

    }

   public static Order createFullOrder (Context ctx) throws DatabaseException {

       int toppingId = Integer.parseInt(ctx.formParam("topping_id"));
       int bottomId = Integer.parseInt(ctx.formParam("bottom_id"));
       int amount = Integer.parseInt(ctx.formParam("amount"));
       int orderId


       Cupcake cupcake = CupcakeMapper.getCupcakeFromToppingBottomId(toppingId, bottomId);

       return new Order(order)

   }

   public static void newOrder(Context ctx) throws DatabaseException {

   }

}
