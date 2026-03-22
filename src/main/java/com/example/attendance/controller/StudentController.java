package com.example.attendance.controller;

import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.common.Result;
import com.example.attendance.entity.Student;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/student")
public class StudentController {
    @GetMapping("/info")
    public String getStudentInfo(){
        return "姓名：白景一  学号：42411045  班级：计科2班";
    }
    @PostMapping("/attendance")
    public String attendence(@RequestBody String studentId){
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
        return courses;}
    // 任务一：学生信息查询接口（路径参数）
    @GetMapping("/info/{studentId}")
    public Result<Student> getStudentInfo(@PathVariable String studentId) {
        // 1. 模拟根据 studentId 查询到的学生数据（后续可替换为数据库查询）
        Student student = new Student();
        student.setStudentId(studentId);
        student.setName("白景一");
        student.setClassName("计科2班");
        student.setAge(20);

        // 2. 封装成统一响应格式返回
        return Result.success(student);
    }

    // 任务二：查询参数获取学生列表
    @GetMapping("/list")
    public Result<List<Student>> getStudentList(
            @RequestParam String className,
            @RequestParam(defaultValue = "1") int page) {

        List<Student> students = new ArrayList<>();
        students.add(new Student("42411045", "白景一", className, 20));
        students.add(new Student("42411046", "李四", className, 19));
        return Result.success(students);
    }

}
