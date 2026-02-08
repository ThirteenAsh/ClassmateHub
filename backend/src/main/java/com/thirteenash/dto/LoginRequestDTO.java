package com.thirteenash.dto;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}