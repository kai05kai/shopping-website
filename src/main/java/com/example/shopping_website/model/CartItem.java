package com.example.shopping_website.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "added_at", nullable = false)
    private java.time.LocalDateTime addedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = java.time.LocalDateTime.now();
    }
    
    // 计算小计
    public Double getSubtotal() {
        if (product != null && product.getPrice() != null && quantity != null) {
            return product.getPrice() * quantity;
        }
        return 0.0;
    }
}