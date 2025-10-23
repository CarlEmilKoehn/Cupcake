package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderMapper {

    public static List<Order> getAllOrdersByEmail(String email) throws DatabaseException {

        List<Order> orders = new ArrayList<>();

        String sql = "SELECT public.\"order\".id           AS order_id,\n" +
                "       public.\"order\".email," +
                "       public.\"order\".date," +
                "       public.order_holder.order_holder_id," +
                "       public.order_holder.cupcake_id," +
                "       public.order_holder.quantity," +
                "       public.cupcake.cupcake_price " +
                "FROM public.\"order\" " +
                "JOIN public.order_holder ON public.order_holder.order_id = public.\"order\".id" +
                "JOIN public.cupcake     ON public.cupcake.id = public.order_holder.cupcake_id" +
                "WHERE public.\"order\".email = ?;";


        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, email);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    int orderId = rs.getInt("order_id");
                    String customerMail = rs.getString("email");
                    Date purchaseDate = rs.getDate("date");
                    int orderHolderId = rs.getInt("order_holder_id");
                    int cupcakeId = rs.getInt("cupcake_id");
                    int cupcakePrice = rs.getInt("cupcake_price");
                    int cupcakeAmount = rs.getInt("quantity");

                    orders.add(new Order(orderId, customerMail, purchaseDate, orderHolderId, cupcakeId, cupcakePrice , cupcakeAmount));
                }

                return orders;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }
}
