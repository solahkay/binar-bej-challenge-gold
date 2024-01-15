package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.model.CreateProductRequest;
import solahkay.binar.challenge.model.DeleteProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;

public interface ProductService {

    void createProduct(CreateProductRequest request);

    ProductResponse getProduct(String productSku);

    ProductResponse updateProduct(String productSku, UpdateProductRequest request);

    void deleteProduct(String productSku, DeleteProductRequest request);

    Page<ProductResponse> getAllAvailableProduct(int page, int size);

}
