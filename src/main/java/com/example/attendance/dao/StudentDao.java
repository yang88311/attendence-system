package com.example.attendance.dao;

import com.example.attendance.entity.Student;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StudentDao {

    // 用 Map 模拟数据库表
    private Map<String, Student> studentDB = new ConcurrentHashMap<>();

    public void insert(Student student) {
        studentDB.put(student.getStudentId(), student);
        System.out.println("模拟插入成功: " + student.getName());
    }

    public Student findById(String studentId) {
        return studentDB.get(studentId);
    }

    public List<Student> findByClassName(String className) {
        List<Student> result = new ArrayList<>();
        for (Student student : studentDB.values()) {
            if (className.equals(student.getClassName())) {
                result.add(student);
            }
        }
        return result;
    }

    // 可选：查看所有数据（调试用）
    public Map<String, Student> findAll() {
        return studentDB;
    }
}