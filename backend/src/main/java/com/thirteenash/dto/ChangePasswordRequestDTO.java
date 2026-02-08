package com.thirteenash.dto;

import lombok.Data;

/**
 * 修改密码请求DTO
 */
@Data
public class ChangePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;
}