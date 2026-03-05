package com.thirteenash.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班级统计信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassStatisticsVO {
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 同学人数
     */
    private Long studentCount;
}
