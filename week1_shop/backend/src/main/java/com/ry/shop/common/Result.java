package com.ry.shop.common;

/**
 * 统一返回体：所有接口都包一层 { code, msg, data }
 * 前端约定：code == 0 表示成功
 */
public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /** 成功，带数据 */
    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "success", data);
    }

    /** 成功，无数据 */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /** 失败 */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
}
