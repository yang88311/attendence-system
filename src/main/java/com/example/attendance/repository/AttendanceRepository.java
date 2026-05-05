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
}