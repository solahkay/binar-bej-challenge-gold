package solahkay.binar.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import solahkay.binar.challenge.model.CreateOrderRequest;
import solahkay.binar.challenge.model.OrderResponse;
import solahkay.binar.challenge.model.PagingResponse;
import solahkay.binar.challenge.model.WebResponse;
import solahkay.binar.challenge.service.OrderService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(
            path = "/api/v1/orders",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<byte[]> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        byte[] report = orderService.createOrder(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("invoice", "invoice.pdf");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @GetMapping(
            path = "/api/v1/orders/{orderCode}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderResponse> getOrder(@PathVariable("orderCode") String orderCode) {
        OrderResponse orderResponse = orderService.getOrder(orderCode);
        return WebResponse.<OrderResponse>builder().data(orderResponse).build();
    }

    @GetMapping(
            path = "/api/v1/orders",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<OrderResponse>> getAllOrder(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<OrderResponse> allOrder = orderService.getAllOrder(username, page, size);
        return WebResponse.<List<OrderResponse>>builder()
                .data(allOrder.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(allOrder.getNumber())
                        .totalPage(allOrder.getTotalPages())
                        .size(allOrder.getSize())
                        .build())
                .build();
    }

}
