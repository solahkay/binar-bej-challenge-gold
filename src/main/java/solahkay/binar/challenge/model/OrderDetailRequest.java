package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {

    @NotNull(message = "Product can't be null")
    @Positive
    private String productId;

    @NotNull(message = "Quantity can't be null")
    @Positive
    private Integer quantity;

}
