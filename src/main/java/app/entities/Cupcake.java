package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Cupcake {

    private int cupcakeId;
    private Topping topping;
    private Bottom bottom;
    private int price;

    @Override
    public String toString() {
        return topping.getName() + bottom.getName();
    }

}