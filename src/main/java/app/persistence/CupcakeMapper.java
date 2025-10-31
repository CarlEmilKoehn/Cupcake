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

    public static Integer getCupcakeId(int toppingId, int bottomId) throws DatabaseException {
        String sql = "SELECT id FROM public.cupcake WHERE topping_id = ? AND bottom_id = ?";
        try (var con = ConnectionPool.getInstance().getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setInt(1, toppingId);
            ps.setInt(2, bottomId);
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

    public static int getCupcakePriceById(int cupcakeId) throws DatabaseException {
        String sql = "SELECT cupcake_price FROM public.cupcake WHERE id = ?";
        try (var con = ConnectionPool.getInstance().getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setInt(1, cupcakeId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cupcake_price");
            }
            throw new DatabaseException("Cupcake not found", "id=" + cupcakeId);
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }

}