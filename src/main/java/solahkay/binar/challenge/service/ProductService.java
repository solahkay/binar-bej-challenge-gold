package solahkay.binar.challenge.service;

import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.model.AddProductRequest;
import solahkay.binar.challenge.model.ProductResponse;
import solahkay.binar.challenge.model.UpdateProductRequest;

import java.util.List;

public interface ProductService {

    ProductResponse add(Merchant merchant, AddProductRequest productRequest);

    ProductResponse update(Merchant merchant, UpdateProductRequest productRequest);

    void delete(Merchant merchant, String productId);

    List<ProductResponse> getAllProduct();

}
