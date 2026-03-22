package com.example.attendance.common;
public class Result<T> {
    private Integer code;   // 状态码
    private String msg;     // 响应消息
    private T data;         // 实际数据
    // 无参构造器
    public Result() {}
    // 全参构造器
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // Getter 和 Setter
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    // 静态成功响应方法
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    // 静态失败响应方法
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}