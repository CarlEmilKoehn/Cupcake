package app.controllers;

import app.entities.Order;
import app.entities.User;
import app.persistence.CupcakeMapper;
import app.persistence.OrderMapper;
import app.exceptions.DatabaseException;
import app.persistence.UserMapper;
import io.javalin.Javalin;

import java.util.List;

public class OrderController {

    public static void addRouting(Javalin app) {

        // Show order modal (HTML already loaded in homepage)
        app.get("/viewOrder", ctx -> ctx.render("viewOrder.html"));

        // GET /api/orders -> JSON for orderPopup.js
        app.get("/api/orders", ctx -> {
            User user = ctx.sessionAttribute("currentUser");
            if (user == null) { ctx.status(401).result("Please log in"); return; }
            List<Order> orders = OrderMapper.getAllOrdersByEmail(user.getEmail());
            ctx.json(orders);
        });

        // OrderController.addRouting(...)
        record Item(long cupcakeId, int quantity) {}
        record PayReq(String payMethod, Object delivery, int shippingCents, java.util.List<Item> items) {}

        app.post("/payment/submit", ctx -> {
            User user = ctx.sessionAttribute("currentUser");
            if (user == null) { ctx.status(401).result("Please log in"); return; }

            PayReq req = ctx.bodyAsClass(PayReq.class);
            if (req.items() == null || req.items().isEmpty()) { ctx.status(400).result("No items"); return; }

            // Validate & compute authoritative total from DB
            java.util.List<Integer> cupcakeIds = new java.util.ArrayList<>();
            java.util.List<Integer> quantities = new java.util.ArrayList<>();
            int itemsTotal = 0;

            for (Item it : req.items()) {
                if (it.cupcakeId() <= 0 || it.cupcakeId() > Integer.MAX_VALUE) {
                    ctx.status(400).result("Invalid cupcakeId"); return;
                }
                int id = (int) it.cupcakeId();
                int price = CupcakeMapper.getCupcakePriceById(id); // uses cupcake_price
                itemsTotal += price * it.quantity();
                cupcakeIds.add(id);
                quantities.add(it.quantity());
            }
            int shipping = Math.max(0, req.shippingCents());
            int grandTotal = itemsTotal + shipping;

            if ("coins".equalsIgnoreCase(req.payMethod())) {
                boolean ok = UserMapper.debitBalance(user.getEmail(), grandTotal);
                if (!ok) { ctx.status(400).result("Insufficient balance"); return; }
                user = UserMapper.getUserByEmail(user.getEmail()); // make sure this reads "Balance"
                ctx.sessionAttribute("currentUser", user);
            }

            int orderId = OrderMapper.createOrder(user.getEmail(), cupcakeIds, quantities);
            ctx.status(200).result("Order created successfully! ID: " + orderId);
        });



    }
}
