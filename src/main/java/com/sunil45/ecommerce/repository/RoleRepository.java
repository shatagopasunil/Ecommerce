package com.sunil45.ecommerce.repository;

import com.sunil45.ecommerce.enums.Roles;
import com.sunil45.ecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(Roles roleName);
}
