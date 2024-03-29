package solahkay.binar.challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import solahkay.binar.challenge.model.CreateProductRequest;
import solahkay.binar.challenge.model.DeleteProductRequest;
import solahkay.binar.challenge.model.PagingResponse;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;
import solahkay.binar.challenge.model.WebResponse;
import solahkay.binar.challenge.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<String> createProduct(@RequestBody CreateProductRequest request) {
        productService.createProduct(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "{sku}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> getProduct(@PathVariable("sku") String sku) {
        ProductResponse productResponse = productService.getProduct(sku);
        return WebResponse.<ProductResponse>builder().data(productResponse).build();
    }

    @PatchMapping(
            path = "{sku}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> updateProduct(@PathVariable("sku") String sku,
                                                      @RequestBody UpdateProductRequest request) {
        ProductResponse productResponse = productService.updateProduct(sku, request);
        return WebResponse.<ProductResponse>builder().data(productResponse).build();
    }

    @DeleteMapping(
            path = "delete/{sku}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteProduct(@PathVariable("sku") String sku,
                                             @RequestBody DeleteProductRequest request) {
        productService.deleteProduct(sku, request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductResponse>> getAllProduct(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductResponse> allProduct = productService.getAllAvailableProduct(page, size);
        return WebResponse.<List<ProductResponse>>builder()
                .data(allProduct.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(allProduct.getNumber())
                        .totalPage(allProduct.getTotalPages())
                        .size(allProduct.getSize())
                        .build())
                .build();
    }

}
