package com.bookstore.service;

import com.bookstore.entity.Role;
import com.bookstore.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Create a new role
    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new IllegalArgumentException("Role already exists!");
        }
        return roleRepository.save(role);
    }

    // Retrieve all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Retrieve a role by ID
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }

    // Retrieve a role by name
    public Optional<Role> getRoleByName(Role.ERole roleName) {
        return roleRepository.findByName(roleName);
    }

    // Update an existing role
    public Role updateRole(Integer id, Role updatedRole) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + id + " not found"));

        existingRole.setName(updatedRole.getName()); // Update the name
        return roleRepository.save(existingRole);
    }

    // Delete a role by ID
    public void deleteRole(Integer id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Role with ID " + id + " does not exist");
        }
        roleRepository.deleteById(id);
    }
}