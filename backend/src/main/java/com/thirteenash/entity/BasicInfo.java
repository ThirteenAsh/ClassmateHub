package com.thirteenash.entity;

import lombok.Data;

/**
 * 基础信息
 */
@Data
public class BasicInfo {
    private VisibilityField name; // 姓名
    private VisibilityField gender; // 性别
    private VisibilityField birthday; // 生日
    private VisibilityField hometown; // 籍贯
}