package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班级同学统计DTO（用于Mapper查询结果）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassStudentCountDTO {
    
    /**
     * 班级ID
     */
    private Long clazzId;
    
    /**
     * 班级名称
     */
    private String clazzName;
    
    /**
     * 同学人数
     */
    private Long studentCount;
}
