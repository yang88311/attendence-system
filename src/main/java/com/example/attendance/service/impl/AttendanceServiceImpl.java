package com.example.attendance.service.impl;

import com.example.attendance.entity.Attendance;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.service.AttendanceService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

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
    public Page<Attendance> searchAttendances(String studentId, String courseId,
                                              LocalDate startDate, LocalDate endDate,
                                              String status, Pageable pageable) {
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
}