package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {



    public static void addRoutes(Javalin app) {

        app.before("/admin", AdminController::handleGuard);
        app.before("/admin/*", AdminController::handleGuard);


        app.get("/admin", ctx -> {
            ctx.attribute("customers", UserMapper.getAllUsers());
            ctx.render("homepageAdmin.html");
        });

        app.post("/admin/balance/insert", AdminController::handleAddingBalance);

        //--------------------------------------------------------------------------------------------------------------

        app.get("/adminOrders", ctx -> {
            ctx.attribute("orders", OrderMapper.getAllOrders());
        });
    }

    private static void handleAddingBalance(Context ctx) throws DatabaseException  {
        String email = ctx.formParam("email");
        int amount = Integer.parseInt(ctx.formParam("amount"));
        UserMapper.plusBalanceByEmail(email, amount);

        ctx.redirect("/admin");
    }

    private static void handleNegatingBalance(Context ctx) throws DatabaseException  {
        String email = ctx.formParam("email");
        int amount = Integer.parseInt(ctx.formParam("amount"));
        UserMapper.minusBalanceByEmail(email, amount);

        ctx.redirect("/admin");
    }

    private static void handleGuard(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null || !"admin".equalsIgnoreCase(user.getRole().trim())) {
            ctx.redirect("/login");
        }
    }
}
