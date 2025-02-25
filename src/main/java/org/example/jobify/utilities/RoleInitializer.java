package org.example.jobify.utilities;

import org.example.jobify.model.Role;
import org.example.jobify.model.RoleName;
import org.example.jobify.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(RoleName.JOB_SEEKER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.JOB_SEEKER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
        if (roleRepository.findByName(RoleName.EMPLOYER).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.EMPLOYER
            );
            roleRepository.save(adminRole);
        }
    }
}
