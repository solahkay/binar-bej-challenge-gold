package solahkay.binar.challenge.service;

import org.springframework.data.domain.Page;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.model.AddProductRequest;
import solahkay.binar.challenge.model.ProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;

public interface ProductService {

    ProductResponse add(Merchant merchant, AddProductRequest productRequest);

    ProductResponse update(Merchant merchant, UpdateProductRequest productRequest);

    void delete(Merchant merchant, String productId);

    Page<ProductResponse> getAllProduct(ProductRequest productRequest);

}
