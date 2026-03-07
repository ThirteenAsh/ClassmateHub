package com.thirteenash.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

/**
 * SaToken 登录认证拦截器
 */
public class SaTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 校验当前用户是否登录
            StpUtil.checkLogin();
            return true;
        } catch (NotLoginException e) {
            // 未登录，重定向到登录页面
            handleNotLogin(request, response);
            return false;
        }
    }

    /**
     * 处理未登录情况
     */
    private void handleNotLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 判断请求类型
        String contentType = request.getContentType();

        // AJAX 请求处理（返回 JSON）
        if (isAjaxRequest(request)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return;
        }

        // 普通页面请求，重定向到登录页
        response.sendRedirect(request.getContextPath() + "/auth/login");
    }

    /**
     * 判断是否为 AJAX 请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith);
    }
}