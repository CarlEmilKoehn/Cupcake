package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderMapper {

    public static List<Order> getAllOrdersByEmail(String email) throws DatabaseException {
        List<Order> orders = new ArrayList<>();

        String sql = "SELECT o.id AS order_id, o.email, o.date, " +
                "oh.order_holder_id, oh.cupcake_id, oh.quantity, c.cupcake_price " +
                "FROM public.\"order\" o " +
                "JOIN public.order_holder oh ON oh.order_id = o.id " +
                "JOIN public.cupcake c ON c.id = oh.cupcake_id " +
                "WHERE o.email = ?;";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String customerMail = rs.getString("email");
                    Date purchaseDate = rs.getDate("date");
                    int orderHolderId = rs.getInt("order_holder_id");
                    int cupcakeId = rs.getInt("cupcake_id");
                    int cupcakePrice = rs.getInt("cupcake_price");
                    int cupcakeAmount = rs.getInt("quantity");

                    orders.add(new Order(orderId, customerMail, purchaseDate,
                            orderHolderId, cupcakeId, cupcakePrice, cupcakeAmount));
                }
            }

            return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not get orders by email", e.getMessage());
        }
    }


    public static int createOrder(String email, List<Integer> cupcakeIds, List<Integer> quantities) throws DatabaseException {
        String insertOrder = "INSERT INTO public.\"order\" (email, date) VALUES (?, CURRENT_DATE) RETURNING id";
        String insertHolder = "INSERT INTO public.order_holder (order_id, cupcake_id, quantity) VALUES (?, ?, ?)";

        try (Connection con = ConnectionPool.getInstance().getConnection()) {
            con.setAutoCommit(false);
            int orderId;

            try (PreparedStatement ps = con.prepareStatement(insertOrder)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("No order id returned");
                    orderId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = con.prepareStatement(insertHolder)) {
                for (int i = 0; i < cupcakeIds.size(); i++) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, cupcakeIds.get(i));
                    ps.setInt(3, quantities.get(i));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            con.setAutoCommit(true);
            return orderId;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }



}
