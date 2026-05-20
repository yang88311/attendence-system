package com.example.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "student_name", length = 50, nullable = false)
    private String studentName;

    @Column(name = "course_id", length = 20, nullable = false)
    private String courseId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "seat_row")
    private Byte seatRow;

    @Column(name = "seat_col")
    private Byte seatCol;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "remark", length = 200)
    private String remark;

    public Attendance() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public Byte getSeatRow() { return seatRow; }
    public void setSeatRow(Byte seatRow) { this.seatRow = seatRow; }

    public Byte getSeatCol() { return seatCol; }
    public void setSeatCol(Byte seatCol) { this.seatCol = seatCol; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}