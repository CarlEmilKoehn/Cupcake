package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AdminController {



    public static void addRoutes(Javalin app) {

        app.before("/admin", AdminController::handleGuard);
        app.before("/admin/*", AdminController::handleGuard);


        app.get("/admin", ctx -> {
            ctx.attribute("customers", UserMapper.getAllUsers()); // din filtrerende metode
            ctx.render("homepageAdmin.html");
        });

        app.post("/admin/balance/insert", AdminController::HandlePlussingBalance);
    }

    private static void HandlePlussingBalance(Context ctx) throws DatabaseException  {
        String email = ctx.formParam("email");
        int amount = Integer.parseInt(ctx.formParam("amount"));
        UserMapper.plusBalanceByEmail(email, amount);

        ctx.redirect("/admin");
    }

    private static void handleGuard(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null || !"admin".equalsIgnoreCase(user.getRole().trim())) {
            ctx.redirect("/login");
        }
    }
}
