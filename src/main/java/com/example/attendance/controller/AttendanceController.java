package com.example.attendance.controller;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.CourseService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    private String getCurrentStudentId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @GetMapping("/checkIn")
    public String checkInPage(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "attendance-checkin";
    }

    @PostMapping("/checkIn")
    @ResponseBody
    public Map<String, Object> checkIn(@RequestParam String courseId,
                                       @RequestParam(required = false) String remark,
                                       @RequestParam(required = false) Byte seatRow,
                                       @RequestParam(required = false) Byte seatCol) {
        String studentId = getCurrentStudentId();
        return attendanceService.checkIn(studentId, courseId, remark, seatRow, seatCol);
    }

    @GetMapping("/list")
    public String list(@RequestParam(required = false) String courseId,
                       @RequestParam(required = false) String dateRange,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                       @RequestParam(required = false) String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        String studentId = getCurrentStudentId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("attendanceDate").descending());

        Page<Attendance> attendancePage = attendanceService.filterAttendances(
                studentId, courseId, dateRange, startDate, endDate, status, pageable);

        List<Course> courses = courseService.getAllCourses();

        model.addAttribute("records", attendancePage.getContent());
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("dateRange", dateRange);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", attendancePage.getTotalPages());
        model.addAttribute("totalElements", attendancePage.getTotalElements());
        model.addAttribute("size", size);

        return "attendance-list";
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) String courseId,
                       @RequestParam(required = false) String dateRange,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                       @RequestParam(required = false) String status,
                       HttpServletResponse response) throws IOException {

        String studentId = getCurrentStudentId();

        LocalDate filterStartDate = startDate;
        LocalDate filterEndDate = endDate;

        if (dateRange != null) {
            LocalDate today = LocalDate.now();
            switch (dateRange) {
                case "today":
                    filterStartDate = today;
                    filterEndDate = today;
                    break;
                case "week":
                    filterStartDate = today.minusDays(7);
                    filterEndDate = today;
                    break;
                case "month":
                    filterStartDate = today.minusMonths(1);
                    filterEndDate = today;
                    break;
            }
        }

        List<Attendance> records = attendanceService.getAttendancesForExport(
                studentId, courseId, filterStartDate, filterEndDate, status);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=attendance_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

        PrintWriter writer = response.getWriter();
        writer.println("日期,课程ID,打卡时间,座位号,状态,备注");

        for (Attendance record : records) {
            String dateStr = record.getAttendanceDate().toString();
            String timeStr = record.getCheckInTime() != null ?
                    record.getCheckInTime().toLocalTime().toString() : "";
            String seatStr = "";
            if (record.getSeatRow() != null && record.getSeatCol() != null) {
                seatStr = record.getSeatRow() + "排" + record.getSeatCol() + "列";
            }
            String statusText = "";
            switch (record.getStatus()) {
                case "NORMAL": statusText = "正常"; break;
                case "LATE": statusText = "迟到"; break;
                default: statusText = record.getStatus();
            }
            String remark = record.getRemark() != null ? record.getRemark() : "";

            writer.printf("%s,%s,%s,%s,%s,%s%n",
                    dateStr, record.getCourseId(), timeStr, seatStr, statusText, remark);
        }
        writer.flush();
        writer.close();
    }
}