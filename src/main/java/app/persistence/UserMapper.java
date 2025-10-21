package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static void registerUser(String userEmail, String username, String passwordHash) throws DatabaseException {

        String sql = "INSERT INTO users (userEmail, name, password, role, balance) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, userEmail);
            stmt.setString(2, username);
            stmt.setString(3, passwordHash);

            stmt.setString(4, "customer");
            stmt.setInt(5, 0);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static String getPasswordByEmail(String email) throws DatabaseException {

        String sql = "SELECT password FROM user WHERE email = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }

        return null;
    }

}
