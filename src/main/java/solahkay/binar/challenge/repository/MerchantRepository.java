package solahkay.binar.challenge.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solahkay.binar.challenge.entity.Merchant;
import solahkay.binar.challenge.enums.MerchantStatus;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, String> {

    boolean existsByName(String name);

    Optional<Merchant> findByName(String name);

    Page<Merchant> findAllByStatus(MerchantStatus status, Pageable pageable);

}
