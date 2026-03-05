package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 性别统计DTO（用于Mapper查询结果）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenderCountDTO {
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 人数
     */
    private Long count;
}
