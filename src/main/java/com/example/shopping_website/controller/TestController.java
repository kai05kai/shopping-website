package com.example.shopping_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    
    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "测试页面 - 如果能看到这个，说明基本映射工作正常");
        return "test/simple";
    }
    
    @GetMapping("/test-profile")
    public String testProfile(Model model) {
        model.addAttribute("pageTitle", "测试个人资料");
        model.addAttribute("message", "这是测试的个人资料页面");
        return "user/profile";
    }
}