package com.thirteenash.entity;

import lombok.Data;

/**
 * 学业信息
 */
@Data
public class EducationInfo {
    private VisibilityField school; // 学校
    private VisibilityField major; // 专业
    private VisibilityField graduationDestination; // 毕业去向
}