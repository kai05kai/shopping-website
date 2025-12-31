package com.example.shopping_website.model;

public enum Permission {
    // 商品相关权限
    PRODUCT_VIEW("product:view"),
    PRODUCT_CREATE("product:create"),
    PRODUCT_EDIT("product:edit"),
    PRODUCT_DELETE("product:delete"),
    
    // 订单相关权限
    ORDER_VIEW("order:view"),
    ORDER_MANAGE("order:manage"),
    
    // 用户相关权限
    USER_VIEW("user:view"),
    USER_MANAGE("user:manage"),
    
    // 系统权限
    SYSTEM_ADMIN("system:admin"),
    SYSTEM_CONFIG("system:config");
    
    private final String permission;
    
    Permission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return permission;
    }
}