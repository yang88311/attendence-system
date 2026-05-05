package com.example.attendance.repository;

import com.example.attendance.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // 根据班级查询学生
    List<Student> findByClassName(String className);

    // 根据姓名模糊查询
    List<Student> findByStudentNameContaining(String keyword);

    // 根据班级和性别查询
    List<Student> findByClassNameAndGender(String className, String gender);
}