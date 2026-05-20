package com.example.attendance.repository;

import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer>, JpaSpecificationExecutor<Attendance> {

    // ========== 原有方法 ==========

    List<Attendance> findByStudentId(String studentId);

    List<Attendance> findByCourseId(String courseId);

    List<Attendance> findByCourseIdAndAttendanceDate(String courseId, LocalDate attendanceDate);

    List<Attendance> findByStudentIdAndCourseId(String studentId, String courseId);

    List<Attendance> findByStatus(String status);

    // 分页查询所有
    Page<Attendance> findAll(Pageable pageable);

    // 按学生ID分页查询
    Page<Attendance> findByStudentId(String studentId, Pageable pageable);

    // 按课程ID分页查询
    Page<Attendance> findByCourseId(String courseId, Pageable pageable);

    // 按状态分页查询
    Page<Attendance> findByStatus(String status, Pageable pageable);

    // ========== 新增方法（考勤打卡功能） ==========

    // 按学生ID和课程ID分页查询（用于筛选）
    Page<Attendance> findByStudentIdAndCourseId(String studentId, String courseId, Pageable pageable);

    // 按学生ID和日期范围分页查询
    Page<Attendance> findByStudentIdAndAttendanceDateBetween(String studentId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 按学生ID、课程ID、日期范围分页查询
    Page<Attendance> findByStudentIdAndCourseIdAndAttendanceDateBetween(String studentId, String courseId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // 按学生ID、课程ID、日期范围查询（不分页，用于导出）
    List<Attendance> findByStudentIdAndCourseIdAndAttendanceDateBetween(String studentId, String courseId, LocalDate startDate, LocalDate endDate);

    // 检查某学生某天是否已打卡
    boolean existsByStudentIdAndAttendanceDate(String studentId, LocalDate date);
}