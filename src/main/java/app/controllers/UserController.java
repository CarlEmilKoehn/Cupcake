package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

public class UserController {

    public static void addRoutes(Javalin app) {
        app.get("/register", ctx -> ctx.render("register.html"));
        app.post("/register", ctx -> {
            try {handleRegister(ctx);} catch (Exception e) {ctx.status(500).result("Server error");}
        });

        app.get("/login", ctx -> ctx.render("login.html"));
        app.post("/login", ctx -> {
            try {handleLogin(ctx);} catch (Exception e) {ctx.status(500).result("Server error");}
        });

        app.get("/logout", ctx -> ctx.render("login.html"));
        app.post("/logout", UserController::logout);
    }

    private static void handleRegister(Context ctx) throws DatabaseException {

        String email = ctx.formParam("email");
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (email == null || username == null || password == null || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return;
        }

        //hashing password
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
        UserMapper.registerUser(email, username, hashed);
        ctx.redirect("/");
    }

    private static void handleLogin(Context ctx) throws DatabaseException{

        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return;
        }

        String storedPassword = UserMapper.getPasswordByEmail(email);

        if (storedPassword != null && BCrypt.checkpw(password, storedPassword)) {
            ctx.sessionAttribute("currentUser", email);
                ctx.render("homepage.html");
        } else {
            //TODO tell the user that it was the wrong login.

        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}
