package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 分页查询（支持排序）
    @GetMapping("/page")
    public Map<String, Object> getAttendancesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "attendanceDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> attendancePage = attendanceService.getAttendancesWithPagination(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", attendancePage.getContent());
        response.put("totalElements", attendancePage.getTotalElements());
        response.put("totalPages", attendancePage.getTotalPages());
        response.put("currentPage", attendancePage.getNumber());
        response.put("pageSize", attendancePage.getSize());
        return response;
    }

    // 多条件动态查询（分页+排序）
    @GetMapping("/search")
    public Map<String, Object> searchAttendances(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "attendanceDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> attendancePage = attendanceService.searchAttendances(
                studentId, courseId, startDate, endDate, status, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", attendancePage.getContent());
        response.put("totalElements", attendancePage.getTotalElements());
        response.put("totalPages", attendancePage.getTotalPages());
        response.put("currentPage", attendancePage.getNumber());
        response.put("pageSize", attendancePage.getSize());

        // 使用 HashMap 允许 null 值
        Map<String, Object> filters = new HashMap<>();
        filters.put("studentId", studentId);
        filters.put("courseId", courseId);
        filters.put("startDate", startDate);
        filters.put("endDate", endDate);
        filters.put("status", status);
        response.put("filters", filters);

        return response;
    }

    // 按学生ID分页查询
    @GetMapping("/student/{studentId}/page")
    public Map<String, Object> getByStudentWithPage(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "attendanceDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attendance> attendancePage = attendanceService.getAttendancesByStudentWithPagination(studentId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", attendancePage.getContent());
        response.put("totalElements", attendancePage.getTotalElements());
        response.put("totalPages", attendancePage.getTotalPages());
        response.put("currentPage", attendancePage.getNumber());
        return response;
    }

    // 原有接口：查询所有
    @GetMapping
    public List<Attendance> getAllAttendances() {
        return attendanceService.getAllAttendances();
    }

    // 原有接口：根据ID查询
    @GetMapping("/{id}")
    public Attendance getAttendanceById(@PathVariable Integer id) {
        return attendanceService.getAttendanceById(id);
    }

    // 原有接口：新增
    @PostMapping
    public Attendance addAttendance(@RequestBody Attendance attendance) {
        return attendanceService.saveAttendance(attendance);
    }

    // 原有接口：更新
    @PutMapping
    public Attendance updateAttendance(@RequestBody Attendance attendance) {
        return attendanceService.saveAttendance(attendance);
    }

    // 原有接口：删除
    @DeleteMapping("/{id}")
    public String deleteAttendance(@PathVariable Integer id) {
        attendanceService.deleteAttendance(id);
        return "删除成功";
    }

    // 原有接口：按学生ID查询
    @GetMapping("/student/{studentId}")
    public List<Attendance> getByStudent(@PathVariable String studentId) {
        return attendanceService.getAttendancesByStudent(studentId);
    }

    // 原有接口：按课程ID查询
    @GetMapping("/course/{courseId}")
    public List<Attendance> getByCourse(@PathVariable String courseId) {
        return attendanceService.getAttendancesByCourse(courseId);
    }
}