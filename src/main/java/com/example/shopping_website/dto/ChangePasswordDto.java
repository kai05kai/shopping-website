package com.example.shopping_website.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {
    
    @NotBlank(message = "当前密码不能为空")
    private String currentPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "新密码长度至少6位")
    private String newPassword;
    
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}