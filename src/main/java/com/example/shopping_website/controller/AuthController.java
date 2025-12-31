package com.example.shopping_website.controller;

import com.example.shopping_website.dto.ChangePasswordDto;
import com.example.shopping_website.dto.UserRegistrationDto;
import com.example.shopping_website.dto.UserUpdateDto;
import com.example.shopping_website.model.Product;
import com.example.shopping_website.model.User;
import com.example.shopping_website.service.CartService;
import com.example.shopping_website.service.ProductService;
import com.example.shopping_website.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CartService cartService;  // 添加 CartService 依赖
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "登录");
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("pageTitle", "注册");
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                              BindingResult result,
                              Model model) {
        // 验证错误处理
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // 检查密码是否一致
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            model.addAttribute("passwordError", "两次输入的密码不一致");
            return "auth/register";
        }
        
        try {
            // 注册用户
            userService.registerUser(registrationDto);
            model.addAttribute("success", "注册成功！请登录");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
    
    @GetMapping("/profile")
    public String profilePage(Model model) {
        try {
            System.out.println("=== 开始处理个人资料页请求 ===");
            
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("认证信息: " + auth);
            System.out.println("用户名: " + auth.getName());
            
            // 检查是否已认证
            if (!auth.isAuthenticated()) {
                System.out.println("用户未认证，重定向到登录页");
                return "redirect:/login";
            }
            
            String username = auth.getName();
            System.out.println("获取用户名: " + username);
            
            // 检查是否是匿名用户
            if ("anonymousUser".equals(username)) {
                System.out.println("匿名用户，重定向到登录页");
                return "redirect:/login";
            }
            
            // 从数据库获取用户信息
            User user = userService.getUserByUsername(username).orElse(null);
            
            if (user == null) {
                System.out.println("用户未找到: " + username);
                model.addAttribute("error", "用户信息不存在");
                model.addAttribute("pageTitle", "错误 - Shopping Mall");
                return "error/403";
            }
            
            System.out.println("找到用户: " + user.getUsername());
            
            // 获取购物车数量 - 现在 user 已经定义
            Integer cartCount = cartService.getCartItemCount(user.getId());
            System.out.println("购物车数量: " + cartCount);
            
            // 获取用户的商品（如果是卖家）
            List<Product> userProducts = null;
            int productCount = 0;
            
            // 检查是否是卖家
            boolean isSeller = user.getRoles().stream()
                .anyMatch(role -> role.getName().toString().equals("ROLE_SELLER"));
            
            System.out.println("用户是否为卖家: " + isSeller);
            
            if (isSeller) {
                userProducts = productService.getProductsByUserId(user.getId());
                productCount = userProducts != null ? userProducts.size() : 0;
                System.out.println("卖家商品数量: " + productCount);
            }
            
            // 添加到模型
            model.addAttribute("user", user);
            model.addAttribute("currentUser", user); // 用于导航栏
            model.addAttribute("userProducts", userProducts);
            model.addAttribute("productCount", productCount);
            model.addAttribute("cartCount", cartCount); // 添加购物车数量
            model.addAttribute("pageTitle", "个人资料 - " + (user.getFullName() != null ? user.getFullName() : user.getUsername()));
            
            System.out.println("准备返回个人资料模板");
            
            // 重要：返回 profile 页面，而不是跳转到商品管理
            return "user/profile";
            
        } catch (Exception e) {
            System.err.println("处理个人资料页时发生异常:");
            e.printStackTrace();
            model.addAttribute("error", "服务器内部错误: " + e.getMessage());
            model.addAttribute("pageTitle", "错误 - Shopping Mall");
            return "error/403";
        }
    }
    
    @GetMapping("/profile/edit")
    public String editProfilePage(Model model) {
        try {
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // 检查是否已认证
            if (!auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username).orElse(null);
            
            if (user == null) {
                model.addAttribute("error", "用户信息不存在");
                model.addAttribute("pageTitle", "错误 - Shopping Mall");
                return "error/403";
            }
            
            // 创建UserUpdateDto并填充当前用户数据
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setUsername(user.getUsername());
            updateDto.setEmail(user.getEmail());
            updateDto.setFullName(user.getFullName());
            updateDto.setPhoneNumber(user.getPhoneNumber());
            updateDto.setAddress(user.getAddress());
            
            model.addAttribute("userUpdateDto", updateDto);
            model.addAttribute("currentUser", user);
            model.addAttribute("pageTitle", "编辑资料 - " + user.getUsername());
            
            return "user/profile-edit";
            
        } catch (Exception e) {
            System.err.println("处理编辑资料页时发生异常:");
            e.printStackTrace();
            model.addAttribute("error", "服务器内部错误: " + e.getMessage());
            model.addAttribute("pageTitle", "错误 - Shopping Mall");
            return "error/403";
        }
    }
    
    @PostMapping("/profile/edit")
    public String updateProfile(@Valid @ModelAttribute("userUpdateDto") UserUpdateDto updateDto,
                               BindingResult result,
                               Model model) {
        try {
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // 获取当前用户
            User currentUser = userService.getUserByUsername(username).orElse(null);
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // 验证错误处理
            if (result.hasErrors()) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("pageTitle", "编辑资料 - " + currentUser.getUsername());
                return "user/profile-edit";
            }
            
            // 更新用户信息
            User updatedUser = userService.updateUser(currentUser.getId(), updateDto);
            
            // 更新成功后重定向到个人资料页
            model.addAttribute("success", "资料更新成功！");
            return "redirect:/profile?success";
            
        } catch (RuntimeException e) {
            // 获取当前用户以显示在错误页面
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentUser", currentUser);
            if (currentUser != null) {
                model.addAttribute("pageTitle", "编辑资料 - " + currentUser.getUsername());
            }
            return "user/profile-edit";
        } catch (Exception e) {
            System.err.println("更新用户资料时发生异常:");
            e.printStackTrace();
            
            // 获取当前用户以显示在错误页面
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            model.addAttribute("error", "服务器内部错误: " + e.getMessage());
            model.addAttribute("currentUser", currentUser);
            if (currentUser != null) {
                model.addAttribute("pageTitle", "编辑资料 - " + currentUser.getUsername());
            }
            return "user/profile-edit";
        }
    }
    
    @GetMapping("/profile/change-password")
    public String changePasswordPage(Model model) {
        try {
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // 检查是否已认证
            if (!auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                return "redirect:/login";
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username).orElse(null);
            
            if (user == null) {
                model.addAttribute("error", "用户信息不存在");
                model.addAttribute("pageTitle", "错误 - Shopping Mall");
                return "error/403";
            }
            
            // 创建ChangePasswordDto对象
            ChangePasswordDto changePasswordDto = new ChangePasswordDto();
            
            model.addAttribute("changePasswordDto", changePasswordDto);
            model.addAttribute("currentUser", user);
            model.addAttribute("pageTitle", "修改密码 - " + user.getUsername());
            
            return "user/change-password";
            
        } catch (Exception e) {
            System.err.println("处理修改密码页时发生异常:");
            e.printStackTrace();
            model.addAttribute("error", "服务器内部错误: " + e.getMessage());
            model.addAttribute("pageTitle", "错误 - Shopping Mall");
            return "error/403";
        }
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                                BindingResult result,
                                Model model) {
        try {
            // 获取当前认证信息
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // 获取当前用户
            User currentUser = userService.getUserByUsername(username).orElse(null);
            if (currentUser == null) {
                return "redirect:/login";
            }
            
            // 验证错误处理
            if (result.hasErrors()) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("pageTitle", "修改密码 - " + currentUser.getUsername());
                return "user/change-password";
            }
            
            // 检查新密码和确认密码是否一致
            if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
                model.addAttribute("passwordError", "两次输入的新密码不一致");
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("pageTitle", "修改密码 - " + currentUser.getUsername());
                return "user/change-password";
            }
            
            // 检查新密码是否与当前密码相同
            if (changePasswordDto.getNewPassword().equals(changePasswordDto.getCurrentPassword())) {
                model.addAttribute("error", "新密码不能与当前密码相同");
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("pageTitle", "修改密码 - " + currentUser.getUsername());
                return "user/change-password";
            }
            
            // 调用Service修改密码
            userService.changePassword(username, 
                changePasswordDto.getCurrentPassword(), 
                changePasswordDto.getNewPassword());
            
            // 密码修改成功后，重定向到个人资料页并显示成功消息
            model.addAttribute("success", "密码修改成功！下次登录请使用新密码");
            return "redirect:/profile?passwordChanged=true";
            
        } catch (RuntimeException e) {
            // 获取当前用户以显示在错误页面
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            model.addAttribute("error", e.getMessage());
            model.addAttribute("currentUser", currentUser);
            if (currentUser != null) {
                model.addAttribute("pageTitle", "修改密码 - " + currentUser.getUsername());
            }
            return "user/change-password";
        } catch (Exception e) {
            System.err.println("修改密码时发生异常:");
            e.printStackTrace();
            
            // 获取当前用户以显示在错误页面
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            User currentUser = userService.getUserByUsername(username).orElse(null);
            
            model.addAttribute("error", "服务器内部错误: " + e.getMessage());
            model.addAttribute("currentUser", currentUser);
            if (currentUser != null) {
                model.addAttribute("pageTitle", "修改密码 - " + currentUser.getUsername());
            }
            return "user/change-password";
        }
    }
    
    @GetMapping("/access-denied")
    public String accessDeniedPage(Model model) {
        model.addAttribute("pageTitle", "访问被拒绝");
        return "error/403";
    }
}