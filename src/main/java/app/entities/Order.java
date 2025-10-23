package app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Date;

@Data
@AllArgsConstructor

public class Order {

    private int orderId;
    private String customerMail;
    private Date purchaceDate;
    private int orderHolderId;
    private int cupcakeId;
    private int cupcakePrice;
    private int cupcakeAmount;

    @Override
    public String toString(){

        return "Order:" +
                "orderId=" + orderId +
                ",customerMail='" + customerMail +
                ",purchaseDate=" + purchaceDate +
                ", orderHolderId=" + orderHolderId +
                ", cupcakePrice=" + cupcakePrice +
                ", cupcakeId=" + cupcakeId +
                ", cupcakeAmount=" + cupcakeAmount;
        }
    }



