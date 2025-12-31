package com.example.shopping_website.service.impl;

import com.example.shopping_website.dto.UserRegistrationDto;
import com.example.shopping_website.dto.UserUpdateDto;
import com.example.shopping_website.model.Role;
import com.example.shopping_website.model.User;
import com.example.shopping_website.repository.RoleRepository;
import com.example.shopping_website.repository.UserRepository;
import com.example.shopping_website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl extends UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("用户名已被使用");
        }
        
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        // 检查密码是否一致
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setAddress(registrationDto.getAddress());
        
        // 设置角色 - 查找普通用户角色
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(Role.RoleName.ROLE_USER);
                return roleRepository.save(newRole);
            });
        
        user.setRoles(new HashSet<>());
        user.getRoles().add(userRole);
        
        // 如果是卖家，添加卖家角色
        if ("SELLER".equals(registrationDto.getUserType())) {
            Role sellerRole = roleRepository.findByName(Role.RoleName.ROLE_SELLER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.RoleName.ROLE_SELLER);
                    return roleRepository.save(newRole);
                });
            user.getRoles().add(sellerRole);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateDto updateDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名是否已存在（排除当前用户）
        if (!user.getUsername().equals(updateDto.getUsername()) && 
            userRepository.existsByUsername(updateDto.getUsername())) {
            throw new RuntimeException("用户名已被使用");
        }
        
        // 检查邮箱是否已存在（排除当前用户）
        if (!user.getEmail().equals(updateDto.getEmail()) && 
            userRepository.existsByEmail(updateDto.getEmail())) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        // 更新用户信息
        user.setUsername(updateDto.getUsername());
        user.setEmail(updateDto.getEmail());
        user.setFullName(updateDto.getFullName());
        user.setPhoneNumber(updateDto.getPhoneNumber());
        user.setAddress(updateDto.getAddress());
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User upgradeToSeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        User user = getUserByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查是否已经是卖家
        boolean isSeller = user.getRoles().stream()
            .anyMatch(role -> role.getName() == Role.RoleName.ROLE_SELLER);
        
        if (isSeller) {
            throw new RuntimeException("您已经是商家");
        }
        
        // 添加卖家角色
        Role sellerRole = roleRepository.findByName(Role.RoleName.ROLE_SELLER)
            .orElseThrow(() -> new RuntimeException("卖家角色不存在"));
        user.getRoles().add(sellerRole);
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUserProfile(User user) {
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    
    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
    User user = getUserByUsername(username)
        .orElseThrow(() -> new RuntimeException("用户不存在"));
    
    // 检查原密码是否正确
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new RuntimeException("当前密码不正确");
    }
    
    // 检查新密码是否与旧密码相同
    if (passwordEncoder.matches(newPassword, user.getPassword())) {
        throw new RuntimeException("新密码不能与当前密码相同");
    }
    
    // 加密新密码并保存
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    }
    
    @Override
    public boolean isSeller(String username) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent()) {
            return user.get().getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ROLE_SELLER);
        }
        return false;
    }
    
    @Override
    public boolean isAdmin(String username) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent()) {
            return user.get().getRoles().stream()
                .anyMatch(role -> role.getName() == Role.RoleName.ROLE_ADMIN);
        }
        return false;
    }
}