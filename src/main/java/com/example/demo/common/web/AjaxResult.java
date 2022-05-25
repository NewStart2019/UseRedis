package com.example.demo.common.web;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 接口返回消息统一封装类
 *
 * @param <T>
 */
@Data
public class AjaxResult<T> {

    private Integer code;
    private String msg;
    private T data;


    public AjaxResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public AjaxResult(int code, String msg) {
        this(code, msg, null);
    }

    public AjaxResult(int code) {
        this(code, null, null);
    }


    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult(HttpStatus.OK.value(), msg, data);
    }

    public static <T> AjaxResult<T> success(String msg) {
        return AjaxResult.success(msg, null);
    }

    public static <T> AjaxResult<T> success(T data) {
        return AjaxResult.success("操作成功", data);
    }

    public static <T> AjaxResult<T> success() {
        return AjaxResult.success("操作成功");
    }


    public static <T> AjaxResult<T> error() {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg) {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> AjaxResult<T> error(String msg, T data) {
        return new AjaxResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static AjaxResult<Void> error(int code, String msg) {
        return new AjaxResult(code, msg, null);
    }

    public static <T> AjaxResult<T> error(int code, String msg, T data) {
        return new AjaxResult(code, msg, data);
    }
}
