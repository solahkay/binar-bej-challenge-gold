package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceModel {

    private String sku;

    private String productName;

    private String merchantName;

    private Long price;

    private Long quantity;

    private String username;

    private String address;

    private String status;

    private Long totalPrice;

    private String orderTime;

    private String orderCode;

    private Long quantityTotal;

}
