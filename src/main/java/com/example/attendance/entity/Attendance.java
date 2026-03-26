package com.example.attendance.entity;

// 考勤记录实体类（大驼峰类名，小驼峰属性名）
public class Attendance {
    // 属性：学号、日期、考勤状态（正常/迟到/缺勤）
    private String studentId;
    private String date;
    private String status;
    // 无参构造器（必须）
    public Attendance() {}
    // 全参构造器（方便模拟数据）
    public Attendance(String studentId, String date, String status) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
    }
    // Getter 和 Setter 方法（手动编写）
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}