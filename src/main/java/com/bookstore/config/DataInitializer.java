package com.bookstore.config;

import com.bookstore.entity.Role;
import com.bookstore.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataInitializer {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        // Ensure ROLE_USER exists
        if (roleRepository.findByName(Role.ERole.ROLE_USER).isEmpty()) {
            roleRepository.save(new Role(null, Role.ERole.ROLE_USER));
        }

        // Ensure ROLE_ADMIN exists
        if (roleRepository.findByName(Role.ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(null, Role.ERole.ROLE_ADMIN));
        }
    }
}