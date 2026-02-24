package com.thirteenash.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级信息响应VO
 */
@Data
public class ClassInfoVO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}