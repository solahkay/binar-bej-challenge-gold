package solahkay.binar.challenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solahkay.binar.challenge.entity.Role;
import solahkay.binar.challenge.enums.UserRole;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(UserRole userRole);

}
