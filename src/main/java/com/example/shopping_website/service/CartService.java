package com.example.shopping_website.service;

import com.example.shopping_website.model.CartItem;
import com.example.shopping_website.model.Product;
import com.example.shopping_website.model.User;
import com.example.shopping_website.repository.CartItemRepository;
import com.example.shopping_website.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserService userService;
    
    // 获取用户的购物车商品
    public List<CartItem> getCartItemsByUser(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }
    
    // 获取用户的购物车商品数量
    public Integer getCartItemCount(Long userId) {
        Integer count = cartItemRepository.countTotalItemsByUserId(userId);
        return count != null ? count : 0;
    }
    
    // 添加商品到购物车
    @Transactional
    public CartItem addToCart(Long userId, Long productId, Integer quantity) {
        // 检查商品是否存在
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 检查库存
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + product.getStockQuantity());
        }
        
        // 检查是否已经在购物车中
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        
        if (existingItem.isPresent()) {
            // 如果已存在，更新数量
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // 如果不存在，创建新的购物车项
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            
            return cartItemRepository.save(cartItem);
        }
    }
    
    // 更新购物车商品数量
    @Transactional
    public CartItem updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        // 检查库存
        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new RuntimeException("库存不足，当前库存: " + cartItem.getProduct().getStockQuantity());
        }
        
        if (quantity <= 0) {
            // 如果数量小于等于0，删除该商品
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }
    
    // 从购物车中移除商品
    @Transactional
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        cartItemRepository.delete(cartItem);
    }
    
    // 清空购物车
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
    
    // 计算购物车总价
    public Double calculateTotalPrice(Long userId) {
        Double total = cartItemRepository.calculateTotalPriceByUserId(userId);
        return total != null ? total : 0.0;
    }
    
    // 检查购物车是否为空
    public boolean isCartEmpty(Long userId) {
        return !cartItemRepository.existsByUserId(userId);
    }

    // 结算并更新库存
    @Transactional
    public void checkoutAndUpdateStock(Long userId) {
        // 1. 获取用户购物车中的所有商品
        List<CartItem> cartItems = getCartItemsByUser(userId);
        
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空，无法结算");
        }
        
        // 2. 验证库存并扣减
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            
            // 再次验证库存（防止在加入购物车后库存发生变化）
            if (product.getStockQuantity() < quantity) {
                throw new RuntimeException("商品【" + product.getName() + "】库存不足，当前库存: " + product.getStockQuantity());
            }
            
            // 扣减库存
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);
            
            System.out.println("扣减库存成功 - 商品: " + product.getName() + 
                            ", 扣减数量: " + quantity + 
                            ", 剩余库存: " + product.getStockQuantity());
        }
        
        // 3. 清空购物车
        clearCart(userId);
    }
}