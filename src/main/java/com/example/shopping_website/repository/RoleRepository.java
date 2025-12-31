package com.example.shopping_website.repository;

import com.example.shopping_website.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    // 通过枚举查找
    Optional<Role> findByName(Role.RoleName name);
    
    // 通过字符串查找（新增）
    default Optional<Role> findByName(String name) {
        try {
            Role.RoleName roleName = Role.RoleName.valueOf(name.toUpperCase());
            return findByName(roleName);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}