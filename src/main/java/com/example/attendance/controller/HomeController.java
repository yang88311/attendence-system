package com.example.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "登录成功！欢迎 " + org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
    }
}