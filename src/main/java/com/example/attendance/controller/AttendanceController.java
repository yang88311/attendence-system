package com.example.attendance.controller;

import com.example.attendance.dto.ImportResult;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // ========== 打卡功能 ==========
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

    // ========== 考勤记录列表 ==========
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

    // ========== 导出CSV ==========
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
                default:
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

    // ========== 批量导入功能 ==========
    @GetMapping("/import")
    public String importPage() {
        return "attendance-import";
    }

    @PostMapping("/import")
    public String importFile(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择文件");
            return "redirect:/attendance/import";
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            redirectAttributes.addFlashAttribute("error", "请上传Excel文件（.xlsx或.xls）");
            return "redirect:/attendance/import";
        }

        try {
            ImportResult result = attendanceService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("success",
                    "导入完成！成功: " + result.getSuccessCount() + "条，失败: " + result.getFailCount() + "条");

            if (result.getFailCount() > 0) {
                redirectAttributes.addFlashAttribute("hasErrors", true);
                redirectAttributes.addFlashAttribute("errors", result.getErrorMessages());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "导入失败: " + e.getMessage());
        }

        return "redirect:/attendance/import";
    }

    @GetMapping("/export-error-report")
    public void exportErrorReport(@SessionAttribute(value = "errors", required = false) List<String> errors,
                                  HttpServletResponse response) throws IOException {
        if (errors == null || errors.isEmpty()) {
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().println("无错误记录");
            return;
        }
        attendanceService.exportErrorReport(errors, response);
    }

    @GetMapping("/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        attendanceService.downloadTemplate(response);
    }
}