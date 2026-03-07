package com.thirteenash.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.thirteenash.common.response.Result;
import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.UserLoginRequestDTO;
import com.thirteenash.dto.UserRegisterDTO;
import com.thirteenash.vo.UserLoginInfo;
import com.thirteenash.service.AuthService;
import com.thirteenash.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @SaIgnore
    public Result login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {

        log.info("登录：{}", userLoginRequestDTO);

        try {
            UserLoginInfo userLoginInfo = authService.login(userLoginRequestDTO);
            log.info("Service返回的loginInfo: {}", userLoginInfo);

            if(userLoginInfo != null){
                Result result = Result.success(userLoginInfo);
                log.info("当前Token: {}", StpUtil.getTokenValue());
                log.info("返回成功Result: {}", result);
                return result;
            }else{
                Result result = Result.error("用户名/密码错误！");
                log.info("返回失败Result: {}", result);
                return result;
            }
        } catch (Exception e) {
            log.error("登录异常: ", e);
            return Result.error("系统异常");
        }
    }

    /**
     * 用户注册接口
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    @SaIgnore
    public Result<Map<String, Object>> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册请求：{}", registerDTO.getUsername());

        Integer userId = userService.register(registerDTO);

        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("username", registerDTO.getUsername());

        return Result.success("注册成功", data);
    }

    @PostMapping("/logout")
    @SaCheckLogin
    public Result logout(HttpServletRequest request) {
        // 可以添加额外的验证逻辑
        Integer id = Integer.parseInt((String) StpUtil.getLoginId());

        // 执行登出
        StpUtil.logout();

        // 记录登出日志
        log.info("用户登出成功，id：{}", id);

        return Result.success("退出成功");
    }

    @PostMapping("/change-password")
    @SaCheckLogin
    public Result changePassword(@RequestBody ChangePasswordRequestDTO requestDTO) {
        log.info("修改密码请求，userId：{}", StpUtil.getLoginId());

        Long userId = StpUtil.getLoginIdAsLong();
        authService.changePassword(userId, requestDTO);

        log.info("修改密码成功，userId：{}", userId);
        return Result.success("修改密码成功");
    }

}
