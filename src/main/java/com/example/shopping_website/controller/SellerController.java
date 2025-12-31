package com.example.shopping_website.controller;

import com.example.shopping_website.model.User;
import com.example.shopping_website.service.ProductService;
import com.example.shopping_website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户未找到: " + username));
    }
    
    /**
     * 商家后台首页 - 重定向到我的商品页面
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('SELLER')")
    public String dashboard() {
        return "redirect:/products/my-products";
    }
}