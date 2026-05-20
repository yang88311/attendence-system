package com.example.attendance.service;

import com.example.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    List<Attendance> getAllAttendances();

    Attendance getAttendanceById(Integer id);

    Attendance saveAttendance(Attendance attendance);

    void deleteAttendance(Integer id);

    List<Attendance> getAttendancesByStudent(String studentId);

    List<Attendance> getAttendancesByCourse(String courseId);

    List<Attendance> getAttendancesByCourseAndDate(String courseId, LocalDate date);

    Page<Attendance> getAttendancesWithPagination(Pageable pageable);

    Page<Attendance> getAttendancesByStudentWithPagination(String studentId, Pageable pageable);

    Page<Attendance> searchAttendances(String studentId, String courseId, LocalDate startDate, LocalDate endDate, String status, Pageable pageable);

    // 打卡（带座位号）
    Map<String, Object> checkIn(String studentId, String courseId, String remark, Byte seatRow, Byte seatCol);

    Page<Attendance> filterAttendances(String studentId, String courseId, String dateRange,
                                       LocalDate startDate, LocalDate endDate,
                                       String status, Pageable pageable);

    List<Attendance> getAttendancesForExport(String studentId, String courseId,
                                             LocalDate startDate, LocalDate endDate,
                                             String status);
}