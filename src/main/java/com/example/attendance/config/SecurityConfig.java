package com.example.attendance.config;

import com.example.attendance.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 注册接口和注册页面允许所有人访问
                        .requestMatchers("/api/auth/register", "/register.html").permitAll()
                        // ========== 新增：权限控制 ==========
                        // 只有 ADMIN 角色能访问 /api/admin/**
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // ADMIN 和 TEACHER 角色都能访问 /api/teacher/**
                        .requestMatchers("/api/teacher/**").hasAnyRole("ADMIN", "TEACHER")
                        // ========== 新增结束 ==========
                        // 其他请求需要认证
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .permitAll()  // 使用 Spring Security 默认登录页
                )
                .userDetailsService(customUserDetailsService);


        return http.build();
    }
}