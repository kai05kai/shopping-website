package com.example.shopping_website.controller;

import com.example.shopping_website.model.Product;
import com.example.shopping_website.model.User;
import com.example.shopping_website.service.ProductService;
import com.example.shopping_website.service.UserService;
import com.example.shopping_website.util.FileUploadUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
    // 获取当前用户
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户未登录"));
    }
    
    // ============== 商家后台：我的商品 ==============
    
    /**
     * 查看我的商品（商家后台和profile页面都指向这里）
     */
    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public String myProducts(Model model) {
        try {
            User user = getCurrentUser();
            
            // 获取该商家的所有商品
            List<Product> products = productService.getProductsByUserId(user.getId());
            
            // 计算统计信息
            int productCount = products.size();
            int totalStock = products.stream().mapToInt(Product::getStockQuantity).sum();
            double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStockQuantity()).sum();
            
            model.addAttribute("products", products);
            model.addAttribute("seller", user);
            model.addAttribute("productCount", productCount);
            model.addAttribute("totalStock", totalStock);
            model.addAttribute("totalValue", totalValue);
            model.addAttribute("pageTitle", "我的商品 - 商家后台");
            
            // 调试信息
            System.out.println("=== myProducts 方法 ===");
            System.out.println("用户: " + user.getUsername());
            System.out.println("商品数量: " + productCount);
            
            for (Product product : products) {
                System.out.println("商品: " + product.getName() + 
                                ", ID: " + product.getId() + 
                                ", 图片URL: " + product.getImageUrl());
                
                // 检查图片文件是否存在 - 使用兼容的方法
                String existingImage = fileUploadUtil.checkProductImageExists(product.getId());
                if (existingImage != null) {
                    System.out.println("  图片文件存在: " + existingImage);
                    System.out.println("  完整URL: " + fileUploadUtil.getImageUrl(existingImage));
                } else {
                    System.out.println("  图片文件不存在");
                }
            }
            
            return "product/my-products";
            
        } catch (Exception e) {
            model.addAttribute("error", "加载商品失败: " + e.getMessage());
            return "error/403";
        }
    }
    
    // ============== 添加商品 ==============
    
    /**
     * 显示添加商品页面
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('SELLER')")  // 确保有这个注解
    public String showAddProductForm(Model model) {
        try {
            model.addAttribute("product", new Product());
            model.addAttribute("pageTitle", "添加商品");
            
            // 调试信息
            System.out.println("显示添加商品页面");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("当前用户: " + auth.getName());
            System.out.println("用户权限: " + auth.getAuthorities());
            
            return "product/add";
        } catch (Exception e) {
            System.err.println("显示添加商品页面失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "无法加载添加商品页面: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * 处理商品添加
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('SELLER')")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                            BindingResult result,
                            @RequestParam("imageFile") MultipartFile imageFile,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始处理商品添加 ===");
            
            // 验证表单
            if (result.hasErrors()) {
                model.addAttribute("pageTitle", "添加商品");
                return "product/add";
            }
            
            // 设置当前用户为商品所有者
            User user = getCurrentUser();
            product.setUser(user);
            System.out.println("商品所有者: " + user.getUsername());
            
            // 先保存商品（此时还没有图片URL）
            System.out.println("保存商品到数据库...");
            Product savedProduct = productService.saveProduct(product);
            System.out.println("商品保存成功，ID: " + savedProduct.getId());
            
            // 处理图片上传
            if (imageFile != null && !imageFile.isEmpty()) {
                // 检查是否为图片文件
                if (!fileUploadUtil.isImageFile(imageFile)) {
                    model.addAttribute("error", "请上传有效的图片文件");
                    model.addAttribute("product", product);
                    model.addAttribute("pageTitle", "添加商品");
                    return "product/add";
                }
                
                // 删除可能存在的旧图片（如果有）
                fileUploadUtil.deleteProductImageByProductId(savedProduct.getId());
                
                // 使用产品ID作为文件名上传图片
                System.out.println("开始上传图片...");
                String fileName = fileUploadUtil.uploadProductImageWithId(imageFile, savedProduct.getId());
                
                if (fileName != null) {
                    // 更新商品的图片URL
                    savedProduct.setImageUrl(fileUploadUtil.getImageUrl(fileName));
                    productService.saveProduct(savedProduct);
                    System.out.println("商品图片已上传: " + fileName);
                    System.out.println("图片URL: " + savedProduct.getImageUrl());
                }
            } else {
                // 没有上传图片，使用默认图片
                savedProduct.setImageUrl(fileUploadUtil.getDefaultImageUrl());
                productService.saveProduct(savedProduct);
                System.out.println("使用默认图片: " + savedProduct.getImageUrl());
            }
            
            System.out.println("=== 商品添加完成 ===");
            redirectAttributes.addFlashAttribute("success", "商品添加成功！");
            return "redirect:/products/my-products";
            
        } catch (Exception e) {
            System.err.println("添加商品失败: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "添加商品失败: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "添加商品");
            return "product/add";
        }
    }

    
    // ============== 编辑商品 ==============
    
    /**
     * 显示编辑商品页面
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findProductById(id);
            
            // 检查当前用户是否有权限编辑此商品
            User currentUser = getCurrentUser();
            if (!product.getUser().getId().equals(currentUser.getId())) {
                model.addAttribute("error", "您没有权限编辑此商品");
                return "error/403";
            }
            
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "编辑商品");
            
            System.out.println("显示编辑商品页面: " + product.getName());
            System.out.println("当前图片URL: " + product.getImageUrl());
            
            return "product/edit";
        } catch (Exception e) {
            model.addAttribute("error", "加载商品失败: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * 处理商品编辑
     */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public String updateProduct(@PathVariable Long id,
                            @Valid @ModelAttribute("product") Product product,
                            BindingResult result,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始处理商品编辑 ===");
            System.out.println("商品ID: " + id);
            
            // 验证表单
            if (result.hasErrors()) {
                // 重新加载商品信息以显示在表单中
                Product existingProduct = productService.findProductById(id);
                model.addAttribute("product", existingProduct);
                model.addAttribute("pageTitle", "编辑商品");
                return "product/edit";
            }
            
            // 获取原始商品
            Product existingProduct = productService.findProductById(id);
            System.out.println("原始商品名称: " + existingProduct.getName());
            System.out.println("原始图片URL: " + existingProduct.getImageUrl());
            
            // 检查当前用户是否有权限编辑此商品
            User currentUser = getCurrentUser();
            if (!existingProduct.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "您没有权限编辑此商品");
                return "redirect:/products/my-products";
            }
            
            // 更新商品信息
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStockQuantity(product.getStockQuantity());
            
            // 处理图片上传
            if (imageFile != null && !imageFile.isEmpty()) {
                System.out.println("有新的图片文件上传");
                
                // 检查是否为图片文件
                if (!fileUploadUtil.isImageFile(imageFile)) {
                    model.addAttribute("error", "请上传有效的图片文件");
                    model.addAttribute("product", existingProduct);
                    model.addAttribute("pageTitle", "编辑商品");
                    return "product/edit";
                }
                
                // 删除旧图片（如果有）
                System.out.println("删除旧图片...");
                fileUploadUtil.deleteProductImageByProductId(existingProduct.getId());
                
                // 使用产品ID作为文件名上传新图片
                System.out.println("上传新图片...");
                String fileName = fileUploadUtil.uploadProductImageWithId(imageFile, existingProduct.getId());
                
                if (fileName != null) {
                    existingProduct.setImageUrl(fileUploadUtil.getImageUrl(fileName));
                    System.out.println("新图片URL: " + existingProduct.getImageUrl());
                } else {
                    // 上传失败，使用默认图片
                    existingProduct.setImageUrl(fileUploadUtil.getDefaultImageUrl());
                    System.out.println("图片上传失败，使用默认图片");
                }
            } else {
                System.out.println("没有上传新图片，保持原有图片");
            }
            
            // 保存更新
            Product updatedProduct = productService.saveProduct(existingProduct);
            System.out.println("商品更新成功");
            System.out.println("更新后图片URL: " + updatedProduct.getImageUrl());
            
            System.out.println("=== 商品编辑完成 ===");
            redirectAttributes.addFlashAttribute("success", "商品更新成功！");
            return "redirect:/products/my-products";
            
        } catch (Exception e) {
            System.err.println("更新商品失败: " + e.getMessage());
            e.printStackTrace();
            Product existingProduct = productService.findProductById(id);
            model.addAttribute("product", existingProduct);
            model.addAttribute("error", "更新商品失败: " + e.getMessage());
            model.addAttribute("pageTitle", "编辑商品");
            return "product/edit";
        }
    }
    
    // ============== 删除商品 ==============
    
    /**
     * 删除商品
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== 开始删除商品 ===");
            System.out.println("商品ID: " + id);
            
            // 获取商品
            Product product = productService.findProductById(id);
            System.out.println("商品名称: " + product.getName());
            System.out.println("商品图片URL: " + product.getImageUrl());
            
            // 检查当前用户是否有权限删除此商品
            User currentUser = getCurrentUser();
            if (!product.getUser().getId().equals(currentUser.getId())) {
                redirectAttributes.addFlashAttribute("error", "您没有权限删除此商品");
                return "redirect:/products/my-products";
            }
            
            // 删除商品图片（如果有）
            System.out.println("删除商品图片...");
            boolean deleted = fileUploadUtil.deleteProductImageByProductId(product.getId());
            if (deleted) {
                System.out.println("图片删除成功");
            } else {
                System.out.println("没有找到要删除的图片");
            }
            
            // 删除商品
            productService.deleteProduct(id);
            System.out.println("商品从数据库删除成功");
            
            System.out.println("=== 商品删除完成 ===");
            redirectAttributes.addFlashAttribute("success", "商品删除成功！");
            return "redirect:/products/my-products";
            
        } catch (Exception e) {
            System.err.println("删除商品失败: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "删除商品失败: " + e.getMessage());
            return "redirect:/products/my-products";
        }
    }
    
    // ============== 公共商品页面 ==============
    
    /**
     * 查看商品详情（公共页面）
     */
    @GetMapping("/details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        try {
            Product product = productService.findProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", product.getName() + " - 商品详情");
            return "product/details";
        } catch (Exception e) {
            model.addAttribute("error", "加载商品失败: " + e.getMessage());
            return "error/403";
        }
    }
    
    /**
     * 查看所有商品（公共页面）
     */
    @GetMapping("/list")
    public String listProducts(Model model) {
        try {
            List<Product> products = productService.getAllProducts();
            
            // 确保每个商品都有正确的图片URL
            for (Product product : products) {
                if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
                    // 设置默认图片路径
                    product.setImageUrl(fileUploadUtil.getDefaultImageUrl());
                }
            }
            
            model.addAttribute("products", products);
            model.addAttribute("pageTitle", "所有商品");
            return "product/list";
        } catch (Exception e) {
            model.addAttribute("error", "加载商品列表失败: " + e.getMessage());
            return "error/error";
        }
    }
    
}