package com.thirteenash.dto;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String studentId;
}