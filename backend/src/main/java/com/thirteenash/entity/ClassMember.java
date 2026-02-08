package com.thirteenash.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 班级成员关系实体类
 */
@Data
public class ClassMember {
    private Long id;
    private Long classId; // 班级ID
    private Long userId; // 用户ID
    private LocalDateTime joinTime; // 加入时间
}