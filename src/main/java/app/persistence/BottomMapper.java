package app.persistence;

import app.entities.Bottom;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BottomMapper {

    public static List<Bottom> getAllBottoms() {

        List<Bottom> bottomList = new ArrayList<>();

        String sql = "SELECT * FROM public.bottom";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {

                int bottomId = rs.getInt("id");
                String name = rs.getString("name");
                int price = rs.getInt("price");

                bottomList.add(new Bottom(bottomId, name, price));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return bottomList;

    }

    public static Bottom getBottomFromId(int id) throws DatabaseException {
        String sql = "SELECT name, price FROM bottom WHERE id = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id); // ✅ set the parameter first

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int price = rs.getInt("price");
                    return new Bottom(id, name, price);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: " + e.getMessage());
        }

        return null;
    }




}
