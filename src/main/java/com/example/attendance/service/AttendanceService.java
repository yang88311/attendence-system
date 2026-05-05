package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    List<Attendance> getAllAttendances();

    Attendance getAttendanceById(Integer id);

    Attendance saveAttendance(Attendance attendance);

    void deleteAttendance(Integer id);

    List<Attendance> getAttendancesByStudent(String studentId);

    List<Attendance> getAttendancesByCourse(String courseId);

    List<Attendance> getAttendancesByCourseAndDate(String courseId, LocalDate date);

    // 分页查询
    Page<Attendance> getAttendancesWithPagination(Pageable pageable);

    // 按学生ID分页
    Page<Attendance> getAttendancesByStudentWithPagination(String studentId, Pageable pageable);

    // 多条件动态查询（分页）
    Page<Attendance> searchAttendances(String studentId, String courseId,
                                       LocalDate startDate, LocalDate endDate,
                                       String status, Pageable pageable);
}