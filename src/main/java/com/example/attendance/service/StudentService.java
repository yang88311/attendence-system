package com.example.attendance.service;

import com.example.attendance.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentService {

    // 新增学生
    Student saveStudent(Student student);

    // 根据ID查询
    Student getStudentById(String studentId);

    // 根据班级查询
    List<Student> getStudentsByClass(String className);

    // 查询所有
    List<Student> getAllStudents();

    // 删除学生
    void deleteStudent(String studentId);

    // 分页查询
    Page<Student> getStudentsWithPagination(Pageable pageable);

    // ========== 新增：搜索功能 ==========
    Page<Student> searchStudents(String keyword, Pageable pageable);

    // ========== 新增：批量删除 ==========
    void deleteStudents(List<String> studentIds);
}