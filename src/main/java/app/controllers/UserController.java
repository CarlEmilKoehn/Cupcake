package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

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

        String email = ctx.formParam("email").trim();
        String username = ctx.formParam("username").trim();
        String password = ctx.formParam("password").trim();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            ctx.attribute("registerError", "All fields are required");
            ctx.render("register.html");
            return;
        }

        if (username.length() < 4 || username.length() > 12 || password.length() < 4 || password.length() > 40) {
            ctx.attribute("registerError", "Username or password length invalid");
            ctx.render("register.html");
            return;
        }

        if (!Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email)) {
            ctx.attribute("registerError", "Invalid email format");
            ctx.render("register.html");
            return;
        }

        if (UserMapper.getEmailExists(email)) {
            ctx.attribute("registerError", "Error, user already exists");
            ctx.render("register.html");
            return;
        }

        //hashing password
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
        UserMapper.registerUser(email, username, hashed);
        ctx.redirect("/");
    }

    private static void handleLogin(Context ctx) throws DatabaseException{

        String email = ctx.formParam("email").trim();
        String password = ctx.formParam("password").trim();

        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        String storedPassword = UserMapper.getPasswordByEmail(email);

        if (storedPassword != null && BCrypt.checkpw(password, storedPassword)) {
            User user = UserMapper.getUserByEmail(email);
            ctx.sessionAttribute("currentUser", user);

            if (user != null && user.getRole().equalsIgnoreCase("admin")) {
                ctx.redirect("/admin");
                return;
            }

            ctx.redirect("/homepage");

        } else {
            ctx.attribute("loginError", "Error, wrong email or password");
            ctx.render("login.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }
}