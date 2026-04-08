-- =============================================
-- 考勤系统数据库设计 - 完整脚本
-- 姓名：白景一
-- 学号：42411045
-- 班级：计算机科学与技术2班
-- =============================================

-- 1. 创建数据库
CREATE DATABASE attendance_system;
GO

USE attendance_system;
GO

-- 2. 创建用户表
CREATE TABLE [user] (
                        id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'TEACHER',
    create_time DATETIME DEFAULT GETDATE(),
    -- 补充字段（作业1）
    email VARCHAR(100) NULL,
    phone VARCHAR(20) NULL,
    status TINYINT DEFAULT 1,
    last_login_time DATETIME NULL,
    remark VARCHAR(200) NULL
    );
GO

-- 3. 创建学生表
CREATE TABLE student (
                         student_id VARCHAR(20) PRIMARY KEY,
                         student_name VARCHAR(50) NOT NULL,
                         gender CHAR(2),
                         class_name VARCHAR(50) NOT NULL,
                         phone VARCHAR(20),
                         email VARCHAR(100),
                         create_time DATETIME DEFAULT GETDATE()
);
GO

-- 4. 创建课程表
CREATE TABLE course (
                        course_id VARCHAR(20) PRIMARY KEY,
                        course_name VARCHAR(100) NOT NULL,
                        class_name VARCHAR(50) NOT NULL,
                        teacher_id BIGINT NOT NULL,
                        classroom_name VARCHAR(50),
                        rows TINYINT,
                        cols TINYINT,
                        exclude_seats VARCHAR(200),
                        weekday TINYINT,
                        start_week INT,
                        end_week INT,
                        create_time DATETIME DEFAULT GETDATE(),
                        FOREIGN KEY (teacher_id) REFERENCES [user](id)
);
GO

-- 5. 创建选课表
CREATE TABLE course_selection (
                                  id INT IDENTITY(1,1) PRIMARY KEY,
                                  student_id VARCHAR(20) NOT NULL,
                                  course_id VARCHAR(20) NOT NULL,
                                  select_time DATETIME DEFAULT GETDATE(),
                                  FOREIGN KEY (student_id) REFERENCES student(student_id),
                                  FOREIGN KEY (course_id) REFERENCES course(course_id),
                                  CONSTRAINT UQ_student_course UNIQUE (student_id, course_id)
);
GO

-- 6. 创建考勤记录表
CREATE TABLE attendance (
                            id INT IDENTITY(1,1) PRIMARY KEY,
                            student_id VARCHAR(20) NOT NULL,
                            course_id VARCHAR(20) NOT NULL,
                            attendance_date DATE NOT NULL,
                            check_in_time DATETIME,
                            seat_row TINYINT,
                            seat_col TINYINT,
                            status VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
                            create_time DATETIME DEFAULT GETDATE(),
                            FOREIGN KEY (student_id) REFERENCES student(student_id),
                            FOREIGN KEY (course_id) REFERENCES course(course_id)
);
GO

-- 7. 插入用户表数据（作业2）
INSERT INTO [user] (username, password, real_name, role, email, phone, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtKZQ3m', '系统管理员', 'ADMIN', 'admin@school.com', '13800000000', 1),
('zhangwei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtKZQ3m', '张伟', 'TEACHER', 'zhangwei@school.com', '13800000001', 1),
('lili', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtKZQ3m', '李丽', 'TEACHER', 'lili@school.com', '13800000002', 1),
('wangfang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAtKZQ3m', '王芳', 'TEACHER', 'wangfang@school.com', '13800000003', 1);
GO

-- 8. 插入学生表数据
INSERT INTO student (student_id, student_name, gender, class_name, phone, email) VALUES
('42411045', '白景一', '男', '计算机科学与技术2班', '13800138001', 'bai.jingyi@example.com'),
('2024002', '李思琪', '女', '计算机科学与技术2班', '13800138002', 'li.siqi@example.com'),
('2024003', '王浩', '男', '计算机科学与技术2班', '13800138003', 'wang.hao@example.com'),
('2024004', '赵雨桐', '女', '计算机科学与技术2班', '13800138004', 'zhao.yutong@example.com'),
('2024005', '陈子轩', '男', '计算机科学与技术2班', '13800138005', 'chen.zixuan@example.com');
GO

-- 9. 插入课程表数据
INSERT INTO course (course_id, course_name, class_name, teacher_id, classroom_name, rows, cols, exclude_seats, weekday, start_week, end_week) VALUES
('CS2101', '数据库系统原理', '计算机科学与技术2班', 2, 'A-101', 8, 10, '1,5;2,6;3,7', 2, 1, 16),
('CS2102', 'Java程序设计', '计算机科学与技术2班', 3, 'B-203', 10, 12, '8,9;8,10', 4, 1, 16);
GO

-- 10. 插入选课表数据
INSERT INTO course_selection (student_id, course_id) VALUES
('42411045', 'CS2101'),
('2024002', 'CS2101'),
('2024003', 'CS2101'),
('2024004', 'CS2101'),
('2024005', 'CS2101'),
('42411045', 'CS2102'),
('2024002', 'CS2102'),
('2024003', 'CS2102');
GO

-- 11. 插入考勤表数据
INSERT INTO attendance (student_id, course_id, attendance_date, check_in_time, seat_row, seat_col, status) VALUES
('42411045', 'CS2101', '2024-09-10', '2024-09-10 08:55:00', 3, 5, 'NORMAL'),
('2024002', 'CS2101', '2024-09-10', '2024-09-10 09:05:00', 4, 6, 'LATE'),
('2024003', 'CS2101', '2024-09-10', '2024-09-10 08:50:00', 2, 4, 'NORMAL'),
('2024004', 'CS2101', '2024-09-10', '2024-09-10 08:58:00', 5, 7, 'NORMAL'),
('2024005', 'CS2101', '2024-09-10', NULL, NULL, NULL, 'ABSENT');
GO

-- 12. 验证数据
SELECT '=== 用户表 ===' AS '';
SELECT id, username, real_name, role, email, phone, status FROM [user];
GO

SELECT '=== 学生表 ===' AS '';
SELECT * FROM student;
GO

SELECT '=== 课程表 ===' AS '';
SELECT * FROM course;
GO

SELECT '=== 选课表 ===' AS '';
SELECT * FROM course_selection;
GO

SELECT '=== 考勤表 ===' AS '';
SELECT * FROM attendance;
GO

PRINT '=========================================';
PRINT '数据库创建完成！';
PRINT '姓名：白景一';
PRINT '学号：42411045';
PRINT '=========================================';
GO