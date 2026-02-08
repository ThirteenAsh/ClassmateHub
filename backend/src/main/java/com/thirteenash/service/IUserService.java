package com.thirteenash.service;

import com.thirteenash.dto.ChangePasswordRequestDTO;
import com.thirteenash.dto.LoginRequestDTO;
import com.thirteenash.dto.RegisterRequestDTO;
import com.thirteenash.dto.ResetPasswordRequestDTO;
import com.thirteenash.entity.User;
import com.thirteenash.vo.LoginResponseVO;
import com.thirteenash.vo.UserInfoVO;

/**
 * 用户服务接口
 */
public interface IUserService {
    /**
     * 用户登录
     */
    LoginResponseVO login(LoginRequestDTO loginRequestDTO);

    /**
     * 用户注册
     */
    Boolean register(RegisterRequestDTO registerRequestDTO);

    /**
     * 根据ID获取用户信息
     */
    UserInfoVO getUserById(Long userId);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 更新用户信息
     */
    Boolean updateUser(User user);

    /**
     * 修改密码
     */
    Boolean changePassword(Long userId, ChangePasswordRequestDTO requestDTO);

    /**
     * 重置密码（管理员）
     */
    Boolean resetPassword(Long userId, ResetPasswordRequestDTO requestDTO);
}