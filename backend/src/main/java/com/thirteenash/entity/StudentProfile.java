package com.thirteenash.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfile {
    private Long id;
    
    private Long clazzId;
    
    private String name;
    
    private String gender;
    
    private LocalDate birthDate;
    
    private String phone;

    private String university;

    private String email;
    
    private String wechat;
    
    private String qq;
    
    private String message;
    
    private String avatar;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}