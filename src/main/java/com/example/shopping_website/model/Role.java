package com.example.shopping_website.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private RoleName name;
    
    @Column(name = "description")
    private String description;
    
    // 添加权限字段
    @Column(name = "permissions")
    private String permissions;
    
    public enum RoleName {
        ROLE_USER,
        ROLE_SELLER,
        ROLE_ADMIN,
        ROLE_VIP,
        ROLE_MODERATOR
    }

    // 这个方法返回枚举的名称（字符串）
    public String getNameString() {
        return name != null ? name.name() : null;
    }
    
    // 这个方法返回枚举类型
    public RoleName getName() {
        return name;
    }
    
    // 设置角色名称（接受字符串）
    public void setName(String name) {
        this.name = RoleName.valueOf(name);
    }
    
    // 设置角色名称（接受枚举）
    public void setName(RoleName name) {
        this.name = name;
    }
}
    
