package com.example.attendance.service.impl;

import com.example.attendance.dao.StudentDao;
import com.example.attendance.entity.Student;
import com.example.attendance.service.StudentService;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDao studentDao;

    // 构造器注入（推荐，避免字段注入警告）
    public StudentServiceImpl(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    // 启动时自动添加测试数据
    @PostConstruct
    public void init() {
        studentDao.insert(new Student("42411045", "白景一", "计科2班", 20));
        studentDao.insert(new Student("42411046", "李四", "计科2班", 19));
        studentDao.insert(new Student("42411047", "王五", "计科1班", 21));
        System.out.println("===== 初始化测试数据完成 =====");
    }

    @Override
    public String createStudent(Student student) {
        // 业务校验：学号不能为空
        if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        // 业务校验：姓名不能为空
        if (student.getName() == null || student.getName().isEmpty()) {
            throw new RuntimeException("姓名不能为空");
        }

        studentDao.insert(student);
        return "创建成功";
    }

    @Override
    public Student getStudentById(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            throw new RuntimeException("学号不能为空");
        }
        return studentDao.findById(studentId);
    }

    @Override
    public List<Student> getStudentsByClass(String className) {
        if (className == null || className.isEmpty()) {
            throw new RuntimeException("班级名称不能为空");
        }
        return studentDao.findByClassName(className);
    }
}