package com.example.shopping_website.repository;

import com.example.shopping_website.model.CartItem;
import com.example.shopping_website.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // 根据用户ID查找购物车商品
    List<CartItem> findByUserId(Long userId);
    
    // 根据用户ID和商品ID查找购物车项
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    
    // 计算用户购物车中商品总数
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user.id = :userId")
    Integer countTotalItemsByUserId(@Param("userId") Long userId);
    
    // 计算用户购物车总价
    @Query("SELECT SUM(ci.quantity * p.price) FROM CartItem ci JOIN ci.product p WHERE ci.user.id = :userId")
    Double calculateTotalPriceByUserId(@Param("userId") Long userId);
    
    // 删除用户的所有购物车商品
    void deleteByUserId(Long userId);
    
    // 删除用户的特定购物车商品
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    // 检查购物车是否为空
    boolean existsByUserId(Long userId);
}