package com.example.attendance.controller;

import com.example.attendance.common.Result;
import com.example.attendance.entity.Attendance;
import com.example.attendance.entity.AttendanceRecord;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    // 考勤记录查询接口
    @GetMapping("/query")
    public Result<Attendance> queryAttendance(
            @RequestParam String studentId,
            @RequestParam String date
    ) {
        Attendance attendance = new Attendance();
        attendance.setStudentId(studentId);
        attendance.setDate(date);
        attendance.setStatus("正常");
        return Result.success(attendance);
    }

    // 考勤记录更新接口（JSON体参数）
    @PostMapping("/update")
    public Result<String> updateAttendance(@RequestBody AttendanceRecord record) {
        String message = "考勤成功：" + record.getStudentId() + " - " + record.getStatus();
        return Result.success(message);
    }
}