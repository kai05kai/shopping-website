package com.example.shopping_website.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ImageNameValidator {
    
    /**
     * 验证图片文件名是否符合 product_{id} 格式
     * @param filename 文件名
     * @return 是否符合格式
     */
    public boolean isValidProductImageName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        // 匹配 product_{数字}.{扩展名} 格式
        // 例如: product_1.jpg, product_123.png
        String pattern = "^product_\\d+(\\.jpg|\\.jpeg|\\.png|\\.gif|\\.bmp|\\.webp)$";
        return Pattern.matches(pattern, filename.toLowerCase());
    }
    
    /**
     * 从文件名中提取产品ID
     * @param filename 文件名
     * @return 产品ID，如果不符合格式则返回null
     */
    public Long extractProductIdFromName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        
        try {
            // 移除扩展名
            String nameWithoutExt = filename;
            int dotIndex = filename.lastIndexOf(".");
            if (dotIndex > 0) {
                nameWithoutExt = filename.substring(0, dotIndex);
            }
            
            // 提取ID
            if (nameWithoutExt.startsWith("product_")) {
                String idStr = nameWithoutExt.substring("product_".length());
                return Long.parseLong(idStr);
            }
        } catch (Exception e) {
            System.err.println("提取产品ID失败: " + e.getMessage());
        }
        
        return null;
    }
}