package com.example.shopping_website.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileUploadUtil {
    
    // 上传目录 - 项目结构中的位置
    private final String UPLOAD_DIR;
    
    // Web访问路径
    private final String ACCESS_PATH = "/product_images/uploads/";
    
    public FileUploadUtil() {
        // 获取当前工作目录并构建上传路径
        String userDir = System.getProperty("user.dir");
        this.UPLOAD_DIR = userDir + "/src/main/resources/static/product_images/uploads/";
        System.out.println("上传目录设置为: " + UPLOAD_DIR);
        
        // 创建目录
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("创建上传目录: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("创建上传目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传商品图片 - 使用产品ID作为文件名（格式：product_{id}.{extension}）
     * @param file 上传的文件
     * @param productId 商品ID
     * @return 保存的文件名（不包含路径）
     * @throws IOException
     */
    public String uploadProductImageWithId(MultipartFile file, Long productId) throws IOException {
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
        
        // 生成文件名：格式为 product_{id}.{extension} （不要时间戳）
        String fileName = "product_" + productId + fileExtension;
        
        // 确保目录存在
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("创建上传目录: " + uploadPath.toAbsolutePath());
        }
        
        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        System.out.println("========== 图片上传成功 ==========");
        System.out.println("产品ID: " + productId);
        System.out.println("原始文件名: " + originalFilename);
        System.out.println("生成文件名: " + fileName);
        System.out.println("存储路径: " + filePath.toAbsolutePath());
        System.out.println("Web访问URL: " + ACCESS_PATH + fileName);
        System.out.println("==================================");
        
        return fileName;
    }
    
    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名，如 ".jpg"
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return ".jpg";
        }
        
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0) {
            String extension = filename.substring(dotIndex).toLowerCase();
            // 确保扩展名是有效的图片格式
            if (extension.matches("(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.bmp|\\.webp)")) {
                return extension;
            }
        }
        
        // 默认使用.jpg
        return ".jpg";
    }
    
    /**
     * 获取图片的Web访问路径
     * @param fileName 文件名
     * @return Web访问路径
     */
    public String getImageUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return getDefaultImageUrl();
        }
        
        // 如果已经是完整路径，直接返回
        if (fileName.startsWith("/") || fileName.startsWith("http")) {
            return fileName;
        }
        
        // 否则添加前缀
        return ACCESS_PATH + fileName;
    }
    
    /**
     * 删除商品图片
     * @param fileName 文件名
     * @return 是否删除成功
     */
    public boolean deleteProductImage(String fileName) {
        try {
            if (fileName == null || fileName.isEmpty() || fileName.equals("default.jpg")) {
                return false;
            }
            
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
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
     * 根据产品ID删除图片
     * @param productId 产品ID
     * @return 是否删除成功
     */
    public boolean deleteProductImageByProductId(Long productId) {
        try {
            if (productId == null) {
                return false;
            }
            
            System.out.println("尝试删除产品ID为 " + productId + " 的所有图片");
            
            // 获取目录中的所有文件
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                return false;
            }
            
            boolean deletedAny = false;
            try (var dirStream = Files.newDirectoryStream(uploadPath)) {
                for (Path filePath : dirStream) {
                    if (Files.isRegularFile(filePath)) {
                        String fileName = filePath.getFileName().toString();
                        
                        // 检查文件名是否符合 product_{id} 格式
                        if (fileName.startsWith("product_" + productId + ".")) {
                            boolean deleted = Files.deleteIfExists(filePath);
                            if (deleted) {
                                System.out.println("删除图片: " + fileName);
                                deletedAny = true;
                            }
                        }
                    }
                }
            }
            
            return deletedAny;
        } catch (IOException e) {
            System.err.println("根据产品ID删除图片失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查文件是否为图片
     * @param file 文件
     * @return 是否为图片
     */
    public boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // 检查MIME类型
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }
        
        // 检查文件扩展名
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
     * @return 默认图片URL
     */
    public String getDefaultImageUrl() {
        return ACCESS_PATH + "default.jpg";
    }
    
    /**
     * 获取上传目录
     * @return 上传目录路径
     */
    public String getUploadDir() {
        return UPLOAD_DIR;
    }
    
    /**
     * 获取访问路径前缀
     * @return 访问路径前缀
     */
    public String getAccessPath() {
        return ACCESS_PATH;
    }
    
    /**
     * 检查指定产品ID的图片是否存在
     * @param productId 产品ID
     * @return 如果存在返回文件名，否则返回null
     */
    public String checkProductImageExists(Long productId) {
        if (productId == null) {
            return null;
        }
        
        String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        
        for (String ext : extensions) {
            String fileName = "product_" + productId + ext;
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            
            if (Files.exists(filePath)) {
                System.out.println("找到图片文件: " + fileName);
                return fileName;
            }
        }
        
        System.out.println("未找到产品ID为 " + productId + " 的图片");
        return null;
    }
}