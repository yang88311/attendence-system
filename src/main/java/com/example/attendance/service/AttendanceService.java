package com.example.attendance.service;

import com.example.attendance.dto.ImportResult;
import com.example.attendance.entity.Attendance;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    // 打卡功能
    Map<String, Object> checkIn(String studentId, String courseId, String remark, Byte seatRow, Byte seatCol);

    // 筛选功能
    Page<Attendance> filterAttendances(String studentId, String courseId, String dateRange,
                                       LocalDate startDate, LocalDate endDate,
                                       String status, Pageable pageable);

    // 导出功能
    List<Attendance> getAttendancesForExport(String studentId, String courseId,
                                             LocalDate startDate, LocalDate endDate,
                                             String status);

    // 批量导入功能
    ImportResult importFromExcel(MultipartFile file) throws IOException;

    void exportErrorReport(List<String> errors, HttpServletResponse response) throws IOException;

    void downloadTemplate(HttpServletResponse response) throws IOException;
}