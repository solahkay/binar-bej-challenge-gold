package solahkay.binar.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.entity.Product;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findFirstByMerchantAndSku(Merchant merchant, String sku);

    Optional<Product> findBySku(String sku);

}
