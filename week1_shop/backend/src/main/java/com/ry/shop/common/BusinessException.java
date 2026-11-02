package com.ry.shop.common;

/**
 * 业务异常：可预期的错误（密码错、用户名已存在等）
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String msg) {
        this(400, msg);
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}