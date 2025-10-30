package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static void registerUser(String userEmail, String username, String passwordHash) throws DatabaseException {
        String sql = "INSERT INTO public.\"user\" (email, name, password, role, \"balance\") VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
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

        String sql = "SELECT password FROM public.\"user\" WHERE email = ?";

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

    public static int getBalanceByEmail(String email) throws DatabaseException {

        String sql = "SELECT balance FROM public.\"user\" WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("balance");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }

        return 0;
    }

    public static void plusBalanceByEmail(String email, int plusValue) throws DatabaseException {

        String sql = "UPDATE public.\"user\" SET balance = balance + ? WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            int oldBalance = getBalanceByEmail(email);
            int newBalance = oldBalance + plusValue;

            stmt.setInt(1, newBalance);
            stmt.setString(2, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static void minusBalanceByEmail(String email, int minusValue) throws DatabaseException {

        String sql = "UPDATE public.\"user\" SET balance = balance - ? WHERE email = ?";

        try(Connection connection = ConnectionPool.instance.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            int oldBalance = getBalanceByEmail(email);
            int newBalance = oldBalance - minusValue;

            stmt.setInt(1, newBalance);
            stmt.setString(2, email);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static boolean debitBalance(String email, int amount) throws DatabaseException {
        String sql =
                "UPDATE public.\"user\" " +
                        "SET balance = balance - ? " +
                        "WHERE email = ? AND balance >= ? " +
                        "RETURNING balance";
        try (var con = ConnectionPool.getInstance().getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setString(2, email);
            ps.setInt(3, amount);
            try (var rs = ps.executeQuery()) {
                return rs.next(); // succeeds only if there was enough balance
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }



    public static boolean getEmailExists(String email) throws DatabaseException {

        String sql = "SELECT email FROM public.\"user\" WHERE email = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);


            try {
               stmt.setString(1, email);

               ResultSet rs = stmt.executeQuery();

               if (rs.next()) {

                   return rs.getString("email").equals(email);
               }

            } catch (Exception e) {
                return false;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
        return false;
    }

    public static User getUserByEmail(String email) throws DatabaseException {

        User user;

        String sql = "SELECT * FROM public.\"user\" WHERE email = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String username = rs.getString("name");
                String password = rs.getString("password");
                String role = rs.getString("role");
                int balance = rs.getInt("balance");

                user = new User(email, username, password, role, balance);

                return user;
            }

            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}
