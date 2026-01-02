package com.example.shopping_website.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FileUploadUtil {
    
    // 从配置文件读取上传目录
    @Value("${app.upload.dir:/home/shopping-website/uploads}")
    private String uploadDir;
    
    // Web访问路径前缀
    private final String ACCESS_PATH = "/uploads/";
    
    // 旧路径格式支持
    private final String OLD_ACCESS_PATH = "/product_images/uploads/";
    
    public FileUploadUtil() {
        // 构造函数，确保目录存在
        System.out.println("FileUploadUtil构造函数调用，uploadDir还未注入");
    }
    
    @PostConstruct
    public void init() {
        // 在依赖注入完成后执行
        System.out.println("FileUploadUtil初始化，uploadDir=" + uploadDir);
        ensureUploadDirExists();
    }
    
    /**
     * 确保上传目录存在
     */
    private void ensureUploadDirExists() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("创建上传目录: " + uploadPath.toAbsolutePath());
            }
            
            // 创建产品图片子目录
            Path productPath = uploadPath.resolve("products");
            if (!Files.exists(productPath)) {
                Files.createDirectories(productPath);
                System.out.println("创建产品图片目录: " + productPath.toAbsolutePath());
            }
            
            // 创建用户头像子目录
            Path avatarPath = uploadPath.resolve("avatars");
            if (!Files.exists(avatarPath)) {
                Files.createDirectories(avatarPath);
                System.out.println("创建用户头像目录: " + avatarPath.toAbsolutePath());
            }
            
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
            // 如果创建失败，使用临时目录作为后备
            this.uploadDir = System.getProperty("java.io.tmpdir") + "/shopping-uploads";
            System.err.println("使用临时目录作为后备: " + this.uploadDir);
        }
    }
    
    /**
     * 上传商品图片 - 兼容旧版本的方法
     */
    public String uploadProductImageWithId(MultipartFile file, Long productId) throws IOException {
        return uploadProductImage(file, productId, false);
    }
    
    /**
     * 上传商品图片（新版方法）
     */
    public String uploadProductImage(MultipartFile file, Long productId) throws IOException {
        return uploadProductImage(file, productId, true);
    }
    
    /**
     * 通用的图片上传方法
     */
    private String uploadProductImage(MultipartFile file, Long productId, boolean useTimestamp) throws IOException {
        if (file == null || file.isEmpty()) {
            System.out.println("文件为空，不上传图片");
            return null;
        }
        
        // 检查是否为图片文件
        if (!isImageFile(file)) {
            throw new IOException("文件必须是图片格式 (JPG, PNG, GIF, BMP, WEBP)");
        }
        
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        
        // 生成文件名
        String fileName;
        if (useTimestamp) {
            // 新格式：product_{id}_{timestamp}.{extension}
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            fileName = "product_" + productId + "_" + timestamp + fileExtension;
        } else {
            // 旧格式：product_{id}.{extension}
            fileName = "product_" + productId + fileExtension;
        }
        
        // 确保产品图片目录存在
        Path productDir = Paths.get(uploadDir, "products");
        if (!Files.exists(productDir)) {
            Files.createDirectories(productDir);
        }
        
        // 保存文件
        Path filePath = productDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("========== 图片上传成功 ==========");
        System.out.println("产品ID: " + productId);
        System.out.println("原始文件名: " + originalFilename);
        System.out.println("生成文件名: " + fileName);
        System.out.println("存储路径: " + filePath.toAbsolutePath());
        System.out.println("Web访问URL: " + ACCESS_PATH + "products/" + fileName);
        System.out.println("==================================");
        
        return fileName;
    }
    
    /**
     * 上传用户头像
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            System.out.println("文件为空，不上传头像");
            return null;
        }
        
        if (!isImageFile(file)) {
            throw new IOException("文件必须是图片格式");
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "avatar_" + userId + "_" + timestamp + fileExtension;
        
        // 确保头像目录存在
        Path avatarDir = Paths.get(uploadDir, "avatars");
        if (!Files.exists(avatarDir)) {
            Files.createDirectories(avatarDir);
        }
        
        Path filePath = avatarDir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "avatars/" + fileName;
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return ".jpg";
        }
        
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            String extension = filename.substring(dotIndex).toLowerCase();
            if (extension.matches("(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.bmp|\\.webp)")) {
                return extension;
            }
        }
        
        return ".jpg";
    }
    
    /**
     * 获取图片的Web访问路径 - 兼容新旧格式
     */
    public String getImageUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return getDefaultImageUrl();
        }
        
        // 如果已经是完整URL，直接返回
        if (fileName.startsWith("http")) {
            return fileName;
        }
        
        // 如果是旧格式（/product_images/uploads/...），转换为新格式
        if (fileName.startsWith("/product_images/uploads/")) {
            // 移除旧前缀，保留文件名
            String pureFileName = fileName.substring("/product_images/uploads/".length());
            return ACCESS_PATH + "products/" + pureFileName;
        }
        
        // 如果是相对路径（如 product_1.jpg 或 products/product_1.jpg）
        if (!fileName.startsWith("/")) {
            // 检查是否已经是 products/ 开头的相对路径
            if (fileName.startsWith("products/")) {
                return ACCESS_PATH + fileName;
            } else {
                // 如果不是，添加到 products/ 目录下
                return ACCESS_PATH + "products/" + fileName;
            }
        }
        
        // 直接返回
        return fileName;
    }
    
    /**
     * 删除商品图片 - 兼容旧版本方法
     */
    public boolean deleteProductImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        try {
            // 如果是完整URL，提取文件名
            if (fileName.contains("/")) {
                String[] parts = fileName.split("/");
                fileName = parts[parts.length - 1];
            }
            
            // 查找并删除文件
            Path productDir = Paths.get(uploadDir, "products");
            if (!Files.exists(productDir)) {
                return false;
            }
            
            Path filePath = productDir.resolve(fileName);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                System.out.println("图片删除成功: " + fileName);
            }
            
            return deleted;
        } catch (IOException e) {
            System.err.println("删除图片失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 根据产品ID删除所有相关图片 - 兼容旧版本方法
     */
    public boolean deleteProductImageByProductId(Long productId) {
        return deleteProductImagesByProductId(productId);
    }
    
    /**
     * 根据产品ID删除所有相关图片（新版方法）
     */
    public boolean deleteProductImagesByProductId(Long productId) {
        try {
            if (productId == null) {
                return false;
            }
            
            System.out.println("尝试删除产品ID为 " + productId + " 的所有图片");
            Path productDir = Paths.get(uploadDir, "products");
            
            if (!Files.exists(productDir)) {
                return false;
            }
            
            boolean deletedAny = false;
            
            // 删除旧格式：product_{id}.{extension}
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
            for (String ext : extensions) {
                String oldFileName = "product_" + productId + ext;
                Path oldFilePath = productDir.resolve(oldFileName);
                if (Files.exists(oldFilePath)) {
                    boolean deleted = Files.deleteIfExists(oldFilePath);
                    if (deleted) {
                        System.out.println("删除旧格式图片: " + oldFileName);
                        deletedAny = true;
                    }
                }
            }
            
            // 删除新格式：product_{id}_{timestamp}.{extension}
            try (var dirStream = Files.newDirectoryStream(productDir, "product_" + productId + "_*")) {
                for (Path filePath : dirStream) {
                    if (Files.isRegularFile(filePath)) {
                        boolean deleted = Files.deleteIfExists(filePath);
                        if (deleted) {
                            System.out.println("删除新格式图片: " + filePath.getFileName());
                            deletedAny = true;
                        }
                    }
                }
            } catch (IOException e) {
                // 忽略目录流错误
            }
            
            return deletedAny;
        } catch (IOException e) {
            System.err.println("根据产品ID删除图片失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查指定产品ID的图片是否存在 - 兼容旧版本方法
     */
    public String checkProductImageExists(Long productId) {
        if (productId == null) {
            return null;
        }
        
        System.out.println("检查产品ID为 " + productId + " 的图片");
        Path productDir = Paths.get(uploadDir, "products");
        
        if (!Files.exists(productDir)) {
            return null;
        }
        
        // 先检查旧格式：product_{id}.{extension}
        String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        for (String ext : extensions) {
            String fileName = "product_" + productId + ext;
            Path filePath = productDir.resolve(fileName);
            if (Files.exists(filePath)) {
                System.out.println("找到旧格式图片: " + fileName);
                return fileName;
            }
        }
        
        // 再检查新格式：product_{id}_{timestamp}.{extension}
        try (var dirStream = Files.newDirectoryStream(productDir, "product_" + productId + "_*")) {
            for (Path filePath : dirStream) {
                if (Files.isRegularFile(filePath)) {
                    String fileName = filePath.getFileName().toString();
                    System.out.println("找到新格式图片: " + fileName);
                    return fileName;
                }
            }
        } catch (IOException e) {
            // 忽略目录流错误
        }
        
        System.out.println("未找到产品ID为 " + productId + " 的图片");
        return null;
    }
    
    /**
     * 检查文件是否为图片
     */
    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        
        if (originalFilename != null) {
            String lowerCaseFilename = originalFilename.toLowerCase();
            return lowerCaseFilename.endsWith(".jpg") || 
                   lowerCaseFilename.endsWith(".jpeg") || 
                   lowerCaseFilename.endsWith(".png") || 
                   lowerCaseFilename.endsWith(".gif") ||
                   lowerCaseFilename.endsWith(".bmp") ||
                   lowerCaseFilename.endsWith(".webp");
        }
        
        return false;
    }
    
    /**
     * 获取默认图片URL
     */
    public String getDefaultImageUrl() {
        // 返回静态资源中的默认图片
        return "/static/product_images/uploads/default.jpg";
    }
    
    /**
     * 获取上传目录绝对路径
     */
    public String getUploadDir() {
        return uploadDir;
    }
    
    /**
     * 获取Web访问路径前缀
     */
    public String getAccessPath() {
        return ACCESS_PATH;
    }
    
    /**
     * 获取产品的第一张图片（如果有） - 新版方法
     */
    public String getFirstProductImage(Long productId) {
        return checkProductImageExists(productId);
    }
    
    /**
     * 获取完整文件路径
     */
    public Path getProductImagePath(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }
        
        // 如果文件名包含 products/ 前缀，直接使用
        if (fileName.startsWith("products/")) {
            return Paths.get(uploadDir, fileName);
        }
        
        // 否则，添加到 products/ 目录下
        return Paths.get(uploadDir, "products", fileName);
    }
}