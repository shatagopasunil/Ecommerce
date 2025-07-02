package com.sunil45.ecommerce.app;

import com.sunil45.ecommerce.enums.Roles;
import com.sunil45.ecommerce.model.Role;
import com.sunil45.ecommerce.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig implements CommandLineRunner {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
            roleRepository.save(new Role(Roles.ROLE_USER));
            roleRepository.save(new Role(Roles.ROLE_ADMIN));
            roleRepository.save(new Role(Roles.ROLE_SELLER));
    }
}
