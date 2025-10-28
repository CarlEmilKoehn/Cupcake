package app.persistence;

import app.entities.Cupcake;
import app.entities.Topping;
import app.entities.Bottom;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static app.persistence.BottomMapper.getBottomFromId;
import static app.persistence.ToppingMapper.getToppingFromId;

public class CupcakeMapper {

    public static Cupcake getCupcakeFromToppingBottomId(int toppingId, int bottomId) throws DatabaseException {
        String sql = "SELECT id, cupcake_price FROM cupcake WHERE topping_id = ? AND bottom_id = ?";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            stmt.setInt(1, toppingId);
            stmt.setInt(2, bottomId);

            if(rs.next()) {
                int cupcakeId = rs.getInt("id");
                int price = rs.getInt("cupcake_price");

                Topping topping = getToppingFromId(toppingId);
                Bottom bottom = getBottomFromId(bottomId);

                return new Cupcake(cupcakeId, topping, bottom, price);
            }

            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }

    }

}