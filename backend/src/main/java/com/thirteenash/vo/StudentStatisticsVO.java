package com.thirteenash.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 同学统计信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentStatisticsVO {
    
    /**
     * 同学总数
     */
    private Long totalCount;
    
    /**
     * 各班级同学数量
     */
    private List<ClassStatisticsVO> classStatistics;
    
    /**
     * 性别比例统计
     */
    private List<GenderRatioVO> genderRatios;
}
