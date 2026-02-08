package com.thirteenash.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 同学信息档案实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfile {
    private Long id;
    
    private Long userId; // 关联用户ID
    
    private Long clazzId; // 班级ID
    
    private BasicInfo basicInfo; // 基础信息
    
    private ContactInfo contactInfo; // 联系方式
    
    private EducationInfo educationInfo; // 学业信息
    
    private PersonalInfo personalInfo; // 个性化信息
    
    private String avatar; // 头像
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}