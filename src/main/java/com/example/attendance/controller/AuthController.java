package com.example.attendance.controller;

import com.example.attendance.dto.LoginRequest;
import com.example.attendance.dto.RegisterRequest;
import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 任务1：登录接口
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 1. 根据用户名查询用户
        User user = userService.getUserByUsername(request.getUsername());

        // 2. 验证用户名和密码
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户名不存在");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 3. 验证密码（BCrypt）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.put("success", false);
            response.put("message", "密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 4. 登录成功，返回用户信息
        response.put("success", true);
        response.put("message", "登录成功");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("realName", user.getRealName());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    // 任务2：注册接口
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 1. 检查用户名是否已存在
        User existingUser = userService.getUserByUsername(request.getUsername());
        if (existingUser != null) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. 创建新用户
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        // 3. 密码加密存储
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRealName(request.getRealName());
        newUser.setRole(request.getRole() != null ? request.getRole() : "TEACHER");
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());

        // 4. 保存用户信息
        try {
            userService.addUser(newUser);
            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========== 新增：权限测试接口 ==========

    // 只有 ADMIN 角色能访问
    @GetMapping("/admin/test")
    public ResponseEntity<Map<String, Object>> adminTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "只有管理员能访问这个接口");
        response.put("role", "ADMIN");
        return ResponseEntity.ok(response);
    }

    // ADMIN 和 TEACHER 角色都能访问
    @GetMapping("/teacher/test")
    public ResponseEntity<Map<String, Object>> teacherTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "教师和管理员都能访问这个接口");
        return ResponseEntity.ok(response);
    }
}