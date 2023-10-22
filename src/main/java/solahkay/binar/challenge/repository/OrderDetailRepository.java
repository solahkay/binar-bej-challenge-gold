package solahkay.binar.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.OrderDetail;
import solahkay.binar.challenge.entity.Product;
import solahkay.binar.challenge.entity.User;
import solahkay.binar.challenge.entity.identifier.OrderDetailsId;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailsId> {

    void deleteByProduct(Product product);

    void deleteByOrder(Order order);

    void deleteAllByOrder(Order order);

}
