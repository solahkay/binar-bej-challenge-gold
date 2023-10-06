package solahkay.binar.challenge.service;

import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.model.CreateOrderRequest;
import solahkay.binar.challenge.model.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse create(User user, CreateOrderRequest orderRequest);

    List<OrderResponse> getAll();

}
