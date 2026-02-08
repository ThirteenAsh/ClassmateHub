package com.thirteenash.dto;

import lombok.Data;

/**
 * 创建班级请求DTO
 */
@Data
public class CreateClassRequestDTO {
    private String name; // 班级名称
    private String description; // 班级描述
    private Integer startYear; // 开始年份
    private Integer endYear; // 结束年份
}