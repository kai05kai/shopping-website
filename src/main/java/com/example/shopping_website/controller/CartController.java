package com.example.shopping_website.controller;

import com.example.shopping_website.model.CartItem;
import com.example.shopping_website.model.User;
import com.example.shopping_website.service.CartService;
import com.example.shopping_website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    // 查看购物车
    @GetMapping
    public String viewCart(Model model) {
        try {
            System.out.println("=== 开始查看购物车 ===");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            System.out.println("当前用户名: " + username);
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));
            
            System.out.println("用户ID: " + user.getId());
            
            List<CartItem> cartItems = cartService.getCartItemsByUser(user.getId());
            Double totalPrice = cartService.calculateTotalPrice(user.getId());
            
            System.out.println("购物车商品数量: " + cartItems.size());
            System.out.println("购物车总价: " + totalPrice);
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "购物车 - Shopping Mall");
            model.addAttribute("currentUser", user);
            int totalItemsCount = cartItems.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
            model.addAttribute("totalItemsCount", totalItemsCount);
            
            return "cart/cart";
            
        } catch (Exception e) {
            System.err.println("查看购物车失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "获取购物车信息失败: " + e.getMessage());
            return "error/error";
        }
    }
    
    // 添加商品到购物车
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始添加商品到购物车 ===");
            System.out.println("商品ID: " + productId);
            System.out.println("数量: " + quantity);
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            System.out.println("当前用户名: " + username);
            
            if ("anonymousUser".equals(username)) {
                System.out.println("用户未登录，重定向到登录页");
                redirectAttributes.addFlashAttribute("error", "请先登录");
                return "redirect:/login";
            }
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
            
            System.out.println("用户ID: " + user.getId());
            
            // 添加商品到购物车
            cartService.addToCart(user.getId(), productId, quantity);
            
            // 添加成功消息
            redirectAttributes.addFlashAttribute("success", "商品已成功添加到购物车！");
            System.out.println("商品添加成功");
            
            return "redirect:/";
            
        } catch (Exception e) {
            System.err.println("添加购物车失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "添加失败: " + e.getMessage());
            return "redirect:/";
        }
    }
    
    // 更新购物车商品数量
    @PostMapping("/update/{cartItemId}")
    public String updateQuantity(@PathVariable Long cartItemId,
                                @RequestParam Integer quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始更新购物车商品数量 ===");
            System.out.println("购物车项ID: " + cartItemId);
            System.out.println("新数量: " + quantity);
            
            cartService.updateCartItemQuantity(cartItemId, quantity);
            redirectAttributes.addFlashAttribute("success", "购物车已更新");
            System.out.println("更新成功");
            
        } catch (Exception e) {
            System.err.println("更新失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "更新失败: " + e.getMessage());
        }
        return "redirect:/cart";
    }
    
    // 从购物车移除商品
    @PostMapping("/remove/{cartItemId}")
    public String removeItem(@PathVariable Long cartItemId,
                            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始移除购物车商品 ===");
            System.out.println("购物车项ID: " + cartItemId);
            
            cartService.removeFromCart(cartItemId);
            redirectAttributes.addFlashAttribute("success", "商品已从购物车移除");
            System.out.println("移除成功");
            
        } catch (Exception e) {
            System.err.println("移除失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "移除失败: " + e.getMessage());
        }
        return "redirect:/cart";
    }
    
    // 清空购物车
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始清空购物车 ===");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));
            
            cartService.clearCart(user.getId());
            redirectAttributes.addFlashAttribute("success", "购物车已清空");
            System.out.println("清空成功");
            
        } catch (Exception e) {
            System.err.println("清空失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "清空失败: " + e.getMessage());
        }
        return "redirect:/cart";
    }
    
    // 结算购物车
    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始结算购物车 ===");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            System.out.println("当前用户名: " + username);
            
            User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));
            
            System.out.println("用户ID: " + user.getId());
            
            // 检查购物车是否为空
            List<CartItem> cartItems = cartService.getCartItemsByUser(user.getId());
            if (cartItems == null || cartItems.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "购物车为空，无法结算");
                System.out.println("购物车为空");
                return "redirect:/cart";
            }
            
            // 计算总件数和总价（在结算前计算，用于显示）
            int totalItemsCount = cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
            Double totalPrice = cartService.calculateTotalPrice(user.getId());
            
            System.out.println("结算信息 - 总件数: " + totalItemsCount + " 件");
            System.out.println("结算总价: ¥" + totalPrice);
            System.out.println("商品种类: " + cartItems.size() + " 种");
            
            // 关键修改：调用结算方法，扣减库存 - 不再接收返回值
            cartService.checkoutAndUpdateStock(user.getId());
            
            // 添加成功消息（使用我们计算好的值）
            redirectAttributes.addFlashAttribute("success", 
                "结算成功！共结算 " + totalItemsCount + " 件商品，总计 ¥" + 
                String.format("%.2f", totalPrice));
            
            System.out.println("结算成功，库存已更新，购物车已清空");
            
            return "redirect:/cart";
            
        } catch (Exception e) {
            System.err.println("结算失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "结算失败: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}