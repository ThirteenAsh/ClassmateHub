package com.thirteenash.vo;

import lombok.Data;

/**
 * 登录响应VO
 */
@Data
public class LoginResponseVO {
    private String token;
    private Integer expiresIn; // 过期时间（秒）
    private UserInfoVO user; // 用户信息
}