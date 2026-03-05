package com.thirteenash.common.exception;

/**
 * 业务异常类
 * <p>
 * 用于封装业务逻辑中发生的异常信息，包含错误码和错误消息。
 * 当业务规则不满足时，抛出此异常并通过全局异常处理器返回给前端。
 * </p>
 */
public class BusinessException extends RuntimeException {

    //本页面注释均使用AI生成，请注意甄别！！！
    //本页面注释均使用AI生成，请注意甄别！！！

    /**
     * 错误码
     * <p>
     * 用于标识具体的错误类型，默认为 500
     * </p>
     */
    private Integer code;

    /**
     * 构造业务异常
     * <p>
     * 使用默认错误码 500 初始化异常
     * </p>
     *
     * @param message 异常消息，描述具体的错误原因
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常
     * <p>
     * 使用指定的错误码和异常消息初始化异常
     * </p>
     *
     * @param code    错误码，用于标识具体的错误类型
     * @param message 异常消息，描述具体的错误原因
     */
    public BusinessException(Integer code, String message) {
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