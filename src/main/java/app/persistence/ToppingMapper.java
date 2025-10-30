package app.persistence;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.entities.Topping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToppingMapper {

    public static List<Topping> getAllToppings() throws DatabaseException {

        List<Topping> toppingList = new ArrayList<>();

        String sql = "SELECT * FROM public.topping";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {

                int toppingId = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");

                toppingList.add(new Topping(toppingId, name, price));

            }

            return toppingList;

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }




    }

    public static Topping getToppingFromId(int id) throws DatabaseException {
        String sql = "SELECT name, price FROM topping WHERE id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    return new Topping(id, name, price);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: " + e.getMessage());
        }

        return null;
    }



}
