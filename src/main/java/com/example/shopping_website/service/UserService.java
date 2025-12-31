package com.example.shopping_website.service;

import com.example.shopping_website.dto.UserRegistrationDto;
import com.example.shopping_website.dto.UserUpdateDto;
import com.example.shopping_website.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public abstract class UserService {
    
    // 用户管理
    public abstract List<User> getAllUsers();
    public abstract Optional<User> getUserById(Long id);
    public abstract Optional<User> getUserByUsername(String username);
    public abstract boolean existsByUsername(String username);
    public abstract boolean existsByEmail(String email);
    
    // 用户注册
    @Transactional
    public abstract User registerUser(UserRegistrationDto registrationDto);
    
    // 用户更新
    @Transactional
    public abstract User updateUser(Long userId, UserUpdateDto updateDto);
    @Transactional
    public abstract User updateUserProfile(User user);
    @Transactional
    public abstract User updateUser(User user);
    
    // 用户角色管理
    @Transactional
    public abstract User upgradeToSeller();
    public abstract boolean isSeller(String username);
    public abstract boolean isAdmin(String username);
    
    // 用户删除
    @Transactional
    public abstract void deleteUser(Long id);
    
    // 密码管理
    public abstract boolean checkPassword(User user, String rawPassword);
    @Transactional
    public abstract void changePassword(String username, String oldPassword, String newPassword);
}