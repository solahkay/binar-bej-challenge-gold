package solahkay.binar.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.exception.ProductNotFoundException;
import solahkay.binar.challenge.model.AddProductRequest;
import solahkay.binar.challenge.model.ProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;
import solahkay.binar.challenge.repository.MerchantRepository;
import solahkay.binar.challenge.repository.ProductRepository;
import solahkay.binar.challenge.resolver.MerchantResolver;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MerchantResolver.class)
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        merchantRepository.deleteAll();
    }

    @Test
    void testAddSuccess(Merchant merchant) {
        merchantRepository.save(merchant);

        AddProductRequest productRequest = new AddProductRequest();
        productRequest.setName("test");
        productRequest.setPrice(100_000L);
        productRequest.setStock(30);
        productRequest.setMerchant(merchant);

        ProductResponse response = productService.add(merchant, productRequest);

        Product product = productRepository.findFirstByMerchantAndId(merchant, response.getId())
                        .orElse(null);

        assertNotNull(product);
        assertEquals(response.getId(), product.getId());
        assertEquals(response.getName(), product.getName());
        assertEquals(response.getPrice(), product.getPrice());
        assertEquals(response.getStock(), product.getStock());
    }

    @Test
    void testAddFailed(Merchant merchant) {
        AddProductRequest productRequest = new AddProductRequest();
        productRequest.setName("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffj");
        productRequest.setPrice(100_000L);
        productRequest.setStock(30);
        productRequest.setMerchant(merchant);

        assertThrows(ConstraintViolationException.class, () -> productService.add(merchant, productRequest));
    }

    @Test
    void testUpdateSuccess(Merchant merchant) {
        merchantRepository.save(merchant);

        AddProductRequest productRequest = new AddProductRequest();
        productRequest.setName("test");
        productRequest.setPrice(100_000L);
        productRequest.setStock(30);
        productRequest.setMerchant(merchant);

        ProductResponse productResponse = productService.add(merchant, productRequest);

        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setId(productResponse.getId());
        updateProductRequest.setName("test123");
        updateProductRequest.setPrice(30_000L);
        updateProductRequest.setStock(30);

        ProductResponse response = productService.update(merchant, updateProductRequest);

        Product product = productRepository.findById(response.getId()).orElse(null);

        assertNotNull(product);
        assertEquals(product.getId(), response.getId());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getPrice(), response.getPrice());
        assertEquals(product.getStock(), response.getStock());
    }

    @Test
    void testUpdateFailedProductNotFound(Merchant merchant) {
        merchantRepository.save(merchant);

        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setName("test");
        addProductRequest.setPrice(100_000L);
        addProductRequest.setStock(30);
        addProductRequest.setMerchant(merchant);

        productService.add(merchant, addProductRequest);

        UpdateProductRequest productRequest = new UpdateProductRequest();
        productRequest.setId("slkafdjsdkafjsakfjskfjkdjfjdkdfjdkfjdkjfkdjfkdjfkdfjdkfjdfjdkfjdkfjdkjdkfjdkfjdjfdkfdjfkdjfkdfjkdfjdkjf");
        productRequest.setName("test");
        productRequest.setPrice(1300L);
        productRequest.setStock(5);

        assertThrows(ProductNotFoundException.class, () -> productService.update(merchant, productRequest));
    }

    @Test
    void testUpdateFailedConstrainViolation(Merchant merchant) {
        UpdateProductRequest productRequest = new UpdateProductRequest();
        productRequest.setId("slkafdjsdkafjskfjkdjfjdkdfjfkdjfkdjfkdfjdkfjdfjdkfjdkfjdkjdkfjdkfjdjfdkfdjfkdjfkdfjkdfjdkjf");
        productRequest.setName("gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
        productRequest.setPrice(1300L);
        productRequest.setStock(5);

        assertThrows(ConstraintViolationException.class, () -> productService.update(merchant, productRequest));
    }

    @Test
    void testDeleteSuccess(Merchant merchant) {
        merchantRepository.save(merchant);

        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setName("test");
        addProductRequest.setPrice(100_000L);
        addProductRequest.setStock(30);
        addProductRequest.setMerchant(merchant);

        ProductResponse productResponse = productService.add(merchant, addProductRequest);

        productService.delete(merchant, productResponse.getId());

        assertFalse(productRepository.existsById(productResponse.getId()));
    }

    @Test
    void testDeleteFailed(Merchant merchant) {
        merchantRepository.save(merchant);

        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setName("test");
        addProductRequest.setPrice(100_000L);
        addProductRequest.setStock(30);
        addProductRequest.setMerchant(merchant);

       assertThrows(ProductNotFoundException.class, () ->  productService.delete(merchant, "askfjsakjsafisjaj"));
    }

    @Test
    void testGetAllProduct(Merchant merchant1, Merchant merchant2) {
        merchant1.setName("Toko Emas Retail");
        merchant2.setName("Toko Dunkin Donats");
        merchantRepository.save(merchant1);
        merchantRepository.save(merchant2);

        AddProductRequest addProductRequest1 = new AddProductRequest();
        addProductRequest1.setName("test");
        addProductRequest1.setPrice(100_000L);
        addProductRequest1.setStock(30);
        addProductRequest1.setMerchant(merchant1);

        productService.add(merchant1, addProductRequest1);

        AddProductRequest addProductRequest2 = new AddProductRequest();
        addProductRequest2.setName("donut");
        addProductRequest2.setPrice(12_000L);
        addProductRequest2.setStock(10);
        addProductRequest2.setMerchant(merchant2);

        productService.add(merchant2, addProductRequest2);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setPage(0);
        productRequest.setSize(10);

        Page<ProductResponse> allProduct = productService.getAllProduct(productRequest);

        assertNotNull(allProduct);
        assertEquals(2, allProduct.getContent().size());
    }

}
