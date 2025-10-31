package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor

public class User {

    private String email;
    private String username;
    private String password;
    private String role;
    private int balance;

    @Override
    public String toString() {
        return email + " | " + username + " | " + role;
    }
}
