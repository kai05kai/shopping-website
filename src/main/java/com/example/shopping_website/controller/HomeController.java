package com.example.shopping_website.controller;

import com.example.shopping_website.model.Product;
import com.example.shopping_website.service.ProductService;
import com.example.shopping_website.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil; // 添加这个依赖
    
    @GetMapping("/")
    public String home(Model model) {
        try {
            List<Product> products = productService.getAllProducts();
            
            // 确保每个商品都有正确的图片URL
            for (Product product : products) {
                String imageUrl = product.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    product.setImageUrl(fileUploadUtil.getDefaultImageUrl());
                } else {
                    // 使用FileUploadUtil确保路径正确
                    product.setImageUrl(fileUploadUtil.getImageUrl(imageUrl));
                }
            }
            
            model.addAttribute("products", products);
            model.addAttribute("pageTitle", "购物商城首页");
            model.addAttribute("searchMode", false);
            
            // 调试信息 - 检查图片URL
            System.out.println("=== 主页图片URL检查 ===");
            for (Product product : products) {
                System.out.println("商品: " + product.getName() + 
                                ", 图片URL: " + product.getImageUrl());
            }
            
            return "layout/base";
            
        } catch (Exception e) {
            System.err.println("加载首页失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "加载商品失败: " + e.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        try {
            List<Product> products;
            String pageTitle;
            boolean searchMode = false;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 执行搜索
                products = productService.searchProducts(keyword);
                pageTitle = "搜索结果: " + keyword + " - Shopping Mall";
                model.addAttribute("keyword", keyword);
                searchMode = true;
            } else {
                // 如果没有关键词，显示所有商品
                products = productService.getAllProducts();
                pageTitle = "所有商品";
            }
            
            // 处理图片路径
            for (Product product : products) {
                String imageUrl = product.getImageUrl();
                if (imageUrl == null || imageUrl.isEmpty()) {
                    product.setImageUrl(fileUploadUtil.getDefaultImageUrl());
                } else {
                    product.setImageUrl(fileUploadUtil.getImageUrl(imageUrl));
                }
            }
            
            model.addAttribute("products", products);
            model.addAttribute("pageTitle", pageTitle);
            model.addAttribute("searchMode", searchMode);
            
            return "layout/base";
        } catch (Exception e) {
            System.err.println("搜索失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "搜索失败: " + e.getMessage());
            return "error/error";
        }
    }

    @GetMapping("/test-simple")
    public String testSimple() {
        return "test/simple-test";
    }

    @GetMapping("/test-cart")
    public String testCart() {
        return "test-cart";
    }
}