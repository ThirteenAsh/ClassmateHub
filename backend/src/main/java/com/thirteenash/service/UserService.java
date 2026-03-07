package com.thirteenash.service;

import com.thirteenash.dto.UserRegisterDTO;

public interface UserService {
    
    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册成功的用户ID
     */
    Integer register(UserRegisterDTO registerDTO);
}