package com.thirteenash.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生列表分页查询DTO（支持按姓名、性别、班级过滤）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentPageQueryDTO {
    private Integer page;
    private Integer size;
    private String name;
    private String gender;
    private Long classId;
}
