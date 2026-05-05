package com.example.attendance.controller;

import com.example.attendance.entity.User;
import com.example.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. 新增用户
    @PostMapping
    public String addUser(@RequestBody User user) {
        userService.addUser(user);
        return "添加成功";
    }

    // 2. 根据ID查询
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // 3. 根据用户名查询
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    // 4. 查询所有教师
    @GetMapping("/teachers")
    public List<User> getAllTeachers() {
        return userService.getAllTeachers();
    }

    // 5. 查询所有用户
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 6. 更新用户
    @PutMapping
    public String updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return "更新成功";
    }

    // 7. 删除用户
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "删除成功";
    }
}