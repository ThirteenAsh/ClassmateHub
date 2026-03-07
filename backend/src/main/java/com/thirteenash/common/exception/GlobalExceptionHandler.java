package com.thirteenash.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.thirteenash.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        // 获取第一个验证错误信息
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数验证失败：{}", errorMessage);
        return Result.error(400, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }

    @ExceptionHandler(NotLoginException.class)
    public Result handleNotLoginException(NotLoginException e) {
        return Result.error(401, "未登录，请先登录");
    }

    @ExceptionHandler(NotRoleException.class)
    public Result handleNotRoleException(NotRoleException e) {
        log.warn("权限异常：用户缺少所需角色");
        return Result.error(403, "权限不足，你没有该角色权限，无法执行此操作");
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result handleNotPermissionException(NotPermissionException e) {
        log.warn("权限异常：用户缺少所需权限");
        return Result.error(403, "权限不足，你没有该权限，无法执行此操作");
    }
}