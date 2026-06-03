package com.example.attendance.service.impl;

import com.example.attendance.dto.ImportResult;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.temporal.ChronoUnit;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    @Override
    public Attendance getAttendanceById(Integer id) {
        return attendanceRepository.findById(id).orElse(null);
    }

    @Override
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public void deleteAttendance(Integer id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public List<Attendance> getAttendancesByStudent(String studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public List<Attendance> getAttendancesByCourse(String courseId) {
        return attendanceRepository.findByCourseId(courseId);
    }

    @Override
    public List<Attendance> getAttendancesByCourseAndDate(String courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndAttendanceDate(courseId, date);
    }

    @Override
    public Page<Attendance> getAttendancesWithPagination(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public Page<Attendance> getAttendancesByStudentWithPagination(String studentId, Pageable pageable) {
        return attendanceRepository.findByStudentId(studentId, pageable);
    }

    @Override
    public Page<Attendance> searchAttendances(String studentId, String courseId, LocalDate startDate, LocalDate endDate, String status, Pageable pageable) {
        Specification<Attendance> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (studentId != null && !studentId.isEmpty()) {
                predicates.add(cb.equal(root.get("studentId"), studentId));
            }
            if (courseId != null && !courseId.isEmpty()) {
                predicates.add(cb.equal(root.get("courseId"), courseId));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return attendanceRepository.findAll(spec, pageable);
    }

    // ========== 打卡功能（带座位号） ==========
    @Override
    public Map<String, Object> checkIn(String studentId, String courseId, String remark, Byte seatRow, Byte seatCol) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        boolean alreadyChecked = attendanceRepository.existsByStudentIdAndAttendanceDate(studentId, today);
        if (alreadyChecked) {
            result.put("success", false);
            result.put("message", "今天已经打过卡了！");
            result.put("status", "DUPLICATE");
            return result;
        }

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            result.put("success", false);
            result.put("message", "课程不存在！");
            result.put("status", "ERROR");
            return result;
        }

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            result.put("success", false);
            result.put("message", "学生不存在！");
            result.put("status", "ERROR");
            return result;
        }

        // 临时：任何时间都能打卡
        LocalTime classStartTime = LocalTime.now().minusHours(1);
        LocalTime checkInTime = now.toLocalTime();
        LocalTime earliestCheckIn = classStartTime.minusMinutes(15);
        LocalTime latestCheckIn = classStartTime.plusMinutes(30);

        if (checkInTime.isBefore(earliestCheckIn)) {
            result.put("success", false);
            result.put("message", "打卡时间未到！课程开始前15分钟才能开始打卡。");
            result.put("status", "TOO_EARLY");
            return result;
        }

        if (checkInTime.isAfter(latestCheckIn)) {
            result.put("success", false);
            result.put("message", "打卡时间已过！课程开始后30分钟内才能打卡。");
            result.put("status", "TOO_LATE");
            return result;
        }

        String status = checkInTime.isAfter(classStartTime) ? "LATE" : "NORMAL";

        Attendance attendance = new Attendance();
        attendance.setStudentId(studentId);
        attendance.setStudentName(student.getStudentName());
        attendance.setCourseId(courseId);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(now);
        attendance.setSeatRow(seatRow);
        attendance.setSeatCol(seatCol);
        attendance.setStatus(status);
        attendance.setCreateTime(now);
        attendance.setRemark(remark);

        attendanceRepository.save(attendance);

        result.put("success", true);
        result.put("message", status.equals("LATE") ? "打卡成功，但已迟到！" : "打卡成功！");
        result.put("status", status);
        return result;
    }

    // ========== 筛选功能 ==========
    @Override
    public Page<Attendance> filterAttendances(String studentId, String courseId, String dateRange,
                                              LocalDate startDate, LocalDate endDate,
                                              String status, Pageable pageable) {
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

        if (filterStartDate == null) filterStartDate = LocalDate.now().minusYears(1);
        if (filterEndDate == null) filterEndDate = LocalDate.now();

        if (courseId != null && !courseId.isEmpty()) {
            return attendanceRepository.findByStudentIdAndCourseIdAndAttendanceDateBetween(
                    studentId, courseId, filterStartDate, filterEndDate, pageable);
        } else {
            return attendanceRepository.findByStudentIdAndAttendanceDateBetween(
                    studentId, filterStartDate, filterEndDate, pageable);
        }
    }

    // ========== 导出功能 ==========
    @Override
    public List<Attendance> getAttendancesForExport(String studentId, String courseId,
                                                    LocalDate startDate, LocalDate endDate,
                                                    String status) {
        if (courseId != null && !courseId.isEmpty()) {
            return attendanceRepository.findByStudentIdAndCourseIdAndAttendanceDateBetween(
                    studentId, courseId, startDate, endDate);
        } else {
            return attendanceRepository.findByStudentIdAndAttendanceDateBetween(
                    studentId, startDate, endDate, Pageable.unpaged()).getContent();
        }
    }

    // ========== 批量导入功能 ==========
    @Override
    public ImportResult importFromExcel(MultipartFile file) throws IOException {
        ImportResult result = new ImportResult();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String studentId = getCellValue(row.getCell(0));
                    String courseId = getCellValue(row.getCell(1));
                    String attendanceDateStr = getCellValue(row.getCell(2));
                    String status = getCellValue(row.getCell(3));
                    String remark = getCellValue(row.getCell(4));

                    if (studentId.isEmpty()) {
                        result.incrementFail();
                        result.addError("第" + (i+1) + "行：学号不能为空");
                        continue;
                    }
                    if (courseId.isEmpty()) {
                        result.incrementFail();
                        result.addError("第" + (i+1) + "行：课程ID不能为空");
                        continue;
                    }

                    if (!studentRepository.existsById(studentId)) {
                        result.incrementFail();
                        result.addError("第" + (i+1) + "行：学号 " + studentId + " 不存在");
                        continue;
                    }

                    if (!courseRepository.existsById(courseId)) {
                        result.incrementFail();
                        result.addError("第" + (i+1) + "行：课程ID " + courseId + " 不存在");
                        continue;
                    }

                    LocalDate attendanceDate = parseDate(attendanceDateStr);
                    if (attendanceDate == null) {
                        result.incrementFail();
                        result.addError("第" + (i+1) + "行：日期格式错误");
                        continue;
                    }

                    Student student = studentRepository.findById(studentId).orElse(null);
                    String studentName = student != null ? student.getStudentName() : "";

                    Attendance attendance = new Attendance();
                    attendance.setStudentId(studentId);
                    attendance.setStudentName(studentName);
                    attendance.setCourseId(courseId);
                    attendance.setAttendanceDate(attendanceDate);
                    attendance.setCheckInTime(attendanceDate.atTime(8, 0));
                    attendance.setStatus(status != null && !status.isEmpty() ? status : "NORMAL");
                    attendance.setRemark(remark);
                    attendance.setCreateTime(LocalDateTime.now());

                    attendanceRepository.save(attendance);
                    result.incrementSuccess();

                } catch (Exception e) {
                    result.incrementFail();
                    result.addError("第" + (i+1) + "行：解析失败 - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            throw new IOException("读取Excel文件失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public void exportErrorReport(List<String> errors, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=import_errors.txt");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("考勤记录导入失败报告");
            writer.println("==================");
            writer.println("共 " + errors.size() + " 条错误：");
            writer.println();
            for (String error : errors) {
                writer.println(error);
            }
        }
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("考勤导入模板");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "课程ID", "考勤日期", "状态", "备注"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("42411045");
            exampleRow.createCell(1).setCellValue("CS2101");
            exampleRow.createCell(2).setCellValue("2026-05-27");
            exampleRow.createCell(3).setCellValue("NORMAL");
            exampleRow.createCell(4).setCellValue("正常打卡");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=attendance_template.xlsx");

            workbook.write(response.getOutputStream());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // 处理 Excel 日期（可能返回数字或带时间）
                    try {
                        LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                        return date.toString();
                    } catch (Exception e) {
                        // 兼容旧版本 POI
                        java.util.Date date = cell.getDateCellValue();
                        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
                    }
                }
                // 普通数字
                long longVal = (long) cell.getNumericCellValue();
                if (cell.getNumericCellValue() == longVal) {
                    return String.valueOf(longVal);
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        // 处理 Excel 导入的日期（数字形式，如 45234 代表某个日期）
        if (dateStr.matches("\\d+")) {
            try {
                long excelDate = Long.parseLong(dateStr);
                return LocalDate.of(1900, 1, 1).plusDays(excelDate - 2);
            } catch (Exception ignored) {}
        }

        String[] patterns = {
                "yyyy-MM-dd",           // 2026-05-27
                "yyyy/MM/dd",           // 2026/05/27
                "yyyy/M/d",             // 2026/5/27
                "yyyyMMdd",             // 20260527
                "yyyy年M月d日",          // 2026年5月27日
                "M/d/yyyy",             // 5/27/2026
                "d/M/yyyy",             // 27/5/2026
                "yyyy-MM-dd HH:mm:ss",  // 2026-05-27 10:30:00
                "yyyy/MM/dd HH:mm:ss"   // 2026/05/27 10:30:00
        };
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {}
        }
        return null;
    }
}