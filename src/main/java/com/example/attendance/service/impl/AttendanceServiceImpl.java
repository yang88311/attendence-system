package com.example.attendance.service.impl;

import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.Course;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.CourseRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
}