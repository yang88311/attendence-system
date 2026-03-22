package com.example.attendance.entity;

public class Student {
    // 属性：学号、姓名、班级、年龄（使用包装类）
    private String studentId;
    private String name;
    private String className;
    private Integer age;
    // 无参构造器（必须）
    public Student() {}
    // 全参构造器（可选，方便创建对象）
    public Student(String studentId, String name, String className, Integer age) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.age = age;
    }
    // Getter 和 Setter 方法（手动编写，不用 Lombok）
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}