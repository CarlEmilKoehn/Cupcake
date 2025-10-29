package app.controllers;

import app.entities.Cupcake;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OrderController {

    public static void addRouting(Javalin app) {
        app.get("/viewOrder", OrderController::showOrders);
        app.post("/createOrder", OrderController::createFullOrder);
    }

    private static void showOrders(Context ctx) throws DatabaseException {
        String userEmail = ctx.sessionAttribute("currentUser");

        if (userEmail == null) {
            ctx.redirect("/login");
            return;
        }

        List<Order> orders = OrderMapper.getAllOrdersByEmail(userEmail);
        ctx.attribute("orders", orders);
        ctx.render("viewOrder.html");
    }

    private static void createFullOrder(@NotNull Context ctx) throws DatabaseException {
        String userEmail = ctx.sessionAttribute("currentUser");

        if (userEmail == null) {
            ctx.status(401).result("You must be logged in to place an order.");
            return;
        }

        // Parse JSON body
        Map<String, Object> body = ctx.bodyAsClass(Map.class);
        List<Map<String, Object>> cupcakes = (List<Map<String, Object>>) body.get("cupcakes");

        if (cupcakes == null || cupcakes.isEmpty()) {
            ctx.status(400).result("No cupcakes in the order.");
            return;
        }

        // Create the order (main table)
        Date date = Date.valueOf(LocalDate.now());
        OrderMapper.createOrder(userEmail, date);
        int orderId = OrderMapper.getLastOrderId(userEmail);

        // Insert all cupcakes into order_holder
        for (Map<String, Object> item : cupcakes) {
            int toppingId = (int) item.get("toppingId");
            int bottomId = (int) item.get("bottomId");
            int amount = (int) item.get("amount");

            Cupcake cupcake = CupcakeMapper.getCupcakeFromToppingBottomId(toppingId, bottomId);
            if (cupcake == null) continue;

            OrderMapper.putInOrderHolder(orderId, cupcake.getCupcakeId(), amount);
        }

        ctx.result("Order successfully placed! Order ID: " + orderId);
    }
}
