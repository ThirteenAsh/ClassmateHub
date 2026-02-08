package com.thirteenash.dto;

import lombok.Data;

/**
 * 重置密码请求DTO
 */
@Data
public class ResetPasswordRequestDTO {
    private String newPassword;
}