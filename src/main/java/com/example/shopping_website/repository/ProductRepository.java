package com.example.shopping_website.repository;

import com.example.shopping_website.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 通过名称模糊搜索（不区分大小写）
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    // 自定义查询：搜索商品名称或描述（不区分大小写）
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    // 按用户ID查找商品
    List<Product> findByUserId(Long userId);
    
    // 可选：添加一个更精确的搜索方法
    @Query("SELECT p FROM Product p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.stockQuantity > 0")
    List<Product> searchAvailableProducts(@Param("keyword") String keyword);
}