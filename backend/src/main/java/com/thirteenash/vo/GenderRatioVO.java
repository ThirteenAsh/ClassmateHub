package com.thirteenash.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 性别比例VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenderRatioVO {
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 数量
     */
    private Long count;
    
    /**
     * 占比百分比
     */
    private Double ratio;
}
