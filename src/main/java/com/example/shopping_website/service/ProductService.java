package com.example.shopping_website.service;

import com.example.shopping_website.model.Product;
import com.example.shopping_website.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // 获取所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // 获取商品详情（返回Optional）
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // 获取商品详情（返回Product，如果不存在则抛出异常）
    public Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在，ID: " + id));
    }
    
    // 保存商品
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    // 删除商品
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    // 获取用户的所有商品
    public List<Product> getProductsByUserId(Long userId) {
        return productRepository.findByUserId(userId);
    }
    
    // 搜索商品
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(keyword.trim());
    }
}