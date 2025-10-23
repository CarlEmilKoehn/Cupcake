package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static void registerUser(String userEmail, String username, String passwordHash) throws DatabaseException {

        String sql = "INSERT INTO user (userEmail, name, password, role, balance) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, userEmail);
            stmt.setString(2, username);
            stmt.setString(3, passwordHash);

            stmt.setString(4, "customer");
            stmt.setBigDecimal(5, BigDecimal.ZERO);

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

    public static BigDecimal getBalanceByEmail(String email) throws DatabaseException {

        String sql = "SELECT balance FROM user WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("balance");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    public static void plusBalanceByEmail(String email, BigDecimal plusValue) throws DatabaseException {

        String sql = "UPDATE user SET balance = balance + ? WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            BigDecimal oldBalance = getBalanceByEmail(email);
            BigDecimal newBalance = oldBalance.add(plusValue);

            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static void minusBalanceByEmail(String email, BigDecimal minusValue) throws DatabaseException {

        String sql = "UPDATE user SET balance = balance - ? WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            BigDecimal oldBalance = getBalanceByEmail(email);
            BigDecimal newBalance = oldBalance.subtract(minusValue);

            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

}
