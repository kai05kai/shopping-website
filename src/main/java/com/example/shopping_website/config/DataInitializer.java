package com.example.shopping_website.config;

import com.example.shopping_website.model.Role;
import com.example.shopping_website.model.User;
import com.example.shopping_website.repository.RoleRepository;
import com.example.shopping_website.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 开始初始化数据 ===");
        
        // 初始化角色
        initializeRoles();
        
        // 初始化用户
        initializeUsers();
        
        System.out.println("=== 数据初始化完成 ===");
    }
    
    private void initializeRoles() {
        System.out.println("正在初始化角色...");
        
        // 创建角色
        Role roleUser = new Role();
        roleUser.setName(Role.RoleName.ROLE_USER);
        
        Role roleSeller = new Role();
        roleSeller.setName(Role.RoleName.ROLE_SELLER);
        
        Role roleAdmin = new Role();
        roleAdmin.setName(Role.RoleName.ROLE_ADMIN);
        
        // 保存角色
        if (roleRepository.findByName(Role.RoleName.ROLE_USER).isEmpty()) {
            roleRepository.save(roleUser);
            System.out.println("✅ 普通用户角色已创建");
        }
        if (roleRepository.findByName(Role.RoleName.ROLE_SELLER).isEmpty()) {
            roleRepository.save(roleSeller);
            System.out.println("✅ 卖家角色已创建");
        }
        if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(roleAdmin);
            System.out.println("✅ 管理员角色已创建");
        }
    }
    
    private void initializeUsers() {
        System.out.println("正在初始化用户...");
        
        // 获取角色
        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("管理员角色不存在"));
        Role sellerRole = roleRepository.findByName(Role.RoleName.ROLE_SELLER)
            .orElseThrow(() -> new RuntimeException("卖家角色不存在"));
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("普通用户角色不存在"));
        
        // 1. 创建管理员 (admin / admin123)
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@shopping.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("系统管理员");
            admin.setPhoneNumber("13800138000");
            admin.setAddress("北京市海淀区");
            admin.setEnabled(true);
            
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            
            userRepository.save(admin);
            System.out.println("✅ 管理员账户已创建: admin / admin123");
        } else {
            System.out.println("⏭️  管理员账户已存在，跳过创建");
        }
        
        // 2. 创建商家1 (seller1 / seller123)
        if (userRepository.findByUsername("seller1").isEmpty()) {
            User seller1 = new User();
            seller1.setUsername("seller1");
            seller1.setEmail("seller1@shopping.com");
            seller1.setPassword(passwordEncoder.encode("seller123"));
            seller1.setFullName("数码电器专卖店");
            seller1.setPhoneNumber("13800138001");
            seller1.setAddress("上海市浦东新区");
            seller1.setEnabled(true);
            
            Set<Role> seller1Roles = new HashSet<>();
            seller1Roles.add(sellerRole);
            seller1.setRoles(seller1Roles);
            
            userRepository.save(seller1);
            System.out.println("✅ 商家1账户已创建: seller1 / seller123");
        } else {
            System.out.println("⏭️  商家1账户已存在，跳过创建");
        }
        
        // 3. 创建商家2 (seller2 / seller456)
        if (userRepository.findByUsername("seller2").isEmpty()) {
            User seller2 = new User();
            seller2.setUsername("seller2");
            seller2.setEmail("seller2@shopping.com");
            seller2.setPassword(passwordEncoder.encode("seller456"));
            seller2.setFullName("时尚服装店");
            seller2.setPhoneNumber("13800138002");
            seller2.setAddress("广州市天河区");
            seller2.setEnabled(true);
            
            Set<Role> seller2Roles = new HashSet<>();
            seller2Roles.add(sellerRole);
            seller2.setRoles(seller2Roles);
            
            userRepository.save(seller2);
            System.out.println("✅ 商家2账户已创建: seller2 / seller456");
        } else {
            System.out.println("⏭️  商家2账户已存在，跳过创建");
        }
        
        // 4. 创建用户1 (user1 / user123)
        if (userRepository.findByUsername("user1").isEmpty()) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setEmail("user1@shopping.com");
            user1.setPassword(passwordEncoder.encode("user123"));
            user1.setFullName("张三");
            user1.setPhoneNumber("13900139001");
            user1.setAddress("杭州市西湖区");
            user1.setEnabled(true);
            
            Set<Role> user1Roles = new HashSet<>();
            user1Roles.add(userRole);
            user1.setRoles(user1Roles);
            
            userRepository.save(user1);
            System.out.println("✅ 用户1账户已创建: user1 / user123");
        } else {
            System.out.println("⏭️  用户1账户已存在，跳过创建");
        }
        
        // 5. 创建用户2 (user2 / user456)
        if (userRepository.findByUsername("user2").isEmpty()) {
            User user2 = new User();
            user2.setUsername("user2");
            user2.setEmail("user2@shopping.com");
            user2.setPassword(passwordEncoder.encode("user456"));
            user2.setFullName("李四");
            user2.setPhoneNumber("13900139002");
            user2.setAddress("成都市武侯区");
            user2.setEnabled(true);
            
            Set<Role> user2Roles = new HashSet<>();
            user2Roles.add(userRole);
            user2.setRoles(user2Roles);
            
            userRepository.save(user2);
            System.out.println("✅ 用户2账户已创建: user2 / user456");
        } else {
            System.out.println("⏭️  用户2账户已存在，跳过创建");
        }
    }
}