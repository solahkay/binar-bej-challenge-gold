package solahkay.binar.challenge.service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.OrderDetail;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.entity.identifier.OrderDetailId;
import solahkay.binar.challenge.enums.OrderStatus;
import solahkay.binar.challenge.enums.ProductStatus;
import solahkay.binar.challenge.generator.OrderCodeGenerator;
import solahkay.binar.challenge.model.CreateOrderRequest;
import solahkay.binar.challenge.model.InvoiceModel;
import solahkay.binar.challenge.model.OrderDetailRequest;
import solahkay.binar.challenge.model.OrderDetailResponse;
import solahkay.binar.challenge.model.OrderResponse;
import solahkay.binar.challenge.repository.OrderDetailRepository;
import solahkay.binar.challenge.repository.OrderRepository;
import solahkay.binar.challenge.repository.ProductRepository;
import solahkay.binar.challenge.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ValidationService validationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            OrderDetailRepository orderDetailRepository,
                            ValidationService validationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.validationService = validationService;
    }


    @Override
    @Transactional
    public byte[] createOrder(CreateOrderRequest request) {
        validationService.validate(request);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String formattedLocalDateTime = LocalDateTime.now().format(FORMATTER);
        LocalDateTime localDateTime = LocalDateTime.parse(formattedLocalDateTime, FORMATTER);

        Order order = Order.builder()
                .code(OrderCodeGenerator.generateOrderCode())
                .shippingAddress(request.getShippingAddress())
                .createdAt(localDateTime)
                .status(OrderStatus.PROCESSING)
                .user(user)
                .build();

        orderRepository.save(order);

        List<OrderDetailRequest> orderDetailsRequest = request.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = new LinkedList<>();
        List<InvoiceModel> invoiceModels = new LinkedList<>();
        List<InvoiceModel> productInvoiceModels = new LinkedList<>();

        insertToOrderDetail(orderDetailsRequest, order, orderDetailResponses, productInvoiceModels);

        long totalPrice = getTotalPriceFromOrderDetail(orderDetailResponses);

        long totalQuantity = getTotalQuantityFromOrderDetail(orderDetailResponses);

        DateTimeFormatter formatterForInvoice = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss");
        String orderTime = order.getCreatedAt().format(formatterForInvoice);

        InvoiceModel invoiceModel = InvoiceModel.builder()
                .username(user.getUsername())
                .address(order.getShippingAddress())
                .status(order.getStatus().name())
                .totalPrice(totalPrice)
                .orderTime(orderTime)
                .orderCode(order.getCode())
                .quantityTotal(totalQuantity)
                .build();

        invoiceModels.add(invoiceModel);
        invoiceModels.addAll(productInvoiceModels);

        try {
            return generateInvoice(invoiceModels);
        } catch (IOException | JRException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error!");
        }
    }

    private static long getTotalQuantityFromOrderDetail(List<OrderDetailResponse> orderDetailResponses) {
        return orderDetailResponses.stream()
                .mapToLong(OrderDetailResponse::getQuantity)
                .sum();
    }

    private static long getTotalPriceFromOrderDetail(List<OrderDetailResponse> orderDetailResponses) {
        return orderDetailResponses.stream()
                .mapToLong(OrderDetailResponse::getTotalPrice)
                .sum();
    }

    private void insertToOrderDetail(List<OrderDetailRequest> orderDetailsRequest,
                                     Order order,
                                     List<OrderDetailResponse> orderDetailResponses,
                                     List<InvoiceModel> productInvoiceModels) {
        orderDetailsRequest.forEach(orderDetailRequest -> {
            Product product = productRepository.findBySku(orderDetailRequest.getProductSku())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            if (product.getQuantity() < orderDetailRequest.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product quantity is less than order quantity");
            }

            OrderDetailId id = new OrderDetailId();
            id.setOrderId(order.getId());
            id.setProductId(product.getId());

            OrderDetail orderDetail = OrderDetail.builder()
                    .id(id)
                    .quantity(orderDetailRequest.getQuantity())
                    .totalPrice(product.getPrice() * orderDetailRequest.getQuantity())
                    .order(order)
                    .product(product)
                    .build();

            orderDetailRepository.save(orderDetail);

            long quantity = product.getQuantity() - orderDetailRequest.getQuantity();
            if (quantity == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            product.setQuantity(quantity);
            productRepository.save(product);

            OrderDetailResponse orderDetailResponse = toOrderDetailResponse(orderDetail);
            orderDetailResponses.add(orderDetailResponse);

            insertOrderDetailToInvoiceModels(productInvoiceModels, orderDetail);
        });
    }

    private static void insertOrderDetailToInvoiceModels(List<InvoiceModel> productInvoiceModels,
                                                         OrderDetail orderDetail) {
        Product product = orderDetail.getProduct();
        InvoiceModel productInvoiceModel = InvoiceModel.builder()
                .sku(product.getSku())
                .productName(product.getName())
                .merchantName(product.getMerchant().getName())
                .price(product.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();

        productInvoiceModels.add(productInvoiceModel);
    }

    private static OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail) {
        Product product = orderDetail.getProduct();
        Long quantity = orderDetail.getQuantity();
        return OrderDetailResponse.builder()
                .productSku(product.getSku())
                .productName(product.getName())
                .quantity(quantity)
                .price(product.getPrice())
                .totalPrice(quantity * product.getPrice())
                .build();
    }

    private byte[] generateInvoice(List<InvoiceModel> invoiceModels) throws IOException, JRException {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            byte[] report = buildReport(invoiceModels, jasperReport);

            if (Objects.nonNull(reportStream)) {
                reportStream.close();
            }

            return report;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error!");
        }
    }

    private static byte[] buildReport(List<InvoiceModel> invoiceModels, JasperReport jasperReport) throws JRException {
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoiceModels);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("author", "Solah");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameter, dataSource);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderCode) {
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return toOrderResponse(order);
    }

    private static OrderResponse toOrderResponse(Order order) {
        List<OrderDetail> orderDetails = order.getOrderDetails();
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderServiceImpl::toOrderDetailResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .code(order.getCode())
                .username(order.getUser().getUsername())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt().format(FORMATTER))
                .status(order.getStatus())
                .details(orderDetailResponses)
                .totalPrice(getTotalPriceFromOrderDetail(orderDetailResponses))
                .totalQuantity(getTotalQuantityFromOrderDetail(orderDetailResponses))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrder(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findAllByUser(user, pageable);

        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(OrderServiceImpl::toOrderResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, orders.getTotalElements());
    }

}
