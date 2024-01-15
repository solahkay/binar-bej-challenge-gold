package solahkay.binar.challenge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solahkay.binar.challenge.entity.Order;
import solahkay.binar.challenge.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByCode(String code);

    List<Order> findAllByUser(User user);

    Page<Order> findAllByUser(User user, Pageable pageable);

}
