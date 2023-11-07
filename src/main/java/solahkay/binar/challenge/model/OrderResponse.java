package solahkay.binar.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import solahkay.binar.challenge.enums.OrderStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private String code;

    private String username;

    private String shippingAddress;

    private String createdAt;

    private OrderStatus status;

    private List<OrderDetailResponse> details;

    private Long totalPrice;

    private Long totalQuantity;

}
