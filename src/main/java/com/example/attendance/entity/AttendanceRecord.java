package com.example.attendance.entity;

import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/attendance")
@Data
public class AttendanceRecord {
    private String studentId;
    private String status;
    private LocalDateTime checkTime;

}
