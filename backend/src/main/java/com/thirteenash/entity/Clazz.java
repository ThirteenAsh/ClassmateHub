package com.thirteenash.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级实体类
 */
@Data
public class Clazz {
    private Long id;
    
    private String name;
    
    private String description;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}