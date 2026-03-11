package com.thirteenash.common.exception;

public class ClassException extends RuntimeException {


    private Integer code;

    public ClassException(String message) {
        super(message);
        this.code = 401;
    }

    public ClassException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }
}