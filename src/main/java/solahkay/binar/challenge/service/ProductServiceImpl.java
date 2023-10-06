package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.exception.ProductNotFoundException;
import solahkay.binar.challenge.model.AddProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;
import solahkay.binar.challenge.repository.ProductRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ValidationService validationService;

    private static final String NOT_FOUND = " not found";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ValidationService validationService) {
        this.productRepository = productRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public ProductResponse add(Merchant merchant, AddProductRequest productRequest) {
        validationService.validate(productRequest);

        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .merchant(merchant)
                .build();

        productRepository.save(product);

        return toProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Merchant merchant, UpdateProductRequest productRequest) {
        validationService.validate(productRequest);

        Product product = productRepository.findFirstByMerchantAndId(merchant, productRequest.getId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id " + productRequest.getId() + NOT_FOUND
                ));

        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());

        productRepository.save(product);

        return toProductResponse(product);
    }

    @Override
    @Transactional
    public void delete(Merchant merchant, String productId) {
        Product product = productRepository.findFirstByMerchantAndId(merchant, productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with id " + productId + NOT_FOUND
                ));

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::toProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

}
