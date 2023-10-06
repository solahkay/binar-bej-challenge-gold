package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.OrderDetail;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.exception.ProductNotFoundException;
import solahkay.binar.challenge.model.CreateOrderRequest;
import solahkay.binar.challenge.model.OrderDetailRequest;
import solahkay.binar.challenge.model.OrderResponse;
import solahkay.binar.challenge.repository.OrderRepository;
import solahkay.binar.challenge.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final ValidationService validationService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            ValidationService validationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public OrderResponse create(User user, CreateOrderRequest orderRequest) {
        validationService.validate(orderRequest);

        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .destinationAddress(orderRequest.getDestinationAddress())
                .orderDate(LocalDateTime.now())
                .completed(false)
                .user(user)
                .build();

        List<OrderDetail> orderDetails = new LinkedList<>();
        addOrderToOrderDetail(orderRequest, order, orderDetails);
        order.setOrderDetails(orderDetails);

        orderRepository.save(order);

        return toOrderResponse(order);
    }

    private void addOrderToOrderDetail(CreateOrderRequest orderRequest, Order order, List<OrderDetail> orderDetails) {
        orderRequest.getOrderDetails()
                .forEach(detailRequest -> {
                    Product product = productRepository.findById(detailRequest.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException(
                                    "Product with id " + detailRequest.getProductId() + " not found"
                            ));

                    OrderDetail orderDetail = toOrderDetail(detailRequest, order, product);

                    orderDetails.add(orderDetail);
                });
    }

    private OrderDetail toOrderDetail(OrderDetailRequest detailRequest, Order order, Product product) {
        return OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(detailRequest.getQuantity())
                .totalPrice(product.getPrice() * detailRequest.getQuantity())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .destinationAddress(order.getDestinationAddress())
                .orderDate(order.getOrderDate())
                .completed(order.isCompleted())
                .build();
    }

}
