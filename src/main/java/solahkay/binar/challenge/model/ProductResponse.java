package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import solahkay.binar.challenge.enums.ProductStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private String sku;

    private String name;

    private Long price;

    private Long quantity;

    private ProductStatus status;

    private String merchantUsername;

}
