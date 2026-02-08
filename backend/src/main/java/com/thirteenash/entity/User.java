package com.thirteenash.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    
    private String username;
    
    private String password;
    
    private String email;
    
    private String phone;
    
    private String realName;
    
    private String studentId;
    
    private Integer age;
    
    private String gender;
    
    private String avatar;
    
    private String role; // ADMIN, STUDENT
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String status; // NORMAL, DISABLED
}