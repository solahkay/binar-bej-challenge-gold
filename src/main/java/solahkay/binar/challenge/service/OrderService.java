package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.model.CreateOrderRequest;
import solahkay.binar.challenge.model.OrderResponse;

public interface OrderService {

    byte[] createOrder(CreateOrderRequest request);

    OrderResponse getOrder(String orderCode);

    Page<OrderResponse> getAllOrder(String username, int page, int size);

}
