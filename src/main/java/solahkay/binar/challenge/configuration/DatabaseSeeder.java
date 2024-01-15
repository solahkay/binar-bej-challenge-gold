package solahkay.binar.challenge.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import solahkay.binar.challenge.entity.Role;
import solahkay.binar.challenge.enums.UserRole;
import solahkay.binar.challenge.repository.RoleRepository;

@Component
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Autowired
    public DatabaseSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            for (UserRole roleName : UserRole.values()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
            log.info("Roles seeded successfully.");
        } else {
            log.warn("Roles are already present in the database.");
        }
    }

}
