package com.example.shopping_website.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 允许公开访问的路径
                .requestMatchers(
                    "/",
                    "/home",
                    "/register",
                    "/login",
                    "/logout",
                    "/error/**",
                    "/access-denied",
                    "/test/**",
                    "/test-simple",
                    "/test-cart"
                ).permitAll()
                
                // 允许静态资源访问 - 修复这里！
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/product_images/**",
                    "/user/images/**",
                    "/uploads/**",
                    "/static/**",  // 添加这行
                    "/favicon.ico"
                ).permitAll()
                
                // 商品相关页面允许公开访问
                .requestMatchers(
                    "/products/list", 
                    "/products/details/**",
                    "/products/**"  // 暂时允许所有商品相关访问，生产环境需要细化
                ).permitAll()
                
                // 搜索功能允许公开访问
                .requestMatchers("/search").permitAll()
                
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .csrf(csrf -> csrf.disable());  // 开发阶段先禁用CSRF，方便测试
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}