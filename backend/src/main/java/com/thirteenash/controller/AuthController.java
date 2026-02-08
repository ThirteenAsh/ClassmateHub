package com.thirteenash.controller;

import com.thirteenash.common.response.Result;
import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.LoginRequestDTO;
import com.thirteenash.dto.RegisterRequestDTO;
import com.thirteenash.service.IUserService;
import com.thirteenash.vo.LoginResponseVO;
import com.thirteenash.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponseVO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseVO response = userService.login(loginRequestDTO);
        return Result.success(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        Boolean success = userService.register(registerRequestDTO);
        return Result.success(success);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserInfoVO> getUserProfile(@RequestAttribute("userId") Long userId) {
        UserInfoVO userInfo = userService.getUserById(userId);
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     */
    @PostMapping("/password/change")
    public Result<Boolean> changePassword(@RequestAttribute("userId") Long userId, @RequestBody ChangePasswordRequestDTO requestDTO) {
        Boolean success = userService.changePassword(userId, requestDTO);
        return Result.success(success);
    }
}