package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    // 注入 Service 层（用于新增的分层架构功能）
    @Autowired
    private StudentService studentService;

    // ==================== 你原来的接口（保留不变） ====================

    @GetMapping("/info")
    public String getStudentInfo() {
        return "姓名：白景一  学号：42411045  班级：计科2班";
    }

    @PostMapping("/attendance")
    public String attendance(@RequestBody String studentId) {
        return "学号为 " + studentId + " 的学生打卡成功！";
    }

    @GetMapping("/courses")
    public List<String> getCourses() {
        List<String> courses = new ArrayList<>();
        courses.add("Java程序设计");
        courses.add("数据结构");
        courses.add("数据库原理");
        courses.add("操作系统");
        courses.add("计算机网络");
        return courses;
    }

    // ==================== 新增：分层架构任务接口 ====================

    // 任务一：学生信息查询接口（路径参数）- 调用 Service 层
    @GetMapping("/info/{studentId}")
    public Result<Student> getStudentInfoById(@PathVariable String studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            return Result.error("学生不存在");
        }
        return Result.success(student);
    }

    // 任务二：学生列表查询接口（查询参数）- 调用 Service 层
    @GetMapping("/list")
    public Result<List<Student>> getStudentList(
            @RequestParam String className,
            @RequestParam(defaultValue = "1") int page) {
        List<Student> students = studentService.getStudentsByClass(className);
        return Result.success(students);
    }

    // 任务三：学生新增接口（POST）- 调用 Service 层
    @PostMapping("/create")
    public Result<String> createStudent(@RequestBody Student student) {
        try {
            String result = studentService.createStudent(student);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}