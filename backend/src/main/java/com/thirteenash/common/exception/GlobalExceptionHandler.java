package com.thirteenash.common.exception;

import com.thirteenash.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 使用 Spring AOP 拦截控制器层抛出的各类异常，进行统一处理并返回标准化的响应结果。
 * 支持处理业务异常和系统异常，记录日志信息以便排查问题。
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //本页面注释均使用AI生成，请注意甄别！！！
    //本页面注释均使用AI生成，请注意甄别！！！
    /**
     * 处理业务异常
     * <p>
     * 捕获 {@link BusinessException} 类型的异常，返回带有错误码和错误消息的响应结果。
     * 记录完整的异常堆栈信息到日志，便于问题排查。
     * </p>
     *
     * @param e 业务异常对象，包含错误码和错误消息
     * @return 标准化的错误响应结果，包含错误码和错误消息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理系统异常
     * <p>
     * 捕获其他未被处理的 {@link Exception} 类型异常，返回通用的错误提示。
     * 记录完整的异常堆栈信息到日志，便于问题排查。
     * 为避免泄露敏感信息，向用户隐藏具体的错误详情。
     * </p>
     *
     * @param e 系统异常对象
     * @return 标准化的错误响应结果，包含通用错误提示
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}