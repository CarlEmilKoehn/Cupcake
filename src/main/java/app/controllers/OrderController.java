package app.controllers;

import app.entities.Order;
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

        app.get("/payment", ctx -> ctx.render("payment.html"));
    }
}
