package com.thirteenash.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体类
 */
@Data
public class Clazz {
    private Long id;
    
    private String name; // 班级名称
    
    private String description; // 班级描述
    
    private Integer startYear; // 开始年份
    
    private Integer endYear; // 结束年份
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}