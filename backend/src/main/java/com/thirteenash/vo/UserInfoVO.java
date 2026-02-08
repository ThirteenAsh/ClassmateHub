package com.thirteenash.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应VO
 */
@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String realName;
    private String studentId;
    private String role;
    private String avatar;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}