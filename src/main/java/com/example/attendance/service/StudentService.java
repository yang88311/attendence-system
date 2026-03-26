package com.example.attendance.service;

import com.example.attendance.entity.Student;
import java.util.List;

public interface StudentService {

    String createStudent(Student student);

    Student getStudentById(String studentId);

    List<Student> getStudentsByClass(String className);
}