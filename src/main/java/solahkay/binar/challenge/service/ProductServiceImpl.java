package solahkay.binar.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.enums.ProductStatus;
import solahkay.binar.challenge.generator.ProductSkuGenerator;
import solahkay.binar.challenge.model.CreateProductRequest;
import solahkay.binar.challenge.model.DeleteProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;
import solahkay.binar.challenge.repository.MerchantRepository;
import solahkay.binar.challenge.repository.OrderDetailRepository;
import solahkay.binar.challenge.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final MerchantRepository merchantRepository;

    private final ValidationService validationService;

    private final OrderDetailRepository orderDetailRepository;

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    private static final String MERCHANT_NOT_FOUND_MESSAGE = "Merchant not found";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              MerchantRepository merchantRepository,
                              OrderDetailRepository orderDetailRepository,
                              ValidationService validationService) {
        this.productRepository = productRepository;
        this.merchantRepository = merchantRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.validationService = validationService;
    }


    @Override
    @Transactional
    public void createProduct(CreateProductRequest request) {
        validationService.validate(request);

        Merchant merchant = merchantRepository.findByName(request.getMerchantName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MERCHANT_NOT_FOUND_MESSAGE
                ));

        ProductStatus status = request.getQuantity() > 0 ?
                ProductStatus.AVAILABLE :
                ProductStatus.OUT_OF_STOCK;

        Product product = Product.builder()
                .id(UUID.randomUUID().toString())
                .sku(ProductSkuGenerator.generate(merchant, request.getName()))
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .status(status)
                .merchant(merchant)
                .build();

        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProduct(String productSku) {
        Product product = productRepository.findBySku(productSku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));

        return toProductResponse(product);
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .sku(product.getSku())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .status(product.getStatus())
                .merchantName(product.getMerchant().getName())
                .build();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String productSku, UpdateProductRequest request) {
        validationService.validate(request);

        Merchant merchant = merchantRepository.findByName(request.getMerchantName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MERCHANT_NOT_FOUND_MESSAGE
                ));

        Product product = productRepository.findFirstByMerchantAndSku(merchant, productSku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));

        if (Objects.nonNull(request.getName())) {
            product.setName(request.getName());
            product.setSku(ProductSkuGenerator.generate(merchant, request.getName()));
        }

        if (Objects.nonNull(request.getPrice())) {
            product.setPrice(request.getPrice());
        }

        if (Objects.nonNull(request.getQuantity())) {
            product.setQuantity(request.getQuantity());
            if (request.getQuantity() == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
        }

        productRepository.save(product);

        return toProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String productSku, DeleteProductRequest request) {
        validationService.validate(request);

        Merchant merchant = merchantRepository.findByName(request.getMerchantName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, MERCHANT_NOT_FOUND_MESSAGE
                ));

        Product product = productRepository.findFirstByMerchantAndSku(merchant, productSku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE));

        orderDetailRepository.deleteByProduct(product);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllAvailableProduct(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = products.getContent().stream()
                .filter(product -> product.getStatus() == ProductStatus.AVAILABLE)
                .map(ProductServiceImpl::toProductResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(productResponses, pageable, products.getTotalElements());
    }

}
